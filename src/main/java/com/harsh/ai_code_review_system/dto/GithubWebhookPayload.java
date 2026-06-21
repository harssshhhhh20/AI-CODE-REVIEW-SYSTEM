package com.harsh.ai_code_review_system.dto;

public record GithubWebhookPayload(
        String action,
        PullRequest pull_request,
        Repository repository
) {

    public record PullRequest(
            Long id,
            Integer number,
            String title,
            User user
    ) {}

    public record User(
            String login
    ) {}

    public record Repository(
            String name,
            Owner owner
    ) {}

    public record Owner(
            String login
    ) {}
}