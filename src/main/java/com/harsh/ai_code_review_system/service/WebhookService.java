package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.dto.CreateGithubPullRequest;
import com.harsh.ai_code_review_system.dto.GithubWebhookPayload;
import com.harsh.ai_code_review_system.dto.PullRequestInfo;
import com.harsh.ai_code_review_system.dto.ReviewJob;
import com.harsh.ai_code_review_system.entity.CodeRepository;
import com.harsh.ai_code_review_system.entity.PullRequest;
import com.harsh.ai_code_review_system.entity.Review;
import com.harsh.ai_code_review_system.repository.CodeRepositoryRepository;
import com.harsh.ai_code_review_system.repository.PullRequestRepository;
import com.harsh.ai_code_review_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WebhookService {

    private final CodeRepositoryRepository codeRepositoryRepository;
    private final PullRequestRepository pullRequestRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewPublisher reviewPublisher;
    private final GithubService githubService;
    private final HashService hashService;

    @Transactional
    public void processPullRequestOpened(
            CreateGithubPullRequest event
    ) {
        CodeRepository repository =
                codeRepositoryRepository
                        .findById(event.repositoryId())
                        .orElseThrow(() ->
                                new RuntimeException("Repository not found"));

        String diff =
                githubService.getPullRequestDiff(
                        repository.getOwner(),
                        repository.getName(),
                        event.prNumber()
                );
        PullRequestInfo info =
                githubService.getPullRequestInfo(
                        repository.getOwner(),
                        repository.getName(),
                        event.prNumber()
                );

        System.out.println("Commit SHA: " + info.head().sha());
        String diffHash = hashService.generateHash(diff);

        PullRequest savedPullRequest =
                pullRequestRepository
                        .findByGithubPrId(event.githubPrId())
                        .orElseGet(() -> {

                            PullRequest pullRequest =
                                    PullRequest.builder()
                                            .githubPrId(event.githubPrId())
                                            .prNumber(event.prNumber())
                                            .title(event.title())
                                            .author(event.author())
                                            .status("OPEN")
                                            .diff(diff)
                                            .repository(repository)
                                            .build();

                            return pullRequestRepository.save(pullRequest);
                        });
        savedPullRequest.setPrNumber(event.prNumber());
        savedPullRequest.setDiff(diff);
        pullRequestRepository.save(savedPullRequest);
        Review review = Review.builder()
                .qualityScore(0)
                .summary("Review pending")
                .status("PENDING")
                .pullRequest(savedPullRequest)
                .diffHash(diffHash)
                .build();
        Review savedReview = reviewRepository.save(review);
        reviewRepository.flush();
        System.out.println("Publishing review: " + savedReview.getId());
        reviewPublisher.publish(
                new ReviewJob(
                        savedReview.getId(),
                        savedPullRequest.getId()
                )
        );
    }

    @Transactional
    public void processWebhook(GithubWebhookPayload payload) {
        if (!"opened".equals(payload.action())
                && !"synchronize".equals(payload.action())) {
            return;
        }
        String owner = payload.repository().owner().login();
        String repoName = payload.repository().name();

        Long githubPrId = payload.pull_request().id();
        Integer prNumber = payload.pull_request().number();
        String title = payload.pull_request().title();
        String author = payload.pull_request().user().login();

        CodeRepository repository =
                codeRepositoryRepository
                        .findByNameAndOwner(repoName, owner)
                        .orElseThrow(() ->
                                new RuntimeException("Repository not found"));

        CreateGithubPullRequest event =
                new CreateGithubPullRequest(
                        repository.getId(),
                        githubPrId,
                        prNumber,
                        title,
                        author,
                        null
                );
        processPullRequestOpened(event);

    }
}