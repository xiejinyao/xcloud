package com.jinyao.xdp.lock.redisson.config;

import com.jinyao.xdp.lock.redisson.XRedissonLook;
import com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties;
import com.jinyao.xdp.lock.redisson.properties.mode.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.connection.ConnectionListener;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import java.net.InetSocketAddress;

import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.ENABLED;
import static com.jinyao.xdp.lock.redisson.properties.XRedissonConfigProperties.REDISSON_PROPERTIES_PREFIX;

/**
 * Redis 锁配置，参考官方文档：https://github.com/redisson/redisson/wiki
 *
 * @author 谢进伟
 * @createDate 2022/8/25 14:46
 */
@Slf4j
@EnableConfigurationProperties({
		XRedissonConfigProperties.class,
		XClusterServersConfigProperties.class,
		XMasterSlaveServersConfigProperties.class,
		XReplicatedServersConfigProperties.class,
		XSentinelServersConfigProperties.class,
		XSingleServerConfigProperties.class
})
@ConditionalOnProperty(prefix = REDISSON_PROPERTIES_PREFIX, name = ENABLED, havingValue = "true")
public class XRedissonConfiguration {

	@Bean
	public XRedissonLook redissonLook(RedissonClient redissonClient) {
		return new XRedissonLook(redissonClient);
	}

	@Bean
	public RedissonClient redissonClient(XRedissonConfigProperties redissonConfigProperties,
										 XClusterServersConfigProperties clusterServersConfigProperties,
										 XMasterSlaveServersConfigProperties masterSlaveServersConfigProperties,
										 XReplicatedServersConfigProperties replicatedServersConfigProperties,
										 XSentinelServersConfigProperties sentinelServersConfigProperties,
										 XSingleServerConfigProperties singleServerConfigProperties) {
		Config config = new Config();

		redissonConfigProperties.copyPropertiesToConfig(config);

		switch (redissonConfigProperties.getModel()) {
			case CLUSTER:
				clusterServersConfigProperties.copyPropertiesToConfig(config.useClusterServers());
				break;
			case SENTINEL:
				sentinelServersConfigProperties.copyPropertiesToConfig(config.useSentinelServers());
				break;
			case REPLICATED:
				replicatedServersConfigProperties.copyPropertiesToConfig(config.useReplicatedServers());
				break;
			case MASTER_SLAVE:
				masterSlaveServersConfigProperties.copyPropertiesToConfig(config.useMasterSlaveServers());
				break;
			case SINGLE_INSTANCE:
				singleServerConfigProperties.copyPropertiesToConfig(config.useSingleServer());
		}

		config.setConnectionListener(new ConnectionListener() {

			@Override
			public void onConnect(InetSocketAddress inetSocketAddress) {
				log.info("redisson connect to {}", inetSocketAddress.getAddress());
			}

			@Override
			public void onDisconnect(InetSocketAddress inetSocketAddress) {
				log.info("redisson disconnect from {}", inetSocketAddress.getAddress());
			}
		});
		return Redisson.create(config);
	}

}
