package com.gateway.guardrails.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuardrailNotificationService {

	@Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String PENDING_NOTIFS_KEY_PREFIX = "user:";
    private static final String PENDING_NOTIFS_KEY_SUFFIX = ":pending_notifs";
    private static final String NOTIF_COOLDOWN_KEY_PREFIX = "user:";
    private static final String NOTIF_COOLDOWN_KEY_SUFFIX = ":notif_cooldown";
    private static final long NOTIF_COOLDOWN_MINUTES = 15;

    public void handleNotification(Long userId, String notification) {
        String cooldownKey = NOTIF_COOLDOWN_KEY_PREFIX + userId + NOTIF_COOLDOWN_KEY_SUFFIX;
        Boolean hasCooldown = redisTemplate.hasKey(cooldownKey);
        
        if (Boolean.TRUE.equals(hasCooldown)) {
            redisTemplate.opsForList().rightPush(PENDING_NOTIFS_KEY_PREFIX + userId + PENDING_NOTIFS_KEY_SUFFIX, notification);
            log.debug("Queued notification for user {}: {}", userId, notification);
        } else {
            log.info("Push Notification Sent to User {}: {}", userId, notification);
            redisTemplate.opsForValue().set(
            		cooldownKey,
            		"1",
            		Duration.ofMinutes(NOTIF_COOLDOWN_MINUTES));
        }
    }

    @Scheduled(fixedRate = 300000)
    public void processPendingNotifications() {
        log.info("Starting notification sweep...");
        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");
        if (keys == null || keys.isEmpty()) {
            log.info("No pending notifications to process");
            return;
        }
        
        for (String notifKey : keys) {
            try {
                Long listSize = redisTemplate.opsForList().size(notifKey);
                
                if (listSize == null || listSize == 0)
                	continue;
                
                StringBuilder summary = new StringBuilder();
                for (int i = 0; i < listSize; i++) {
                    Object notif = redisTemplate.opsForList().leftPop(notifKey);
                    if (notif != null) {
                        if (i > 0)
                            summary.append(", ");
                        summary.append(notif.toString());
                    }
                }
                
                String userIdStr = notifKey
                        .replace("user:", "")
                        .replace(":pending_notifs", "");
                
                log.info(
                		"Summarized Push Notification for User {}: {} and {} others interacted with your posts",
                		userIdStr,
                		summary,
                		listSize - 1);
                
            } catch (Exception e) {
                log.error("Error processing notifications for key {}: {}", notifKey, e.getMessage());
            }
        }
        
        log.info("Notification sweep completed");
    }
}