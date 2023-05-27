package com.jinyao.xdp.lock.redisson.properties.mode;

import com.jinyao.xdp.lock.redisson.enums.XNatMapperEnum;
import com.jinyao.xdp.lock.redisson.properties.mode.base.XBaseMasterSlaveServersConfigProperties;
import lombok.Data;
import org.redisson.api.HostNatMapper;
import org.redisson.api.HostPortNatMapper;
import org.redisson.api.NatMapper;
import org.redisson.config.ClusterServersConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.jinyao.xdp.lock.redisson.enums.XRedissonMode.CLUSTER_NAME;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_MODE;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * 集群模式配置参数 {@link ClusterServersConfig}
 *
 * @author 谢进伟
 * @createDate 2022/8/26 08:43
 */
@Data
@ConfigurationProperties(prefix = REDISSON_PROPERTIES_PREFIX + XClusterServersConfigProperties.PREFIX)
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = REDISSON_MODE, havingValue = CLUSTER_NAME)
public class XClusterServersConfigProperties extends XBaseMasterSlaveServersConfigProperties {

	public static final String PREFIX = ".cluster-servers";

	/**
	 * 定义映射 Redis URI 对象的 NAT 映射器接口。它适用于所有 Redis 连接。
	 * 很少有实现：org.redisson.api.HostPortNatMapper 和 org.redisson.api.HostNatMapper。
	 */
	private XNatMapperEnum natMapper = XNatMapperEnum.DEFAULT_NAT_MAPPER;

	/**
	 * 以 host:port 格式添加 Redis 集群节点地址。可以一次添加多个节点。应至少指定一个来自 Redis 集群的节点。 Redisson 自动发现集群拓扑。使用 redis:// 协议进行 SSL 连接。
	 */
	private List<String> nodeAddresses = new ArrayList<>();

	/**
	 * Redis 集群扫描间隔，以毫秒为单位。
	 */
	private Integer scanInterval = 5000;

	/**
	 * 在 Redisson 启动期间启用集群插槽检查。
	 */
	private Boolean checkSlotsCoverage = true;


	public void copyPropertiesToConfig(ClusterServersConfig config) {
		super.copyPropertiesToConfig(config);

		NatMapper natMapper = null;
		switch (this.getNatMapper()) {
			case DEFAULT_NAT_MAPPER:
				natMapper = NatMapper.direct();
				break;
			case HOST_NAT_MAPPER:
				natMapper = new HostNatMapper();
				break;
			case HOST_PORT_NAT_MAPPER:
				natMapper = new HostPortNatMapper();
		}

		config.setNatMapper(natMapper)
				.setScanInterval(this.getScanInterval())
				.setCheckSlotsCoverage(this.getCheckSlotsCoverage())
				.setNodeAddresses(this.getNodeAddresses());
	}
}
