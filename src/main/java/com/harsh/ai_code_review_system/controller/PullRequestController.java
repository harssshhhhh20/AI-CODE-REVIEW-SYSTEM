package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.entity.PullRequest;
import com.harsh.ai_code_review_system.service.PullRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pull-requests")
@RequiredArgsConstructor
public class PullRequestController {

    private final PullRequestService pullRequestService;

    @PostMapping("/{repositoryId}")
    public PullRequest createPullRequest(
            @PathVariable Long repositoryId,
            @RequestBody PullRequest pullRequest
    ){
        return  pullRequestService.createPullRequest(repositoryId,pullRequest);
    }
}
