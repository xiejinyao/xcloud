package com.jinyao.xdp.lock.redisson.properties.mode;

import com.jinyao.xdp.lock.redisson.properties.mode.base.XBaseMasterSlaveServersConfigProperties;
import lombok.Data;
import org.redisson.config.ReplicatedServersConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.jinyao.xdp.lock.redisson.enums.XRedissonMode.REPLICATED_NAME;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_MODE;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * 复制模式 {@link ReplicatedServersConfig}
 * @author 谢进伟
 * @createDate 2022/8/26 09:26
 */
@Data
@ConfigurationProperties(prefix = REDISSON_PROPERTIES_PREFIX + XReplicatedServersConfigProperties.PREFIX)
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = REDISSON_MODE, havingValue = REPLICATED_NAME)
public class XReplicatedServersConfigProperties extends XBaseMasterSlaveServersConfigProperties {

    public static final String PREFIX = ".replicated-servers";

    /**
     * 以 host:port 格式添加 Redis 节点地址。可以一次添加多个节点。应定义所有节点（主节点和从节点）。对于 Aiven Redis 托管单个主机名就足够了。使用 redis:// 协议进行 SSL 连接。
     */
    private List<String> nodeAddresses = new ArrayList<>();

    /**
     * 以毫秒为单位的复制节点扫描间隔。
     */
    private Integer scanInterval = 1000;

    /**
     * 用于 Redis 连接的数据库索引
     */
    private Integer database = 0;

    public void copyPropertiesToConfig(ReplicatedServersConfig config) {
        super.copyPropertiesToConfig(config);

        config.setScanInterval(this.getScanInterval())
                .setDatabase(this.getDatabase())
                .setNodeAddresses(this.getNodeAddresses());


    }
}
