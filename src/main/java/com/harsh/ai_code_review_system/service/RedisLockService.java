package com.harsh.ai_code_review_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public boolean acquireLock(String diffHash) {
        Boolean success =
                redisTemplate.opsForValue()
                        .setIfAbsent("review-lock:" + diffHash, "LOCKED", Duration.ofSeconds(30));

        return Boolean.TRUE.equals(success);
    }
    public void releaseLock(String diffHash) {
        redisTemplate.delete("review-lock:" + diffHash);
    }
}