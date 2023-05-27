package com.jinyao.xdp.lock.redisson.properties.mode;

import com.jinyao.xdp.lock.redisson.properties.mode.base.XBaseMasterSlaveServersConfigProperties;
import lombok.Data;
import org.redisson.config.MasterSlaveServersConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

import static com.jinyao.xdp.lock.redisson.enums.XRedissonMode.MASTER_SLAVE_NAME;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_MODE;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * 主从模式配置 {@link  MasterSlaveServersConfig}
 * @author 谢进伟
 * @createDate 2022/8/26 09:24
 */
@Data
@ConfigurationProperties(prefix = REDISSON_PROPERTIES_PREFIX + XMasterSlaveServersConfigProperties.PREFIX)
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = REDISSON_MODE, havingValue = MASTER_SLAVE_NAME)
public class XMasterSlaveServersConfigProperties extends XBaseMasterSlaveServersConfigProperties {

    public static final String PREFIX = ".master-slave-servers";

    /**
     * Redis从服务器地址
     */
    private Set<String> slaveAddresses = new HashSet<>();

    /**
     * Redis 主节点地址，采用 host:port 格式。使用 redis:// 协议进行 SSL 连接。
     */
    private String masterAddress;

    /**
     * 用于 Redis 连接的数据库索引
     */
    private Integer database = 0;

    public void copyPropertiesToConfig(MasterSlaveServersConfig config) {
        super.copyPropertiesToConfig(config);

        config.setMasterAddress(this.getMasterAddress())
                .setDatabase(this.getDatabase())
                .setSlaveAddresses(slaveAddresses);
    }
}
