package com.harsh.ai_code_review_system.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenRouterChoice(
        OpenRouterMessage message
) {
}
