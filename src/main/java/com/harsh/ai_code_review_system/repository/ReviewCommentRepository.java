package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.Review;
import com.harsh.ai_code_review_system.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.harsh.ai_code_review_system.dto.TopProblemFileResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewCommentRepository
        extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByReview(Review review);
    long countBySeverity(String severity);

    long countByReviewPullRequestRepositoryUserIdAndSeverity(
            Long userId,
            String severity
    );

    @Query("""
       SELECT new com.harsh.ai_code_review_system.dto.TopProblemFileResponse(
           rc.fileName,
           COUNT(rc)
       )
       FROM ReviewComment rc
       GROUP BY rc.fileName
       ORDER BY COUNT(rc) DESC
       """)
    List<TopProblemFileResponse> findTopProblemFiles();

    @Query("""
       SELECT new com.harsh.ai_code_review_system.dto.TopProblemFileResponse(
           rc.fileName,
           COUNT(rc)
       )
       FROM ReviewComment rc
       WHERE rc.review.pullRequest.repository.id = :repositoryId
       GROUP BY rc.fileName
       ORDER BY COUNT(rc) DESC
       """)
    List<TopProblemFileResponse> findTopProblemFilesByRepositoryId(
            @Param("repositoryId") Long repositoryId
    );

    @Query("""
       SELECT new com.harsh.ai_code_review_system.dto.TopProblemFileResponse(
           rc.fileName,
           COUNT(rc)
       )
       FROM ReviewComment rc
       WHERE rc.review.pullRequest.repository.user.id = :userId
       GROUP BY rc.fileName
       ORDER BY COUNT(rc) DESC
       """)
    List<TopProblemFileResponse> findTopProblemFilesByUserId(
            @Param("userId") Long userId
    );
}