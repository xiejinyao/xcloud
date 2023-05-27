package com.xjinyao.xcloud.gateway.filter;

import com.xjinyao.xcloud.gateway.service.SafeRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @author 谢进伟
 * @description 安全规则过滤器，负责黑名单、服务路由、服务api进行安全检查过滤
 * @createDate 2020/11/16 15:33
 */
@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
public class SecurityRuleFilter implements WebFilter {

    private final SafeRuleService safeRuleService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 检查黑名单、检查服务是否启用、检查接口是否启用
        Mono<Void> filterBlackListResult = safeRuleService.filter(exchange);
        if (filterBlackListResult != null) {
            return filterBlackListResult;
        }
        return chain.filter(exchange);
    }
}
