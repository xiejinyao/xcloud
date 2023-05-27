package com.xjinyao.xcloud.common.mybatis.resolver;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * @description 参数解析配置
 * @createDate 2020/5/8 14:30
 */
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ModelArgumentResolver());
        argumentResolvers.add(new SqlFilterArgumentResolver());
    }
}
