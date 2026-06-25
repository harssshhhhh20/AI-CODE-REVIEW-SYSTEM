package com.harsh.ai_code_review_system.dto.auth;

public record RegisterRequest(
        String username,
        String email,
        String password
) {}