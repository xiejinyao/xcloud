package com.xjinyao.xcloud.common.core.config;

import cn.hutool.core.date.DatePattern;
import com.xjinyao.xcloud.common.core.interceptors.ResourceDownloadInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * @date 2019-06-24
 * <p>
 */
@Configuration
@ConditionalOnWebApplication(type = SERVLET)
public class WebMvcConfiguration implements WebMvcConfigurer {

    /**
     * 增加GET请求参数中时间类型转换
     * <ul>
     * <li>HH:mm:ss -> LocalTime</li>
     * <li>yyyy-MM-dd -> LocalDate</li>
     * <li>yyyy-MM-dd HH:mm:ss -> LocalDateTime</li>
     * </ul>
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
        dateTimeFormatterRegistrar.setTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN));
        dateTimeFormatterRegistrar.setDateFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN));
        dateTimeFormatterRegistrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN));
        dateTimeFormatterRegistrar.registerFormatters(registry);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //资源下载映射
        registry.addResourceHandler(ResourceDownloadInterceptor.DOWNLOAD_REQUEST_URI_PREFIX + "/docs/**")
                .addResourceLocations("classpath:/docs/")
                .setCacheControl(CacheControl.noStore());
        registry.addResourceHandler(ResourceDownloadInterceptor.DOWNLOAD_REQUEST_URI_PREFIX + "/template/**")
                .addResourceLocations("classpath:/template/")
                .setCacheControl(CacheControl.noStore());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //资源下载拦截器
        registry.addInterceptor(new ResourceDownloadInterceptor());
    }
}
