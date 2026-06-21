package com.harsh.ai_code_review_system.dto;

import java.util.List;

public record AiReviewResult(
        Integer qualityScore,
        String summary,
        List<AiIssue> issues
) {
}
