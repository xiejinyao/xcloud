package com.xjinyao.xcloud.common.core.redis.config;

import com.xjinyao.xcloud.common.core.redis.constant.RedisTemplateBeanNames;
import com.xjinyao.xcloud.common.core.redis.service.DefaultRedisService;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @date 2019/2/1 Redis 配置类
 */
@EnableCaching
@Configuration
@RequiredArgsConstructor
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisTemplateConfiguration {

    private final RedisConnectionFactory factory;

    @Primary
    @Bean(name = RedisTemplateBeanNames.DEFAULT_REDIS_TEMPLATE)
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean(RedisTemplateBeanNames.JSON_REDIS_TEMPLATE)
    public RedisTemplate<String, Object> jsonRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer(Object.class));
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    @ConditionalOnBean(name = RedisTemplateBeanNames.JSON_REDIS_TEMPLATE)
    public RedisService redisService(@Qualifier(RedisTemplateBeanNames.JSON_REDIS_TEMPLATE) RedisTemplate redisTemplate) {
        return new RedisService(redisTemplate);
    }

    @Bean
    public DefaultRedisService defaultRedisService(@Qualifier(RedisTemplateBeanNames.DEFAULT_REDIS_TEMPLATE)
                                                       RedisTemplate redisTemplate) {
        return new DefaultRedisService(redisTemplate);
    }
}
