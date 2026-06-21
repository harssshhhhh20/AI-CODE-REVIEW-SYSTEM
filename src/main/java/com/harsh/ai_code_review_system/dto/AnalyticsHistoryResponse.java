package com.harsh.ai_code_review_system.dto;

import java.time.LocalDateTime;

public record AnalyticsHistoryResponse(
        Long reviewId,
        Integer qualityScore,
        Boolean cacheHit,
        Long responseTimeMs,
        LocalDateTime createdAt
) {
}
