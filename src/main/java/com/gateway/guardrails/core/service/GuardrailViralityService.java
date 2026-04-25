package com.gateway.guardrails.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuardrailViralityService {

	@Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String VIRALITY_KEY_PREFIX = "post:";
    private static final String VIRALITY_SCORE_SUFFIX = ":virality_score";
    private static final int BOT_REPLY_POINTS = 1;
    private static final int HUMAN_LIKE_POINTS = 20;
    private static final int HUMAN_COMMENT_POINTS = 50;

    public void updateViralityScore(Long postId, String interactionType) {
        int points = getPointsForInteraction(interactionType);
        Long newScore = redisTemplate.opsForValue().increment(VIRALITY_KEY_PREFIX + postId + VIRALITY_SCORE_SUFFIX, points);
        log.debug("Updated virality score for post {} to {} (interaction: {})", postId, newScore, interactionType);
    }

    public Long getViralityScore(Long postId) {
        Object value = redisTemplate.opsForValue().get(VIRALITY_KEY_PREFIX + postId + VIRALITY_SCORE_SUFFIX);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    private int getPointsForInteraction(String interactionType) {
        return switch (interactionType) {
            case "BOT_REPLY" -> BOT_REPLY_POINTS;
            case "HUMAN_LIKE" -> HUMAN_LIKE_POINTS;
            case "HUMAN_COMMENT" -> HUMAN_COMMENT_POINTS;
            default -> 0;
        };
    }
}