package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.config.GitHubProperties;
import com.harsh.ai_code_review_system.dto.GithubCommentRequest;
import com.harsh.ai_code_review_system.dto.PullRequestInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final RestClient restClient;
    private final GitHubProperties properties;

    public String getPullRequestDiff(
            String owner,
            String repo,
            Integer prNumber
    ) {
        return restClient.get()
                .uri(
                        "https://api.github.com/repos/{owner}/{repo}/pulls/{number}",
                        owner,
                        repo,
                        prNumber
                )
                .header(
                        "Authorization",
                        "Bearer " + properties.getToken()
                )
                .header(
                        "Accept",
                        "application/vnd.github.v3.diff"
                )
                .retrieve()
                .body(String.class);
    }
    public void postComment(
            String owner,
            String repo,
            Integer prNumber,
            String comment
    ) {
        GithubCommentRequest request =
                new GithubCommentRequest(comment);
        System.out.println("Owner = " + owner);
        System.out.println("Repo = " + repo);
        System.out.println("PR Number = " + prNumber);
        System.out.println(
                "URL = https://api.github.com/repos/"
                        + owner
                        + "/"
                        + repo
                        + "/issues/"
                        + prNumber
                        + "/comments"
        );
        restClient.post()
                .uri(
                        "https://api.github.com/repos/{owner}/{repo}/issues/{number}/comments",
                        owner,
                        repo,
                        prNumber
                )
                .header(
                        "Authorization",
                        "Bearer " + properties.getToken()
                )
                .header(
                        "Accept",
                        "application/vnd.github+json"
                )
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
    public PullRequestInfo getPullRequestInfo(
            String owner,
            String repo,
            Integer prNumber
    ) {
        return restClient.get()
                .uri(
                        "https://api.github.com/repos/{owner}/{repo}/pulls/{number}",
                        owner,
                        repo,
                        prNumber
                )
                .header(
                        "Authorization",
                        "Bearer " + properties.getToken()
                )
                .header(
                        "Accept",
                        "application/vnd.github+json"
                )
                .retrieve()
                .body(PullRequestInfo.class);
    }
}