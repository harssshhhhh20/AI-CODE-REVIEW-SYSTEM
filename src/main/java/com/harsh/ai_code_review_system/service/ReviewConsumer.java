package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.config.RabbitMQConfig;
import com.harsh.ai_code_review_system.dto.ReviewJob;
import com.harsh.ai_code_review_system.entity.Review;
import com.harsh.ai_code_review_system.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewConsumer {

    private final ReviewProcessingService reviewProcessingService;
    private final ReviewRepository reviewRepository;

    @RabbitListener(queues = RabbitMQConfig.REVIEW_QUEUE, concurrency = "4")
    public void consume(ReviewJob job) {

        log.info("Processing review {}", job.reviewId());

        Review review = reviewRepository.findById(job.reviewId())
                .orElseThrow(() -> new RuntimeException("Review not found"));

        try {

            review.setStatus("PROCESSING");
            reviewRepository.save(review);

            reviewProcessingService.processReview(job.reviewId());

            log.info("Review {} processed successfully", job.reviewId());

        } catch (Exception e) {

            review.setStatus("FAILED");
            reviewRepository.save(review);

            log.error(
                    "Review {} failed",
                    job.reviewId(),
                    e
            );
        }
    }
}