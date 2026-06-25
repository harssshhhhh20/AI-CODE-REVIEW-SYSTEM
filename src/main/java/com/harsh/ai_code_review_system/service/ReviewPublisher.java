package com.harsh.ai_code_review_system.service;

import com.harsh.ai_code_review_system.config.RabbitMQConfig;
import com.harsh.ai_code_review_system.dto.ReviewJob;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(ReviewJob job) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REVIEW_QUEUE,
                job
        );
    }
}