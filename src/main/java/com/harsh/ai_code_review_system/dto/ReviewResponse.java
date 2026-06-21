package com.harsh.ai_code_review_system.dto;

import com.harsh.ai_code_review_system.dto.ReviewCommentResponse;

import java.util.List;

public record ReviewResponse(
        Long id,
        Integer qualityScore,
        String status,
        String summary,
        String repositoryName,
        Boolean cacheHit,
        Long responseTimeMs,
        String modelName,
        Integer highCount,
        Integer mediumCount,
        Integer lowCount,
        List<ReviewCommentResponse> comments
) {
}