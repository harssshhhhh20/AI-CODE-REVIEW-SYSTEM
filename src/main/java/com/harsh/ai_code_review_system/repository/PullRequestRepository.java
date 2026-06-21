package com.harsh.ai_code_review_system.repository;

import com.harsh.ai_code_review_system.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PullRequestRepository extends JpaRepository<PullRequest,Long> {
    Optional<PullRequest> findByGithubPrId(Long githubPrId);
    boolean existsByGithubPrId(Long githubPrId);
}
