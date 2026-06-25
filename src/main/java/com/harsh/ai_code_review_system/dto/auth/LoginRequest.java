package com.harsh.ai_code_review_system.dto.auth;

public record LoginRequest(
        String email,
        String password
) {}