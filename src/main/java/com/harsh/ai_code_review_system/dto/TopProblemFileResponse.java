package com.harsh.ai_code_review_system.dto;

public record TopProblemFileResponse(
        String fileName,
        long issueCount
) {
}
