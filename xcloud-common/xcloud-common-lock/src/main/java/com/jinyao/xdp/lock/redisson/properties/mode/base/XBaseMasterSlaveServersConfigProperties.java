package com.jinyao.xdp.lock.redisson.properties.mode.base;

import com.jinyao.xdp.lock.redisson.enums.XLoadBalancerEnum;
import lombok.Data;
import org.redisson.config.BaseMasterSlaveServersConfig;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.redisson.connection.balancer.LoadBalancer;
import org.redisson.connection.balancer.RandomLoadBalancer;
import org.redisson.connection.balancer.RoundRobinLoadBalancer;
import org.redisson.connection.balancer.WeightedRoundRobinBalancer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link BaseMasterSlaveServersConfig}
 *
 * @author 谢进伟
 * @createDate 2022/8/26 09:03
 */
@Data
public class XBaseMasterSlaveServersConfigProperties extends XBaseConfigProperties {


	/**
	 * 连接多个 Redis 服务器的负载均衡器。可用的实现：
	 * org.redisson.connection.balancer.WeightedRoundRobinBalancer
	 * org.redisson.connection.balancer.RoundRobinLoadBalancer
	 * org.redisson.connection.balancer.RandomLoadBalancer
	 */
	protected XLoadBalancerEnum loadBalancer = XLoadBalancerEnum.ROUND_ROBIN_LOAD_BALANCER;

	/**
	 * 从节点地址映射的权重，格式为redis://host:port
	 * 当参数loadBalancer为WEIGHTED_ROUND_ROBIN_BALANCER时有效
	 */
	protected List<Weights> weights;

	/**
	 * 指定给权重映射中未定义的从节点的默认权重值
	 * 当参数loadBalancer为WEIGHTED_ROUND_ROBIN_BALANCER时有效
	 */
	private Integer defaultWeight;

	/**
	 * Redis 'slave' 节点每个从节点的最小空闲连接量
	 */
	protected Integer slaveConnectionMinimumIdleSize = 24;

	/**
	 * Redis“从”节点每个从节点的最大连接池大小
	 */
	protected Integer slaveConnectionPoolSize = 64;

	/**
	 * Redis Slave 从可用服务器的内部列表中排除时的重新连接尝试间隔。在每个超时事件上，Redisson 都会尝试连接到断开连接的 Redis 服务器。以毫秒为单位的值。
	 */
	protected Integer failedSlaveReconnectionInterval = 3000;

	/**
	 * 当此服务器上第一次 Redis 命令执行失败的时间间隔达到定义值时，Redis Slave 节点执行命令失败从可用节点的内部列表中排除。以毫秒为单位的值。
	 */
	protected Integer failedSlaveCheckInterval = 180000;

	/**
	 * Redis 'master' 节点每个主节点的最小空闲连接量。主节点也被添加为从节点，并将空闲连接保持为从节点。这些连接保留在从连接池中，但处于非活动状态。
	 * 当您的集群丢失所有从属服务器时，它们会被保留。在这种情况下，主机开始用作从机，空闲连接变为活动状态。
	 */
	protected Integer masterConnectionMinimumIdleSize = 24;

	/**
	 * Redis 'master' 节点最大连接池大小
	 */
	protected Integer masterConnectionPoolSize = 64;

	/**
	 * 设置用于读取操作的节点类型。
	 */
	protected ReadMode readMode = ReadMode.SLAVE;

	/**
	 * 设置用于订阅操作的节点类型
	 */
	protected SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;

	/**
	 * 订阅（发布/订阅）频道的最小空闲连接池大小。由 RTopic、RPatternTopic、RLock、RSemaphore、RCountDownLatch、RClusteredLocalCachedMap
	 * 、RClusteredLocalCachedMapCache、RLocalCachedMap、RLocalCachedMapCache 对象和 Hibernate READ_WRITE 缓存策略使用。
	 */
	protected Integer subscriptionConnectionMinimumIdleSize = 1;

	/**
	 * 订阅（发布/订阅）频道的最大连接池大小。由 RTopic、RPatternTopic、RLock、RSemaphore、RCountDownLatch、RClusteredLocalCachedMap、
	 * RClusteredLocalCachedMapCache、RLocalCachedMap、RLocalCachedMapCache 对象和 Hibernate READ_WRITE 缓存策略使用
	 */
	protected Integer subscriptionConnectionPoolSize = 50;

	/**
	 * 检查端点 DNS 的时间间隔（以毫秒为单位）。应用程序必须确保 JVM DNS 缓存 TTL 足够低以支持这一点。设置 -1 禁用。
	 */
	protected Long dnsMonitoringInterval = 5000L;

	@Data
	public static class Weights {
		/**
		 * 从节点地址，格式为redis://host:port
		 */
		private String host;
		/**
		 * 权重
		 */
		private Integer weight = 0;
	}

	public void copyPropertiesToConfig(BaseMasterSlaveServersConfig config) {
		super.copyPropertiesToConfig(config);

		LoadBalancer loadBalancer = new RoundRobinLoadBalancer();
		switch (this.getLoadBalancer()) {
			case WEIGHTED_ROUND_ROBIN_BALANCER:
				loadBalancer = new WeightedRoundRobinBalancer(this.getWeights()
						.stream()
						.collect(Collectors.toMap(Weights::getHost, Weights::getWeight, (o1, o2) -> o2)),
						this.getDefaultWeight());
				break;
			case RANDOM_LOAD_BALANCER:
				loadBalancer = new RandomLoadBalancer();
				break;
			case ROUND_ROBIN_LOAD_BALANCER:
				loadBalancer = new RoundRobinLoadBalancer();
		}

		config.setLoadBalancer(loadBalancer)
				.setSlaveConnectionMinimumIdleSize(this.getSlaveConnectionMinimumIdleSize())
				.setSlaveConnectionPoolSize(this.getSlaveConnectionPoolSize())
				.setFailedSlaveReconnectionInterval(this.getFailedSlaveReconnectionInterval())
				.setFailedSlaveCheckInterval(this.getFailedSlaveCheckInterval())
				.setMasterConnectionMinimumIdleSize(this.getMasterConnectionMinimumIdleSize())
				.setMasterConnectionPoolSize(this.getMasterConnectionPoolSize())
				.setReadMode(this.getReadMode())
				.setSubscriptionMode(this.getSubscriptionMode())
				.setSubscriptionConnectionMinimumIdleSize(this.getSubscriptionConnectionMinimumIdleSize())
				.setSubscriptionConnectionPoolSize(this.getSubscriptionConnectionPoolSize())
				.setDnsMonitoringInterval(this.getDnsMonitoringInterval());
	}
}
