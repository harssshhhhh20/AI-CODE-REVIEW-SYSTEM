package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Optional<Review> findTopByDiffHashAndStatusOrderByIdDesc(
            String diffHash,
            String status
    );

    List<Review> findByPullRequestIdOrderByIdDesc(
            Long pullRequestId
    );

    long countBy();

    long countByCacheHitTrue();

    List<Review> findByCacheHitFalse();

    long countByPullRequestRepositoryId(
            Long repositoryId
    );

    long countByPullRequestRepositoryIdAndCacheHitTrue(
            Long repositoryId
    );

    List<Review> findByPullRequestRepositoryIdAndCacheHitFalse(
            Long repositoryId
    );

    List<Review> findByPullRequestRepositoryIdOrderByIdAsc(
            Long repositoryId
    );

    @Query("""
       SELECT AVG(r.qualityScore)
       FROM Review r
       """)
    Double findAverageQualityScore();
}
