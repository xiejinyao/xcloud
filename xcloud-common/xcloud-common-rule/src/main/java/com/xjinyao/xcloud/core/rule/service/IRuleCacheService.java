package com.xjinyao.xcloud.core.rule.service;

import com.xjinyao.xcloud.core.rule.po.ApiStatusList;
import com.xjinyao.xcloud.core.rule.po.BlackList;
import com.xjinyao.xcloud.core.rule.po.RouterStatusList;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 规则缓存业务
 */
public interface IRuleCacheService {

    /**
     * 根据IP获取黑名单
     *
     * @param ip 　ip
     * @return Set
     */
    Set<Object> getBlackList(String ip);

    /**
     * 查询所有黑名单
     *
     * @return Set
     */
    Set<Object> getBlackList();

    /**
     * 设置黑名单
     *
     * @param blackList 黑名单对象
     */
    void cacheBlackList(BlackList blackList);

    /**
     * 删除黑名单
     *
     * @param blackList 黑名单对象
     */
    void deleteBlackList(BlackList blackList);

    /**
     * 设置路由状态信息信息
     *
     * @param routers 路由状态信息
     */
    void cacheRoutes(List<RouterStatusList> routers);

    /**
     * 获取路由状态状态是否启用
     *
     * @param serviceId 服务id
     * @return
     */
    Boolean getRouterStatus(String serviceId);

    /**
     * 移除路由状态缓存
     *
     * @param serviceId 服务id
     */
    void deleteRouteCache(String serviceId);

    /**
     * 缓存Api 状态信息
     *
     * @param apis 接口集合
     */
    void cacheApis(ApiStatusList apis);

    /**
     * 缓存Api 状态信息
     *
     * @param apis 接口集合
     */
    void cacheApis(List<ApiStatusList> apis);

    /**
     * 获取Api状态是否启用
     *
     * @param serviceId 服务id
     * @return
     */
    Map<Object, Object> getServiceApiStatusList(String serviceId);

    /**
     * 移除Api状态缓存
     *
     * @param serviceId 服务id
     * @param method    请求方法
     * @param pattern   匹配模式
     */
    void deleteApiCache(String serviceId, HttpMethod method, String pattern);

    /**
     * 移除Api状态缓存
     *
     * @param serviceId   服务id
     * @param method      请求方法
     * @param patternList 匹配模式
     */
    void deleteApiCaches(String serviceId, HttpMethod method, List<String> patternList);
}
