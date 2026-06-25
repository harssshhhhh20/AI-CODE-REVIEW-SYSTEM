package com.harsh.ai_code_review_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.ai_code_review_system.dto.*;
import com.harsh.ai_code_review_system.entity.PullRequest;
import com.harsh.ai_code_review_system.entity.Review;
import com.harsh.ai_code_review_system.entity.ReviewComment;
import com.harsh.ai_code_review_system.entity.User;
import com.harsh.ai_code_review_system.repository.PullRequestRepository;
import com.harsh.ai_code_review_system.repository.ReviewCommentRepository;
import com.harsh.ai_code_review_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.harsh.ai_code_review_system.dto.SeverityResponse;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewProcessingService {

    private final ReviewRepository reviewRepository;
    private final OpenRouterService openRouterService;
    private final ObjectMapper objectMapper;
    private final ReviewCommentRepository reviewCommentRepository;
    private final GithubService githubService;
    private final HashService hashService;
    private final DiffFilterService diffFilterService;
    private final ReviewCacheService reviewCacheService;
    private final RedisLockService redisLockService;
    private final RepositoryService repositoryService;
    private final UserService userService;
    private final PullRequestRepository pullRequestRepository;
    private final ReviewMetricsService reviewMetricsService;


    public void processReview(Long reviewId) {

        System.out.println("Processing review in thread: " + Thread.currentThread().getName());

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException("Review not found"));

        String diffHash = hashService.generateHash(
                review.getPullRequest().getDiff()
        );

        CachedReviewResponse cachedResponse = reviewCacheService.get(diffHash);

        if (cachedResponse != null) {
            System.out.println("Redis cache hit");

            review.setQualityScore(cachedResponse.qualityScore());
            review.setSummary(cachedResponse.summary());
            review.setRawResponse(cachedResponse.rawResponse());
            review.setResponseTimeMs(cachedResponse.responseTimeMs());
            review.setModelName(cachedResponse.modelName());
            review.setStatus("COMPLETED");
            review.setCacheHit(true);
            review.setDiffHash(diffHash);

            reviewRepository.save(review);
            reviewMetricsService.incrementCompleted();

            Review cachedReview = reviewRepository
                    .findTopByDiffHashAndStatusOrderByIdDesc(diffHash, "COMPLETED")
                    .orElse(null);

            if (cachedReview != null) {
                List<ReviewComment> comments =
                        reviewCommentRepository.findByReview(cachedReview);
                review.setComments(comments);
            }

            postGithubReview(review);
            return;
        }

        if (!redisLockService.acquireLock(diffHash)) {
            System.out.println("Another thread is already processing this review.");
            return;
        }

        try {

            Review cachedReview = reviewRepository
                    .findTopByDiffHashAndStatusOrderByIdDesc(
                            diffHash,
                            "COMPLETED"
                    )
                    .orElse(null);

            if (cachedReview != null
                    && !cachedReview.getId().equals(reviewId)) {

                System.out.println("Using cached review");

                review.setQualityScore(cachedReview.getQualityScore());
                review.setSummary(cachedReview.getSummary());
                review.setStatus("COMPLETED");
                review.setCacheHit(true);
                review.setDiffHash(diffHash);
                review.setRawResponse(cachedReview.getRawResponse());
                review.setModelName(cachedReview.getModelName());
                review.setResponseTimeMs(cachedReview.getResponseTimeMs());

                List<ReviewComment> cachedComments =
                        reviewCommentRepository.findByReview(cachedReview);

                review.setComments(cachedComments);

                reviewRepository.save(review);
                reviewMetricsService.incrementCompleted();

                reviewCacheService.save(
                        diffHash,
                        new CachedReviewResponse(
                                cachedReview.getQualityScore(),
                                cachedReview.getSummary(),
                                cachedReview.getRawResponse(),
                                cachedReview.getResponseTimeMs(),
                                cachedReview.getModelName()
                        )
                );

                postGithubReview(review);
                return;
            }

            try {

                System.out.println("Calling OpenRouter...");

                long startTime = System.currentTimeMillis();

                String filteredDiff =
                        diffFilterService.filterDiff(
                                review.getPullRequest().getDiff()
                        );

                String aiReview =
                        openRouterService.reviewPullRequest(
                                review.getPullRequest().getTitle(),
                                review.getPullRequest().getAuthor(),
                                filteredDiff
                        );

                long responseTime =
                        System.currentTimeMillis() - startTime;

                review.setResponseTimeMs(responseTime);
                review.setModelName("nvidia/nemotron-nano-9b-v2:free");
                review.setRawResponse(aiReview);

                AiReviewResult result =
                        objectMapper.readValue(
                                aiReview,
                                AiReviewResult.class
                        );

                review.setQualityScore(result.qualityScore());
                review.setSummary(result.summary());
                review.setStatus("COMPLETED");
                review.setCacheHit(false);
                review.setDiffHash(diffHash);

                reviewRepository.save(review);
                reviewMetricsService.incrementCompleted();

                reviewCacheService.save(
                        diffHash,
                        new CachedReviewResponse(
                                review.getQualityScore(),
                                review.getSummary(),
                                review.getRawResponse(),
                                review.getResponseTimeMs(),
                                review.getModelName()
                        )
                );

                List<ReviewComment> comments = new ArrayList<>();

                if (result.issues() != null) {
                    for (AiIssue issue : result.issues()) {

                        ReviewComment comment =
                                ReviewComment.builder()
                                        .severity(issue.severity())
                                        .comment(issue.comment())
                                        .fileName(issue.fileName())
                                        .lineNumber(issue.lineNumber())
                                        .review(review)
                                        .build();

                        reviewCommentRepository.save(comment);
                        comments.add(comment);
                    }
                }

                review.setComments(comments);

                postGithubReview(review);

            } catch (Exception e) {

                e.printStackTrace();

                review.setSummary(
                        "AI review failed: " + e.getMessage()
                );
                review.setCacheHit(false);
                review.setStatus("FAILED");
                review.setDiffHash(diffHash);

                reviewRepository.save(review);
                reviewMetricsService.incrementFailed();
            }

        } finally {
            redisLockService.releaseLock(diffHash);
        }
    }

    public ReviewResponse getReviewById(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Review Not Found"
                        ));
        repositoryService.getOwnedRepository(
                review.getPullRequest()
                        .getRepository()
                        .getId()
        );

        int highCount = (int) review.getComments()
                .stream()
                .filter(comment ->
                        "HIGH".equalsIgnoreCase(comment.getSeverity()))
                .count();

        int mediumCount = (int) review.getComments()
                .stream()
                .filter(comment ->
                        "MEDIUM".equalsIgnoreCase(comment.getSeverity()))
                .count();

        int lowCount = (int) review.getComments()
                .stream()
                .filter(comment ->
                        "LOW".equalsIgnoreCase(comment.getSeverity()))
                .count();

        List<ReviewCommentResponse> comments =
                review.getComments()
                        .stream()
                        .map(comment ->
                                new ReviewCommentResponse(
                                        comment.getSeverity(),
                                        comment.getFileName(),
                                        comment.getLineNumber(),
                                        comment.getComment()
                                ))
                        .toList();

        return new ReviewResponse(
                review.getId(),
                review.getQualityScore(),
                review.getStatus(),
                review.getSummary(),
                review.getPullRequest()
                        .getRepository()
                        .getName(),
                review.getCacheHit(),
                review.getResponseTimeMs(),
                review.getModelName(),
                highCount,
                mediumCount,
                lowCount,
                comments
        );
    }

    private void postGithubReview(Review review) {

        StringBuilder githubComment = new StringBuilder();

        githubComment.append("## AI Review\n\n");

        githubComment.append("Quality Score: ")
                .append(review.getQualityScore())
                .append("/100\n\n");

        githubComment.append("Summary:\n")
                .append(review.getSummary())
                .append("\n\n");

        if (review.getComments() != null) {

            for (ReviewComment comment : review.getComments()) {

                githubComment.append("### ")
                        .append(comment.getSeverity())
                        .append("\n");

                githubComment.append("File: ")
                        .append(comment.getFileName())
                        .append("\n");

                githubComment.append("Line: ")
                        .append(comment.getLineNumber())
                        .append("\n");

                githubComment.append(comment.getComment())
                        .append("\n\n");
            }
        }


        githubService.postComment(
                review.getPullRequest()
                        .getRepository()
                        .getOwner(),

                review.getPullRequest()
                        .getRepository()
                        .getName(),

                review.getPullRequest()
                        .getPrNumber(),

                githubComment.toString()
        );
    }
    public List<ReviewHistoryResponse> getReviewHistory(
            Long pullRequestId
    ) {

        PullRequest pullRequest =
                pullRequestRepository.findById(
                        pullRequestId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Pull request not found"
                        ));

        repositoryService.getOwnedRepository(
                pullRequest.getRepository().getId()
        );

        return reviewRepository
                .findByPullRequestIdOrderByIdDesc(
                        pullRequestId
                )
                .stream()
                .map(review -> new ReviewHistoryResponse(
                        review.getId(),
                        review.getQualityScore(),
                        review.getStatus(),
                        review.getCacheHit(),
                        review.getResponseTimeMs(),
                        review.getModelName()
                ))
                .toList();
    }
    public AnalyticsResponseSummary getAnalyticsSummary() {

        User user =
                userService.getCurrentAuthenticatedUser();

        Long userId = user.getId();

        long totalReviews =
                reviewRepository.countByPullRequestRepositoryUserId(userId);

        long cacheHits =
                reviewRepository
                        .countByPullRequestRepositoryUserIdAndCacheHitTrue(userId);

        List<Review> aiReviews =
                reviewRepository
                        .findByPullRequestRepositoryUserIdAndCacheHitFalse(userId);

        double averageResponseTime = 0.0;

        if (!aiReviews.isEmpty()) {
            averageResponseTime = aiReviews.stream()
                    .mapToLong(Review::getResponseTimeMs)
                    .average()
                    .orElse(0.0);
        }

        double cacheHitRate = 0.0;

        if (totalReviews > 0) {
            cacheHitRate =
                    (cacheHits * 100.0) / totalReviews;
        }

        return new AnalyticsResponseSummary(
                totalReviews,
                cacheHits,
                cacheHitRate,
                averageResponseTime,
                cacheHits
        );
    }
    public AnalyticsResponseSummary getAnalyticsSummary(
            Long repositoryId
    ) {

        repositoryService.getOwnedRepository(
                repositoryId
        );

        long totalReviews =
                reviewRepository.countByPullRequestRepositoryId(
                        repositoryId
                );

        long cacheHits =
                reviewRepository
                        .countByPullRequestRepositoryIdAndCacheHitTrue(
                                repositoryId
                        );

        List<Review> aiReviews =
                reviewRepository
                        .findByPullRequestRepositoryIdAndCacheHitFalse(
                                repositoryId
                        );

        double averageResponseTime = 0.0;

        if (!aiReviews.isEmpty()) {
            averageResponseTime =
                    aiReviews.stream()
                            .mapToLong(
                                    Review::getResponseTimeMs
                            )
                            .average()
                            .orElse(0.0);
        }

        double cacheHitRate = 0.0;

        if (totalReviews > 0) {
            cacheHitRate =
                    (cacheHits * 100.0)
                            / totalReviews;
        }

        return new AnalyticsResponseSummary(
                totalReviews,
                cacheHits,
                cacheHitRate,
                averageResponseTime,
                cacheHits
        );
    }
    public List<AnalyticsHistoryResponse> getAnalyticsHistory(
            Long repositoryId
    ) {
        repositoryService.getOwnedRepository(
                repositoryId
        );

        return reviewRepository
                .findByPullRequestRepositoryIdOrderByIdAsc(
                        repositoryId
                )
                .stream()
                .map(review -> new AnalyticsHistoryResponse(
                        review.getId(),
                        review.getQualityScore(),
                        review.getCacheHit(),
                        review.getResponseTimeMs(),
                        review.getCreatedAt()
                ))
                .toList();
    }
    public SeverityResponse getSeverityDistribution() {

        User user =
                userService.getCurrentAuthenticatedUser();
        Long userId = user.getId();

        return new SeverityResponse(
                reviewCommentRepository
                        .countByReviewPullRequestRepositoryUserIdAndSeverity(
                                userId,
                                "HIGH"
                        ),
                reviewCommentRepository
                        .countByReviewPullRequestRepositoryUserIdAndSeverity(
                                userId,
                                "MEDIUM"
                        ),
                reviewCommentRepository
                        .countByReviewPullRequestRepositoryUserIdAndSeverity(
                                userId,
                                "LOW"
                        )
        );
    }
    private ReviewCardReponse toReviewCardResponse(
            Review review
    ) {

        int highCount = (int) review.getComments()
                .stream()
                .filter(comment ->
                        "HIGH".equalsIgnoreCase(
                                comment.getSeverity()
                        ))
                .count();

        int mediumCount = (int) review.getComments()
                .stream()
                .filter(comment ->
                        "MEDIUM".equalsIgnoreCase(
                                comment.getSeverity()
                        ))
                .count();

        int lowCount = (int) review.getComments()
                .stream()
                .filter(comment ->
                        "LOW".equalsIgnoreCase(
                                comment.getSeverity()
                        ))
                .count();

        return new ReviewCardReponse(
                review.getId(),
                review.getPullRequest().getPrNumber(),
                review.getQualityScore(),
                review.getStatus(),
                review.getSummary(),
                review.getPullRequest().getRepository().getName(),
                highCount,
                mediumCount,
                lowCount,
                review.getCacheHit(),
                review.getResponseTimeMs(),
                review.getModelName(),
                review.getPullRequest()
                        .getRepository()
                        .getId()
        );
    }

    public List<ReviewCardReponse> getAllReviews() {

        User user =
                userService.getCurrentAuthenticatedUser();

        return reviewRepository
                .findByPullRequestRepositoryUserIdOrderByIdDesc(
                        user.getId()
                )
                .stream()
                .map(this::toReviewCardResponse)
                .toList();
    }

    public List<ReviewCardReponse> getReviewsByRepository(
            Long repositoryId
    ) {

        repositoryService.getOwnedRepository(
                repositoryId
        );

        return reviewRepository
                .findByPullRequestRepositoryIdOrderByIdDesc(
                        repositoryId
                )
                .stream()
                .map(this::toReviewCardResponse)
                .toList();
    }
}
