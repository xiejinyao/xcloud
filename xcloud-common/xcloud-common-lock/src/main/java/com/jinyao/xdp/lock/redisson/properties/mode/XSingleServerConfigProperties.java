package com.jinyao.xdp.lock.redisson.properties.mode;

import com.jinyao.xdp.lock.redisson.properties.mode.base.XBaseConfigProperties;
import lombok.Data;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.jinyao.xdp.lock.redisson.enums.XRedissonMode.SINGLE_INSTANCE_NAME;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_MODE;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * 单实例模式配置 {@link SingleServerConfig}
 * @author 谢进伟
 * @createDate 2022/8/26 09:36
 */
@Data
@ConfigurationProperties(prefix = REDISSON_PROPERTIES_PREFIX + XSingleServerConfigProperties.PREFIX)
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = REDISSON_MODE, havingValue = SINGLE_INSTANCE_NAME)
public class XSingleServerConfigProperties extends XBaseConfigProperties {

    public static final String PREFIX = ".single-server";

    /**
     * Redis 服务器地址，采用 host:port 格式。使用 redis:// 协议进行 SSL 连接。
     */
    private String address;

    /**
     * 最小空闲 Redis 订阅连接量。
     */
    private Integer subscriptionConnectionMinimumIdleSize = 1;

    /**
     * 最小空闲 Redis 订阅连接量。
     */
    private Integer subscriptionConnectionPoolSize = 50;

    /**
     * 最小空闲 Redis 连接量。
     */
    private Integer connectionMinimumIdleSize = 24;

    /**
     * Redis 连接最大池大小
     */
    private Integer connectionPoolSize = 64;

    /**
     * 用于 Redis 连接的数据库索引
     */
    private Integer database = 0;

    /**
     * DNS 更改监控间隔。应用程序必须确保 JVM DNS 缓存 TTL 足够低以支持这一点。设置 -1 禁用。代理模式支持单个主机名的多个 IP 绑定。
     */
    private Long dnsMonitoringInterval = 5000L;


    public void copyPropertiesToConfig(SingleServerConfig config) {
        super.copyPropertiesToConfig(config);

        config.setAddress(this.getAddress())
                .setSubscriptionConnectionMinimumIdleSize(this.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(this.getSubscriptionConnectionPoolSize())
                .setConnectionMinimumIdleSize(this.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(this.getConnectionPoolSize())
                .setDatabase(this.getDatabase())
                .setDnsMonitoringInterval(this.getDnsMonitoringInterval());
    }
}
