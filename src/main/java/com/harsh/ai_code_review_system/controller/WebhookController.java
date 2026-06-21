package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.dto.GithubWebhookPayload;
import com.harsh.ai_code_review_system.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/github")
    public String receiveGithubWebhook(
            @RequestBody GithubWebhookPayload payload
    ) {
        webhookService.processWebhook(payload);
        return "Webhook Processed";
    }
}