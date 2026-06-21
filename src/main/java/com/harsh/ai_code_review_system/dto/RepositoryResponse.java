package com.harsh.ai_code_review_system.dto;

public record RepositoryResponse(
        Long id,
        String name,
        String owner,
        String status
) {
}