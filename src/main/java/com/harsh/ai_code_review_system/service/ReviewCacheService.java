package com.harsh.ai_code_review_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harsh.ai_code_review_system.dto.CachedReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReviewCacheService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public CachedReviewResponse get(
            String diffHash
    ) {
        try {
            String json = redisTemplate.opsForValue().get("review:" + diffHash);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, CachedReviewResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
    public void save(
            String diffHash,
            CachedReviewResponse response
    ) {
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set("review:" + diffHash, json, Duration.ofDays(1));
        } catch (Exception ignored) {

        }
    }
}