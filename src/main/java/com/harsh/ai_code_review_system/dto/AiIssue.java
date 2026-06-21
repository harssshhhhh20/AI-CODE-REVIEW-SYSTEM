package com.harsh.ai_code_review_system.dto;

public record AiIssue(
        String severity,
        String comment,
        String lineNumber,
        String fileName
) {
}
