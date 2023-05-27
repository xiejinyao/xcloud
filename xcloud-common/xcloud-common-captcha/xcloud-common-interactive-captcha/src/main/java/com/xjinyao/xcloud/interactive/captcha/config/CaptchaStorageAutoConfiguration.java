package com.xjinyao.xcloud.interactive.captcha.config;

import com.xjinyao.xcloud.interactive.captcha.core.service.CaptchaCacheService;
import com.xjinyao.xcloud.interactive.captcha.core.service.impl.CaptchaServiceFactory;
import com.xjinyao.xcloud.interactive.captcha.properties.CaptchaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 存储策略自动配置.
 */
@Configuration
public class CaptchaStorageAutoConfiguration {

    @Bean
    public CaptchaCacheService captchaCacheService(CaptchaProperties captchaProperties) {
        return CaptchaServiceFactory.getCache(captchaProperties.getCacheType().name());
    }
}
