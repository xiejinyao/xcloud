package com.xjinyao.xcloud.gateway.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xjinyao.xcloud.gateway.properties.GatewayRouteProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos路由工具类配置
 *
 * @author 谢进伟
 */
@Configuration
@EnableConfigurationProperties(GatewayRouteProperties.class)
public class NacosGatewayConfig {

	public static final long DEFAULT_TIMEOUT = 30000;

	public static String NACOS_SERVER_ADDR;

	public static String NACOS_NAMESPACE;

	public static String NACOS_ROUTE_GROUP;

	public static String NACOS_ROUTE_DATA_ID;

	public static String NACOS_ROUTE_DATA_GROUP;

	NacosGatewayConfig(NacosDiscoveryProperties nacosDiscoveryProperties,
					   GatewayRouteProperties gatewayRouteProperties) {
		NACOS_SERVER_ADDR = nacosDiscoveryProperties.getServerAddr();
		NACOS_NAMESPACE = nacosDiscoveryProperties.getNamespace();
		NACOS_ROUTE_GROUP = nacosDiscoveryProperties.getGroup();
		NACOS_ROUTE_DATA_ID = gatewayRouteProperties.getDataId();
		NACOS_ROUTE_DATA_GROUP = gatewayRouteProperties.getGroup();
	}
}
