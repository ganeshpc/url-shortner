package com.urlshortener.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {
    
    private final StringRedisTemplate redisTemplate;
    private static final int DEFAULT_LIMIT = 100; // requests per window
    private static final Duration DEFAULT_WINDOW = Duration.ofMinutes(1);
    
    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    /**
     * Check if request is allowed under rate limit
     * @param key unique identifier (IP address, user ID, etc.)
     * @param limit maximum requests allowed
     * @param window time window for the limit
     * @return true if request is allowed, false if rate limited
     */
    public boolean isAllowed(String key, int limit, Duration window) {
        String redisKey = "rate_limit:" + key;
        
        try {
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count == null) {
                return false;
            }
            
            if (count == 1) {
                redisTemplate.expire(redisKey, window);
            }
            
            return count <= limit;
        } catch (Exception e) {
            // If Redis is down, allow the request (fail open)
            return true;
        }
    }
    
    /**
     * Check if request is allowed using default limits
     */
    public boolean isAllowed(String key) {
        return isAllowed(key, DEFAULT_LIMIT, DEFAULT_WINDOW);
    }
    
    /**
     * Get remaining requests for a key
     */
    public long getRemainingRequests(String key, int limit) {
        String redisKey = "rate_limit:" + key;
        Long count = redisTemplate.opsForValue().get(redisKey) != null ? 
            Long.valueOf(redisTemplate.opsForValue().get(redisKey)) : 0L;
        return Math.max(0, limit - count);
    }
}
