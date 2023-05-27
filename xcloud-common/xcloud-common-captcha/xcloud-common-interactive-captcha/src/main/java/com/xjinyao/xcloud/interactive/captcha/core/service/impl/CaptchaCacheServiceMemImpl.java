package com.xjinyao.xcloud.interactive.captcha.core.service.impl;

import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaCacheService;
import com.xjinyao.xcloud.interactive.captcha.core.util.CacheUtil;

/**
 * 内存缓存实现
 */
public class CaptchaCacheServiceMemImpl implements CaptchaCacheService {
    @Override
    public void set(String key, String value, long expiresInSeconds) {

        CacheUtil.set(key, value, expiresInSeconds);
    }

    @Override
    public boolean exists(String key) {
        return CacheUtil.exists(key);
    }

    @Override
    public void delete(String key) {
        CacheUtil.delete(key);
    }

    @Override
    public String get(String key) {
        return CacheUtil.get(key);
    }

    @Override
    public String type() {
        return "local";
    }
}
