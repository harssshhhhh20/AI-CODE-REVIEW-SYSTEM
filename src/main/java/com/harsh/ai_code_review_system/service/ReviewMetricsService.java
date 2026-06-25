package com.harsh.ai_code_review_system.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class ReviewMetricsService {

    private final Counter reviewsCompleted;
    private final Counter reviewsFailed;

    public ReviewMetricsService(MeterRegistry registry) {
        reviewsCompleted = Counter.builder("codesage.reviews.completed")
                .description("Completed AI reviews")
                .register(registry);

        reviewsFailed = Counter.builder("codesage.reviews.failed")
                .description("Failed AI reviews")
                .register(registry);
    }

    public void incrementCompleted() {
        reviewsCompleted.increment();
    }

    public void incrementFailed() {
        reviewsFailed.increment();
    }
}