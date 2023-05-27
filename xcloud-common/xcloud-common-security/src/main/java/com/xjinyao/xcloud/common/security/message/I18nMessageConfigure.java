package com.xjinyao.xcloud.common.security.message;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @description 国际化配置
 * @createDate 2020/9/9 15:33
 */
public class I18nMessageConfigure {

    @Bean("messageSource")
    public ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource() {
        ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("classpath*:org/springframework/security/custom");
        return reloadableResourceBundleMessageSource;
    }
}
