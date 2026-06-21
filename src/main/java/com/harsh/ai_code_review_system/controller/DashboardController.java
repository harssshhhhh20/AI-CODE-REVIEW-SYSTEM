package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.dto.AnalyticsResponseSummary;
import com.harsh.ai_code_review_system.dto.DashboardResponse;
import com.harsh.ai_code_review_system.service.RepositoryService;
import com.harsh.ai_code_review_system.service.ReviewProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ReviewProcessingService reviewProcessingService;
    private final RepositoryService repositoryService;

    @GetMapping
    public DashboardResponse getDashboard() {

        AnalyticsResponseSummary analytics =
                reviewProcessingService.getAnalyticsSummary();

        return new DashboardResponse(
                analytics.totalReviews(),
                repositoryService.getTotalRepositories(),
                analytics.cacheHits(),
                analytics.averageResponseTimeMs(),
                analytics.aiCallsSaved(),
                analytics.cacheHitRate()
        );
    }
}