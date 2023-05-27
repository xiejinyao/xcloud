package com.xjinyao.xcloud.core.rule.config;

import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.core.rule.listener.RequestMappingScanListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * @date 2019-06-24
 * <p>
 * 注入自自定义SQL 过滤
 */
@Slf4j
@Configuration
@AllArgsConstructor
@ConditionalOnWebApplication(type = SERVLET)
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final RedisService redisService;

    /**
     * 资源扫描监听器类
     *
     * @return RequestMappingScanListener
     */
    @Bean
    @ConditionalOnMissingBean(RequestMappingScanListener.class)
    public RequestMappingScanListener resourceAnnotationScan() {
        RequestMappingScanListener scan = new RequestMappingScanListener(redisService);
        log.info("资源扫描类.[{}]", scan);
        return scan;
    }
}
