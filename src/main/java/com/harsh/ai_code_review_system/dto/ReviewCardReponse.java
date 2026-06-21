package com.harsh.ai_code_review_system.dto;

public record ReviewCardReponse(
        Long id,
        Integer qualityScore,
        String status,
        String summary,
        String repositoryName,
        Integer highCount,
        Integer mediumCount,
        Integer lowCount,
        Boolean cacheHit,
        Long responseTimeMs,
        String modelName
) {
}
