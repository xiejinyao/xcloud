package com.xjinyao.xcloud.admin.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xjinyao.xcloud.admin.properties.GatewayProperties;
import lombok.Getter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos路由工具类配置
 *
 * @author 谢进伟
 */
@Getter
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class NacosGatewayConfig {
	public static final long DEFAULT_TIMEOUT = 30000;

	public  String nacosServerAddr;

	public  String nacosNamespace;

	public  String nacosRouteGroup;

	public  String nacosRouteDataId;
	public  String nacosRouteDataGroup;

	NacosGatewayConfig(NacosDiscoveryProperties nacosDiscoveryProperties,
					   GatewayProperties gatewayProperties) {
		nacosServerAddr = nacosDiscoveryProperties.getServerAddr();
		nacosNamespace = nacosDiscoveryProperties.getNamespace();
		nacosRouteGroup = nacosDiscoveryProperties.getGroup();
		nacosRouteDataId = gatewayProperties.getDataId();
		nacosRouteDataGroup = gatewayProperties.getGroup();
	}
}
