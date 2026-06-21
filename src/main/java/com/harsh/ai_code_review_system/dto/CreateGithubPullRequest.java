package com.harsh.ai_code_review_system.dto;

public record CreateGithubPullRequest(
        Long repositoryId,
        Long githubPrId,
        Integer prNumber,
        String title,
        String author,
        String diff
) {}
