package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GithubService githubService;

    @GetMapping("/diff")
    public String getDiff(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam Integer prNumber
    ) {

        return githubService.getPullRequestDiff(
                owner,
                repo,
                prNumber
        );
    }

    @PostMapping("/comment")
    public String testComment() {
        githubService.postComment(
                "harssshhhhh20",
                "Sign-Language-Prediction-using-LSTM-MLP-Classifier",
                1,
                """
                ## AI Review
    
                HIGH: Optional.get() may throw NoSuchElementException.
    
                MEDIUM: Missing authorization checks.
                """
        );

        return "Comment posted";
    }
}