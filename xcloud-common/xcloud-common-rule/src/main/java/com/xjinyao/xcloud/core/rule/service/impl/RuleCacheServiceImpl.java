package com.xjinyao.xcloud.core.rule.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xjinyao.xcloud.common.core.redis.service.RedisService;
import com.xjinyao.xcloud.core.rule.RuleUtil;
import com.xjinyao.xcloud.core.rule.constant.RuleConstant;
import com.xjinyao.xcloud.core.rule.po.ApiStatusList;
import com.xjinyao.xcloud.core.rule.po.BlackList;
import com.xjinyao.xcloud.core.rule.po.RouterStatusList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则缓存实现业务类
 */
@Service
@AllArgsConstructor
public class RuleCacheServiceImpl implements IRuleCacheService {

    private RedisService redisService;

    /**
     * 获取指定ip的黑名单
     *
     * @param ip 　ip地址
     * @return
     */
    @Override
    public Set<Object> getBlackList(String ip) {
        return redisService.sGet(RuleConstant.getBlackListCacheKey(ip));
    }

    /**
     * 获取所有黑名单
     *
     * @return
     */
    @Override
    public Set<Object> getBlackList() {
        return redisService.sGet(RuleConstant.getBlackListCacheKey());
    }

    /**
     * 缓存黑名单
     *
     * @param blackList 黑名单对象
     */
    @Override
    public void cacheBlackList(BlackList blackList) {
        String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant.getBlackListCacheKey(blackList.getIp())
                : RuleConstant.getBlackListCacheKey();
        redisService.sSet(key, JSONObject.toJSONString(blackList));
    }

    /**
     * 删除黑名单缓存
     *
     * @param blackList 黑名单对象
     */
    @Override
    public void deleteBlackList(BlackList blackList) {
        String key = StringUtils.isNotBlank(blackList.getIp()) ? RuleConstant.getBlackListCacheKey(blackList.getIp())
                : RuleConstant.getBlackListCacheKey();
        redisService.setRemove(key, JSONObject.toJSONString(blackList));
    }

    /**
     * 缓存路由状态信息
     *
     * @param routers 路由信息
     */
    @Override
    public void cacheRoutes(List<RouterStatusList> routers) {
        if (CollectionUtils.isEmpty(routers)) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        for (RouterStatusList router : routers) {
            map.put(router.getServiceId(), router.getStatus());
        }
        redisService.hmset(RuleConstant.getRouterCacheKey(), map);
    }

    /**
     * 获取路由状态
     *
     * @param serviceId 服务id
     * @return
     */
    @Override
    public Boolean getRouterStatus(String serviceId) {
        return (Boolean) redisService.hmget(RuleConstant.getRouterCacheKey(), serviceId);
    }

    /**
     * 移除路由缓存
     *
     * @param serviceId 服务id
     */
    @Override
    public void deleteRouteCache(String serviceId) {
        if (serviceId != null) {
            redisService.hdel(RuleConstant.getRouterCacheKey(), serviceId);
        }
    }

    /**
     * 缓存Api 状态信息
     *
     * @param api Api新
     */
    @Override
    public void cacheApis(ApiStatusList api) {
        cacheApis(Collections.singletonList(api));
    }

    /**
     * 缓存Api 状态信息
     *
     * @param apis 接口集合
     */
    @Override
    public void cacheApis(List<ApiStatusList> apis) {
        if (CollectionUtils.isEmpty(apis)) {
            return;
        }
        apis.parallelStream().collect(Collectors.groupingBy(ApiStatusList::getServiceId)).forEach((serviceId, apiStatusLists) -> {
            Map<String, Object> map = new HashMap<>();
            for (ApiStatusList api : apiStatusLists) {
                map.put(RuleUtil.getApiPattern(api.getPattern(), api.getMethod()), api.getStatus());
            }
            redisService.hmset(RuleConstant.getApiCacheKey(serviceId), map);
        });
    }

    /**
     * 获取Api状态是否启用
     *
     * @param serviceId 服务id
     * @return
     */
    @Override
    public Map<Object, Object> getServiceApiStatusList(String serviceId) {
        return redisService.hmget(RuleConstant.getApiCacheKey(serviceId));
    }

    /**
     * 移除Api缓存
     *
     * @param serviceId 服务id
     * @param method    请求方法
     * @param pattern   匹配模式
     */
    @Override
    public void deleteApiCache(String serviceId, HttpMethod method, String pattern) {
        deleteApiCaches(serviceId, method, Collections.singletonList(pattern));
    }

    /**
     * 移除Api缓存
     *
     * @param serviceId   服务id
     * @param method      请求方法
     * @param patternList 匹配模式
     */
    @Override
    public void deleteApiCaches(String serviceId, HttpMethod method, List<String> patternList) {
        if (!CollectionUtils.isEmpty(patternList)) {
            List<String> delKeys = new ArrayList<>();
            for (String pattern : patternList) {
                delKeys.add(RuleUtil.getApiPattern(pattern, method));
            }
            redisService.hdel(RuleConstant.getApiCacheKey(serviceId), delKeys.toArray());
        }
    }
}
