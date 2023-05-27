package com.xjinyao.xcloud.admin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author 谢进伟
 * @description 用户安全配置参数
 * @createDate 2021/1/22 11:24
 */
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "security.user.default")
public class UserSecurityProperties {

    /**
     * 重置密码时所使用的默认密码
     */
    private String password;
}
