package com.jinyao.xdp.lock.redisson.properties.mode;

import com.jinyao.xdp.lock.redisson.enums.XNatMapperEnum;
import com.jinyao.xdp.lock.redisson.properties.mode.base.XBaseMasterSlaveServersConfigProperties;
import lombok.Data;
import org.redisson.api.HostNatMapper;
import org.redisson.api.HostPortNatMapper;
import org.redisson.api.NatMapper;
import org.redisson.config.SentinelServersConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

import static com.jinyao.xdp.lock.redisson.enums.XRedissonMode.SENTINEL_NAME;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_MODE;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * 哨兵模式配置 {@link SentinelServersConfig}
 *
 * @author 谢进伟
 * @createDate 2022/8/26 09:29
 */
@Data
@ConfigurationProperties(prefix = REDISSON_PROPERTIES_PREFIX + XSentinelServersConfigProperties.PREFIX)
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = REDISSON_MODE, havingValue = SENTINEL_NAME)
public class XSentinelServersConfigProperties extends XBaseMasterSlaveServersConfigProperties {

	public static final String PREFIX = ".sentinel-servers";

	/**
	 * Redis Sentinel 节点地址，采用 host:port 格式
	 */
	private List<String> sentinelAddresses = new ArrayList<>();
	/**
	 * 定义映射 Redis URI 对象的 NAT 映射器接口。它适用于所有 Redis 连接。
	 * 很少有实现：org.redisson.api.HostPortNatMapper 和 org.redisson.api.HostNatMapper。
	 */
	private XNatMapperEnum natMapper = XNatMapperEnum.DEFAULT_NAT_MAPPER;

	/**
	 * Redis Sentinel 服务器和主变更监控任务使用的主服务器名称。
	 */
	private String masterName;

	/**
	 * 用于身份验证的 Redis Sentinel 服务器的用户名。仅当 Sentinel 用户名与主从用户不同时使用。需要 Redis 6.0+
	 */
	private String sentinelUsername;

	/**
	 * Redis Sentinel 服务器身份验证的密码。仅当 Sentinel 密码与主从密码不同时使用。
	 */
	private String sentinelPassword;

	/**
	 * 用于Redis连接的数据库索引
	 */
	private Integer database = 0;

	/**
	 * 哨兵扫描间隔(毫秒)
	 */
	private Integer scanInterval = 1000;

	/**
	 * 在 Redisson 启动期间启用哨兵列表检查。
	 */
	private Boolean checkSentinelsList = true;

	/**
	 * 检查从节点 master-link-status 字段的状态是否正常。
	 */
	private Boolean checkSlaveStatusWithSyncing = true;

	/**
	 * 启用哨兵发现
	 */
	private Boolean sentinelsDiscovery = true;

	public void copyPropertiesToConfig(SentinelServersConfig config) {
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
				.setMasterName(this.getMasterName())
				.setSentinelUsername(this.getSentinelUsername())
				.setSentinelPassword(this.getSentinelPassword())
				.setDatabase(this.getDatabase())
				.setScanInterval(this.getScanInterval())
				.setCheckSentinelsList(this.getCheckSentinelsList())
				.setCheckSlaveStatusWithSyncing(this.getCheckSlaveStatusWithSyncing())
				.setSentinelsDiscovery(this.getSentinelsDiscovery())
				.setSentinelAddresses(this.getSentinelAddresses());
	}
}
