package com.harsh.ai_code_review_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.ai_code_review_system.config.OpenRouterProperties;
import com.harsh.ai_code_review_system.dto.OpenRouterMessage;
import com.harsh.ai_code_review_system.dto.OpenRouterRequest;
import com.harsh.ai_code_review_system.dto.OpenRouterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenRouterService {

    private final RestClient restClient;
    private final OpenRouterProperties properties;

    public String reviewPullRequest(
            String title,
            String author,
            String diff
    ) {

        String prompt = """
        You are a senior software engineer performing a professional code review.
        
        Review this pull request.
        
        Title: %s
        Author: %s
        
        Code Changes:
        
        %s
        
        Tasks:
        1. Find bugs.
        2. Find security issues.
        3. Find performance issues.
        4. Suggest improvements.
        5. Assign a quality score from 0-100.
        6. For each issue, identify the affected file and approximate line number.
        
        Return ONLY valid JSON in the following format:
        
        {
          "qualityScore": 75,
          "summary": "Overall code quality is acceptable but improvements are required.",
          "issues": [
            {
              "severity": "HIGH",
              "fileName": "UserService.java",
              "lineNumber": 23,
              "comment": "Using Optional.get() may throw NoSuchElementException if no user exists."
            },
            {
              "severity": "MEDIUM",
              "fileName": "UserController.java",
              "lineNumber": 45,
              "comment": "Missing authorization checks before exposing user data."
            }
          ]
        }
        
        Rules:
        - Return JSON only.
        - Do not use markdown.
        - Do not wrap the response in ```json.
        - "severity" must be one of HIGH, MEDIUM, LOW.
        - "fileName" should contain the most relevant source file.
        - "lineNumber" should contain the approximate line where the issue occurs.
        - "comment" should describe exactly one issue.
        - "issues" must be an array of objects.
        - If no issues are found, return an empty array.
        - Do not include explanations outside the JSON.
        - Ensure the response is valid JSON that can be parsed directly.
        
        """.formatted(title, author, diff);

        OpenRouterRequest request =
                new OpenRouterRequest(
                        properties.getModel(),
                        List.of(
                                new OpenRouterMessage(
                                        "user",
                                        prompt
                                )
                        )
                );

        String response =
                restClient.post()
                        .uri("https://openrouter.ai/api/v1/chat/completions")
                        .header("Authorization",
                                "Bearer " + properties.getApiKey())
                        .header("Content-Type", "application/json")
                        .body(request)
                        .retrieve()
                        .body(String.class);

        System.out.println("RAW OPENROUTER RESPONSE:");
        System.out.println(response);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);

            // Handle OpenRouter error responses
            if (root.has("error")) {
                return "OpenRouter Error: "
                        + root.path("error")
                        .path("message")
                        .asText();
            }
            JsonNode choices = root.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return "No review generated";
            }
            return choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to parse OpenRouter response",
                    e
            );
        }
    }
}