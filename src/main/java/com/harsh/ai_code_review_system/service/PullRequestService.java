package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.entity.CodeRepository;
import com.harsh.ai_code_review_system.entity.PullRequest;
import com.harsh.ai_code_review_system.repository.CodeRepositoryRepository;
import com.harsh.ai_code_review_system.repository.PullRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PullRequestService {

    private final PullRequestRepository pullRequestRepository;
    private final CodeRepositoryRepository codeRepositoryRepository;

    public PullRequest createPullRequest(
            Long repositoryId,
            PullRequest pullRequest) {

        CodeRepository repository =
                codeRepositoryRepository.findById(repositoryId)
                        .orElseThrow(() ->
                                new RuntimeException("Repository not found"));

        pullRequest.setRepository(repository);

        return pullRequestRepository.save(pullRequest);
    }
}