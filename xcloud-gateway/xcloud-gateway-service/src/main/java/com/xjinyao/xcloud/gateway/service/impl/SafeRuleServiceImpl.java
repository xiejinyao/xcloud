package com.xjinyao.xcloud.gateway.service.impl;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.xjinyao.xcloud.common.core.util.DateUtil;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.RequestHolder;
import com.xjinyao.xcloud.core.rule.RuleUtil;
import com.xjinyao.xcloud.core.rule.constant.RuleConstant;
import com.xjinyao.xcloud.core.rule.po.BlackList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import com.xjinyao.xcloud.gateway.properties.SafeRuleProperties;
import com.xjinyao.xcloud.gateway.service.SafeRuleService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 安全规则业务实现类
 *
 * @author 谢进伟
 */
@Slf4j
@Service
public class SafeRuleServiceImpl implements SafeRuleService {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final IRuleCacheService ruleCacheService;
    private final NacosDynamicRouteServiceImpl nacosDynamicRouteService;
    private final SafeRuleProperties safeRuleProperties;

    public SafeRuleServiceImpl(IRuleCacheService ruleCacheService,
                               NacosDynamicRouteServiceImpl nacosDynamicRouteService,
                               SafeRuleProperties safeRuleProperties) {
        this.ruleCacheService = ruleCacheService;
        this.nacosDynamicRouteService = nacosDynamicRouteService;
        this.safeRuleProperties = safeRuleProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange) {
        boolean checkRouteStatus = Optional.ofNullable(safeRuleProperties.getCheckRouteStatus()).orElse(false);
        boolean checkApiStatus = Optional.ofNullable(safeRuleProperties.getCheckApiStatus()).orElse(false);
        boolean checkBlacklist = Optional.ofNullable(safeRuleProperties.getCheckBlacklist()).orElse(false);
        if (!checkRouteStatus && !checkApiStatus && !checkBlacklist) {
            // 所有安全规则都不进行检查
            return null;
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        try {
            URI originUri = getOriginRequestUri(exchange);
            if (originUri != null) {
                String requestIp = RequestHolder.getServerHttpRequestIpAddress(request);
                String requestMethod = request.getMethodValue();
                if (checkRouteStatus || checkApiStatus) {
                    Mono<Void> x = checkRoutAndApiStatus(response, checkRouteStatus, checkApiStatus, stopwatch, originUri,
                            requestMethod);
                    if (x != null) {
                        return x;
                    }
                }
                if (checkBlacklist) {
                    if (checkBlacklist(stopwatch, originUri, requestIp, requestMethod)) {
                        return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE,
                                HttpStatus.NOT_ACCEPTABLE, R.restResult(null, HttpStatus.NOT_ACCEPTABLE.value(),
                                        "该接口已列入黑名单，暂访问受限!"));
                    }
                }
            } else {
                log.info("Safety rule check skipped - {}", stopwatch.stop());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Safety rules check for exceptions: {} - {}", e.getMessage(), stopwatch.stop());
        }
        return null;
    }

    /**
     * 检查路由状态和Api状态
     *
     * @param response         响应对象
     * @param checkRouteStatus 是否检查路由状态
     * @param checkApiStatus   是否检查api状态
     * @param stopwatch        计时器
     * @param originUri        请求路径
     * @param requestMethod    请求方法
     * @return
     */
    public Mono<Void> checkRoutAndApiStatus(ServerHttpResponse response, boolean checkRouteStatus, boolean checkApiStatus,
                                            Stopwatch stopwatch, URI originUri, String requestMethod) {
        // 微服务状态检查
        Map<String, String> servicePredicatesMap = nacosDynamicRouteService.getServicePredicatesMap();
        String apiUri = null;
        if (!CollectionUtils.isEmpty(servicePredicatesMap)) {
            String microService = null;
            for (String servicePattern : servicePredicatesMap.keySet()) {
                if (antPathMatcher.match(servicePattern, originUri.getPath())) {
                    microService = servicePredicatesMap.get(servicePattern);
                    // 注意这里需要保证微服务的匹配规则是“以什么开头”的规则才可以正常得到到当前请求对应的微服务的正确uri信息
                    apiUri = antPathMatcher.extractPathWithinPattern(servicePattern, originUri.getPath());
                    break;
                }
            }
            if (microService == null) {
                //未配置微服务路由信息，自动跳过路由和api的状态查
                return null;
            }
            if (checkRouteStatus) {
                //检查服务是否启用
                Boolean routerStatus = Optional.ofNullable(ruleCacheService.getRouterStatus(microService))
                        .orElse(Boolean.TRUE);
                log.info("Micro service:{}, status:{}", microService, routerStatus);
                if (!routerStatus) {
                    log.info("Safety rule check completed - {}", stopwatch.stop());
                    return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE,
                            HttpStatus.NOT_ACCEPTABLE, R.restResult(null, HttpStatus.NOT_ACCEPTABLE.value(),
                                    "提供该接口的微服务路由已禁用，暂访问受限!"));
                }
            }
            if (checkApiStatus) {
                //检查API状态
                Boolean apiStatus = getApiStatus(apiUri, requestMethod, microService);
                log.info("Micro service:{},api:{}, status:{}", microService, apiUri, apiStatus);
                if (!apiStatus) {
                    log.info("Safety rule check completed - {}", stopwatch.stop());
                    return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE,
                            HttpStatus.NOT_ACCEPTABLE, R.restResult(null, HttpStatus.NOT_ACCEPTABLE.value(),
                                    "该接口已禁用，暂访问受限!"));
                }
            }
        }
        return null;
    }

    /**
     * 检查黑名单状态
     *
     * @param stopwatch     计时器
     * @param originUri     请求路径
     * @param requestIp     请求Ip
     * @param requestMethod 请求方法
     * @return
     */
    public boolean checkBlacklist(Stopwatch stopwatch, URI originUri, String requestIp, String requestMethod) {
        //黑名单规则检查
        AtomicBoolean forbid = new AtomicBoolean(false);
        //从缓存中获取黑名单信息
        Set<Object> blackLists = new HashSet<>();
        blackLists.addAll(Optional.ofNullable(ruleCacheService.getBlackList(requestIp)).orElse(Collections.emptySet()));
        blackLists.addAll(ruleCacheService.getBlackList());
        //检查是否在黑名单中
        checkBlackLists(forbid, blackLists, originUri, requestMethod);

        log.info("Safety rule check completed - {}", stopwatch.stop());

        if (forbid.get()) {
            log.warn("The blacklist address is being requested： - {}", originUri.getPath());
            return true;
        }
        return false;
    }

    /**
     * 获取网关请求URI
     *
     * @param exchange
     * @return
     */
    private URI getOriginRequestUri(ServerWebExchange exchange) {
        return exchange.getRequest().getURI();
    }

    /**
     * 检查是否满足黑名单的条件
     *
     * @param forbid
     * @param blackLists
     * @param uri
     * @param requestMethod
     */
    @SneakyThrows
    private void checkBlackLists(AtomicBoolean forbid, Set<Object> blackLists, URI uri, String requestMethod) {
        for (Object bl : blackLists) {
            BlackList blackList = JSONObject.parseObject(bl.toString(), BlackList.class);
            if (antPathMatcher.match(blackList.getRequestUri(), uri.getPath()) &&
                    RuleConstant.BLACKLIST_OPEN.equals(blackList.getStatus())) {
                if (RuleConstant.ALL.equalsIgnoreCase(blackList.getRequestMethod())
                        || StringUtils.equalsIgnoreCase(requestMethod, blackList.getRequestMethod())) {
                    LocalTime startTime = blackList.getStartTime();
                    LocalTime endTime = blackList.getEndTime();
                    if (startTime != null && endTime != null) {
                        if (DateUtil.nowIsInBetween(startTime, endTime)) {
                            forbid.set(Boolean.TRUE);
                        }
                    } else {
                        forbid.set(Boolean.TRUE);
                    }
                }
            }
            if (forbid.get()) {
                break;
            }
        }
    }

    /**
     * 获取API的状态
     *
     * @param apiUri
     * @param requestMethod
     * @param microService
     * @return
     */
    public Boolean getApiStatus(String apiUri, String requestMethod, String microService) {
        Map<Object, Object> serviceApiStatusList = ruleCacheService.getServiceApiStatusList(microService);
        if (!CollectionUtils.isEmpty(serviceApiStatusList)) {
            // 完全匹配查找
            String basePattern = "/" + apiUri;
            for (Object pattern : serviceApiStatusList.keySet()) {
                if (pattern == null) {
                    continue;
                }
                // ant匹配查找
                if (StringUtils.equals(pattern.toString(), RuleUtil.getApiPattern(basePattern, HttpMethod.valueOf(requestMethod)))) {
                    AtomicBoolean status = new AtomicBoolean(true);
                    Optional.of(serviceApiStatusList.get(pattern)).ifPresent(v -> status.set(BooleanUtil.toBoolean(v.toString())));
                    return status.get();
                }
            }
            // ant匹配查找
            for (Object pattern : serviceApiStatusList.keySet()) {
                if (pattern == null) {
                    continue;
                }
                if (antPathMatcher.match(pattern.toString(), RuleUtil.getApiPattern(basePattern, HttpMethod.valueOf(requestMethod)))) {
                    AtomicBoolean status = new AtomicBoolean(true);
                    Optional.of(serviceApiStatusList.get(pattern)).ifPresent(v -> status.set(BooleanUtil.toBoolean(v.toString())));
                    return status.get();
                }
            }

        }
        return Boolean.TRUE;
    }

    /**
     * 设置webflux模型响应
     *
     * @param response    ServerHttpResponse
     * @param contentType content-type
     * @param status      http状态码
     * @param value       响应内容
     * @return Mono<Void>
     */
    private Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType,
                                             HttpStatus status, Object value) {
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        R<Integer> result = R.failed(status.value(), value.toString());
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSONObject.toJSONString(result).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }
}
