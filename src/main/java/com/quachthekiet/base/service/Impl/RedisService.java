package com.quachthekiet.base.service.Impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            return redisTemplate.opsForValue().get(token) != null;
        } catch (RedisConnectionFailureException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    public void addToBlacklist(String token, long expSeconds) {
        save(token, "blacklisted", expSeconds);
    }
}
