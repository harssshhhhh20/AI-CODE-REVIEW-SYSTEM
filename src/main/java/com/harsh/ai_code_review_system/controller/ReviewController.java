package com.harsh.ai_code_review_system.controller;

import com.harsh.ai_code_review_system.dto.ReviewCardReponse;
import com.harsh.ai_code_review_system.dto.ReviewHistoryResponse;
import com.harsh.ai_code_review_system.dto.ReviewResponse;
import com.harsh.ai_code_review_system.service.ReviewProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewProcessingService reviewProcessingService;

    @GetMapping("/{id}")
    public ReviewResponse getReview(
            @PathVariable Long id
    ) {
        return reviewProcessingService.getReviewById(id);
    }

    @GetMapping("/pull-requests/{pullRequestId}/reviews")
    public List<ReviewHistoryResponse> getReviewHistory(
            @PathVariable Long pullRequestId
    ) {
        return reviewProcessingService.getReviewHistory(
                pullRequestId
        );
    }

    @GetMapping
    public List<ReviewCardReponse> getReviews() {
        return reviewProcessingService.getAllReviews();

    }

    @GetMapping("/repositories/{repositoryId}")
    public List<ReviewCardReponse> getReviewsByRepository(
            @PathVariable Long repositoryId
    ) {
        return reviewProcessingService.getReviewsByRepository(
                repositoryId
        );
    }
}