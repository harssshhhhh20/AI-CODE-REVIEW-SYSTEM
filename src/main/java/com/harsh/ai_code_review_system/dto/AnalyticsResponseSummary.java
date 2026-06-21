package com.harsh.ai_code_review_system.dto;

public record AnalyticsResponseSummary(
        Long totalReviews,
        Long cacheHits,
        Double cacheHitRate,
        Double averageResponseTimeMs,
        Long aiCallsSaved
) {
}
