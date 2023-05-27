package com.xjinyao.xcloud.interactive.captcha.core.service.impl;

import com.google.auto.service.AutoService;
import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存实现
 */
@AutoService(CaptchaCacheService.class)
public class CaptchaCacheServiceRedisImpl implements CaptchaCacheService {

    @Autowired
    private RedisTemplate stringRedisTemplate;

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        stringRedisTemplate.opsForValue().set(key, value, expiresInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return String.valueOf(stringRedisTemplate.opsForValue().get(key));
    }

    @Override
    public String type() {
        return "redis";
    }
}
