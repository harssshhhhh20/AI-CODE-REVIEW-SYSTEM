package com. harsh. ai_code_review_system. service;

import com.harsh.ai_code_review_system.config.OpenRouterProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final OpenRouterProperties properties;

    @PostConstruct
    public void test() {
        System.out.println(properties.getModel());
    }
}