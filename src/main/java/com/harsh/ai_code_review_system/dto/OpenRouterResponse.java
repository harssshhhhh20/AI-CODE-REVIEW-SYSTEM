package com.harsh.ai_code_review_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenRouterResponse(
        List<OpenRouterChoice> choices
) {
}
