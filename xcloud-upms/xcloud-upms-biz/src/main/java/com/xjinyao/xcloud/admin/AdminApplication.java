package com.xjinyao.xcloud.admin;

import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomFeignClients;
import com.xjinyao.xcloud.common.security.annotation.EnableCustomResourceServer;
import com.xjinyao.xcloud.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * @date 2018年06月21日 用户统一管理系统
 */
@EnableCustomSwagger2
@EnableCustomResourceServer
@EnableCustomFeignClients
@SpringBootApplication
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public TokenStore tokenStore(RedisConnectionFactory redisConnectionFactory) {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        tokenStore.setPrefix(CacheConstants.PROJECT_OAUTH_ACCESS);
        return tokenStore;
    }
}
