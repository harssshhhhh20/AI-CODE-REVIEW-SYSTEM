package com.harsh.ai_code_review_system.dto;

public record CachedReviewResponse(
        Integer qualityScore,
        String summary,
        String rawResponse,
        Long responseTimeMs,
        String modelName
) {
}
