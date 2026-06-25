package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.dto.AnalyticsHistoryResponse;
import com.harsh.ai_code_review_system.dto.AnalyticsResponseSummary;
import com.harsh.ai_code_review_system.dto.RepositoryResponse;
import com.harsh.ai_code_review_system.entity.CodeRepository;
import com.harsh.ai_code_review_system.service.RepositoryService;
import com.harsh.ai_code_review_system.service.ReviewProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
@RequiredArgsConstructor
public class RepositoryController {

    private final RepositoryService repositoryService;
    private final ReviewProcessingService reviewProcessingService;

    @PostMapping
    public CodeRepository createRepository(
            @RequestBody CodeRepository repository) {

        return repositoryService.createRepository(repository);
    }

    @GetMapping("/{repositoryId}/analytics")
    public AnalyticsResponseSummary getAnalytics(
            @PathVariable Long repositoryId
    ) {
        return reviewProcessingService
                .getAnalyticsSummary(repositoryId);
    }

    @GetMapping("/{repositoryId}/analytics/history")
    public List<AnalyticsHistoryResponse> getAnalyticsHistory(
            @PathVariable Long repositoryId
    ) {
        return reviewProcessingService
                .getAnalyticsHistory(repositoryId);
    }

    @GetMapping
    public List<RepositoryResponse> getRepositories() {
        return repositoryService.getRepositories();
    }

    @DeleteMapping("/{id}")
    public void deleteRepository(
            @PathVariable Long id
    ) {
        repositoryService.deleteRepository(id);
    }
}