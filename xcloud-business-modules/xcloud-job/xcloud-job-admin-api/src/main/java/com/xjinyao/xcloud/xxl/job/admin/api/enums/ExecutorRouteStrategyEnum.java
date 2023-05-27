package com.xjinyao.xcloud.xxl.job.admin.api.enums;

/**
 * 执行器路由策略
 *
 * @author 谢进伟
 * @createDate 2023/2/10 17:25
 */
public enum ExecutorRouteStrategyEnum {

    FIRST,
    LAST,
    ROUND,
    RANDOM,
    CONSISTENT_HASH,
    LEAST_FREQUENTLY_USED,
    LEAST_RECENTLY_USED,
    FAILOVER,
    BUSYOVER,
    SHARDING_BROADCAST;
}
