package com.xjinyao.xcloud.gateway.properties;

import com.xjinyao.xcloud.gateway.filter.SecurityRuleFilter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author 谢进伟
 * @description 安全规则属性
 * @createDate 2020/11/23 9:37
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties("gateway.safe-rule")
public class SafeRuleProperties {

    /**
     * 黑名单检查 {@link SecurityRuleFilter}
     */
    private Boolean checkBlacklist;

    /**
     * 微服务路由状态检查 {@link SecurityRuleFilter}
     */
    private Boolean checkRouteStatus;

    /**
     * 微服务Api状态检查 {@link SecurityRuleFilter}
     */
    private Boolean checkApiStatus;
}
