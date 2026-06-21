package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.service.OpenRouterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiTestController {

    private final OpenRouterService openRouterService;

    @GetMapping("/api/test-ai")
    public String testAi() {
        return openRouterService.reviewPullRequest(
                "Add Payment API",
                "harsh",
                """
            + public User getUser(Long id) {
            +     return repository.findById(id).get();
            + }
            """
        );
    }
}