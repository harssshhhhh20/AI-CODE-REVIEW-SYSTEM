package com.harsh.ai_code_review_system.dto;

public record ReviewHistoryResponse(
        Long id,
        Integer qualityScore,
        String status,
        Boolean cacheHit,
        Long responseTimeMs,
        String modelName
) {
}
