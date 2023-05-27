package com.xjinyao.xcloud.interactive.captcha.config;

import com.xjinyao.xcloud.interactive.captcha.properties.CaptchaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@ComponentScan("com.xjinyao.xcloud.interactive.captcha")
@Import({
        CaptchaStorageAutoConfiguration.class,
        CaptchaServiceAutoConfiguration.class
})
public class CaptchaAutoConfiguration {
}
