package com.harsh.ai_code_review_system.dto;

import java.util.List;

public record OpenRouterRequest(
        String model,
        List<OpenRouterMessage> messages
) {
}
