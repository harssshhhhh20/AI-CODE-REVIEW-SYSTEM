package com.harsh.ai_code_review_system.controller;


import com.harsh.ai_code_review_system.dto.AnalyticsResponseSummary;
import com.harsh.ai_code_review_system.dto.TopProblemFileResponse;
import com.harsh.ai_code_review_system.service.ReviewProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.harsh.ai_code_review_system.dto.SeverityResponse;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final ReviewProcessingService reviewProcessingService;

    @GetMapping("/summary")
    public AnalyticsResponseSummary getSummary(){
        return reviewProcessingService.getAnalyticsSummary();
    }

    @GetMapping("/severity")
    public SeverityResponse getSeverityDistribution() {
        return reviewProcessingService.getSeverityDistribution();

    }

    @GetMapping("top-files")
    public List <TopProblemFileResponse> getTopfiles(){
        return reviewProcessingService.getTopProblemFiles();
    }
}
