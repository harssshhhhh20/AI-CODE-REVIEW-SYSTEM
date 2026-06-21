package com.harsh.ai_code_review_system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "openrouter")
public class OpenRouterProperties {

    private String apiKey;
    private String model;
}