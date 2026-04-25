package com.gateway.guardrails.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuardrailService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BOT_COUNT_KEY_PREFIX = "post:";
    private static final String BOT_COUNT_KEY_SUFFIX = ":bot_count";
    private static final String COOLDOWN_KEY_PREFIX = "cooldown:bot_";
    private static final String COOLDOWN_KEY_SUFFIX = ":human_";
    private static final int MAX_BOT_REPLIES_PER_POST = 100;
    private static final int MAX_DEPTH_LEVEL = 20;
    private static final long COOLDOWN_DURATION_MINUTES = 10;

    public boolean checkHorizontalCap(Long postId) {
        String key = BOT_COUNT_KEY_PREFIX + postId + BOT_COUNT_KEY_SUFFIX;
        Long currentCount = redisTemplate.opsForValue().increment(key);
        
        if (currentCount != null && currentCount == 1)
            redisTemplate.expire(key, Duration.ofHours(24));
        
        boolean allowed = currentCount != null && currentCount <= MAX_BOT_REPLIES_PER_POST;
        
        if (!allowed) {
            redisTemplate.opsForValue().decrement(key);
            log.warn("Horizontal cap exceeded for post {}: {} bot replies", postId, currentCount);
        } else {
            log.debug("Horizontal cap check for post {}: {} bot replies", postId, currentCount);
        }
        
        return allowed;
    }

    public boolean checkVerticalCap(Integer depthLevel) {
        boolean allowed = depthLevel != null && depthLevel <= MAX_DEPTH_LEVEL;
        
        if (!allowed)
            log.warn("Vertical cap exceeded: depth level {} exceeds max {}", depthLevel, MAX_DEPTH_LEVEL);
        
        return allowed;
    }

    public boolean checkCooldownCap(Long botId, Long humanId) {
        String key = COOLDOWN_KEY_PREFIX + botId + COOLDOWN_KEY_SUFFIX + humanId;
        Boolean wasSet = redisTemplate
        		.opsForValue()
                .setIfAbsent(
                		key,
                		"1",
                		Duration.ofMinutes(COOLDOWN_DURATION_MINUTES));
        
        boolean allowed = Boolean.TRUE.equals(wasSet);
        
        if (!allowed)
            log.warn("Cooldown cap active: bot {} cannot interact with human {} yet", botId, humanId);
        else
        	log.debug("Cooldown cap cleared: bot {} can interact with human {}", botId, humanId);
        
        return allowed;
    }

    public Long getBotCount(Long postId) {
        String key = BOT_COUNT_KEY_PREFIX + postId + BOT_COUNT_KEY_SUFFIX;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }
}