package com.jinyao.xdp.lock.redisson.enums;

/**
 * 负载均衡策略枚举
 * @author 谢进伟
 * @createDate 2022/8/26 09:08
 */
public enum XLoadBalancerEnum {

    /**
     * 加权轮循平衡器
     * {@link org.redisson.connection.balancer.WeightedRoundRobinBalancer}
     */
    WEIGHTED_ROUND_ROBIN_BALANCER,
    /**
     * 轮询负载
     * {@link org.redisson.connection.balancer.RoundRobinLoadBalancer}
     */
    ROUND_ROBIN_LOAD_BALANCER,
    /**
     * 随机选择负载
     * {@link org.redisson.connection.balancer.RandomLoadBalancer}
     */
    RANDOM_LOAD_BALANCER
}
