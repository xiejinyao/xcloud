package com.xjinyao.xcloud.core.rule.config;

import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import com.xjinyao.xcloud.core.rule.service.impl.RuleCacheServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 规则配置
 *
 * @author 谢进伟
 */
@Configuration
public class RuleConfiguration {

    @Bean
    public IRuleCacheService ruleCacheService(RedisService redisService) {
        return new RuleCacheServiceImpl(redisService);
    }
}
