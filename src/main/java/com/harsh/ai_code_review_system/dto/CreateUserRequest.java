package com.harsh.ai_code_review_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotNull
        String githubId,

        @NotBlank
        String username,

        @Email
        @NotBlank
        String email
) {
}
