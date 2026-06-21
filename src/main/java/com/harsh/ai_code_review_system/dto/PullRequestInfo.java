package com.harsh.ai_code_review_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PullRequestInfo(
        @JsonProperty("head")
        PullRequestHead head
) {
}