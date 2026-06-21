package com.harsh.ai_code_review_system.dto;

public record ReviewCommentResponse(
        String severity,
        String fileName,
        String lineNumber,
        String comment
) {
}
