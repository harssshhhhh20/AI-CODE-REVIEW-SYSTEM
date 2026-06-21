package com.harsh.ai_code_review_system.dto;

public record DashboardResponse(
        long totalReviews,
        long totalRepositories,
        long cacheHits,
        double averageResponseTime,
        long aiCallsSaved,
        double cacheHitRate
) {
}
