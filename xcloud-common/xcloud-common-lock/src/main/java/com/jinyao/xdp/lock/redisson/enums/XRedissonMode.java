package com.jinyao.xdp.lock.redisson.enums;

/**
 * redisson所采用的模式
 * @author 谢进伟
 * @createDate 2022/8/26 08:40
 */
public enum XRedissonMode {
    /**
     * 集群模式
     */
    CLUSTER,
    /**
     * 复制模式
     */
    REPLICATED,
    /**
     * 单实例模式
     */
    SINGLE_INSTANCE,
    /**
     * 哨兵模式
     */
    SENTINEL,
    /**
     * 主从模式
     */
    MASTER_SLAVE;

    public static final String CLUSTER_NAME = "CLUSTER";
    public static final String REPLICATED_NAME = "REPLICATED";
    public static final String SINGLE_INSTANCE_NAME = "SINGLE_INSTANCE";
    public static final String SENTINEL_NAME = "SENTINEL";
    public static final String MASTER_SLAVE_NAME = "MASTER_SLAVE";

}
