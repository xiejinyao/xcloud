package com.xjinyao.xcloud.common.security.config;

import com.xjinyao.xcloud.common.security.filter.RequestReplaceInputStreamFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

/**
 * @author 谢进伟
 * @description 过滤器配置
 * @createDate 2021/2/26 15:44
 */
public class FilterConfig {

    /**
     * 注册过滤器
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration(@Qualifier("requestReplaceInputStreamFilter")
                                                         Filter replaceStreamFilter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(replaceStreamFilter);
        registration.addUrlPatterns("/*");
        registration.setName("requestReplaceInputStreamFilter");
        return registration;
    }

    /**
     * 实例化StreamFilter
     *
     * @return Filter
     */
    @Bean(name = "requestReplaceInputStreamFilter")
    public Filter requestReplaceInputStreamFilter() {
        return new RequestReplaceInputStreamFilter();
    }
}
