package com.xjinyao.xcloud.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.xjinyao.xcloud.gateway.config.NacosGatewayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

@Slf4j
@Component
@DependsOn({"nacosGatewayConfig"})
public class NacosDynamicRouteServiceImpl {


	private final DynamicRouteServiceImpl dynamicRouteService;

	private ConfigService configService;

	private final GatewayProperties gatewayProperties;

	public NacosDynamicRouteServiceImpl(DynamicRouteServiceImpl dynamicRouteService,
										GatewayProperties gatewayProperties) {
		this.dynamicRouteService = dynamicRouteService;
		this.gatewayProperties = gatewayProperties;
	}

	@PostConstruct
	public void init() {
		log.info("gateway route init...");
		try {
			configService = initConfigService();
			if (configService == null) {
				log.warn("initConfigService fail");
				return;
			}
			String configInfo = configService.getConfig(NacosGatewayConfig.NACOS_ROUTE_DATA_ID,
					NacosGatewayConfig.NACOS_ROUTE_DATA_GROUP, NacosGatewayConfig.DEFAULT_TIMEOUT);
			if (StringUtils.isNotBlank(configInfo)) {
				log.info("获取网关当前配置:\r\n{}", configInfo);
				List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
				for (RouteDefinition definition : definitionList) {
					log.info("update route : {}", definition.toString());
					dynamicRouteService.add(definition);
				}
				gatewayProperties.setRoutes(definitionList);
			} else {
				log.error("无法获取网关当前配置，daaId:{},group:{}", NacosGatewayConfig.NACOS_ROUTE_DATA_ID,
						NacosGatewayConfig.NACOS_ROUTE_DATA_GROUP);
			}
		} catch (Exception e) {
			log.error("初始化网关路由时发生错误", e);
		}
		dynamicRouteByNacosListener(NacosGatewayConfig.NACOS_ROUTE_DATA_ID, NacosGatewayConfig.NACOS_ROUTE_DATA_GROUP);
	}

	/**
	 * URL 匹配规则和服务的对应关系
	 *
	 * @return
	 */
	public Map<String, String> getServicePredicatesMap() {
		Map<String, String> map = new HashMap<>();
		List<RouteDefinition> routes = gatewayProperties.getRoutes();
		if (!CollectionUtils.isEmpty(routes)) {
			gatewayProperties.getRoutes().forEach(d -> {
				URI uri = d.getUri();
				String service = uri.getHost();
				List<PredicateDefinition> predicates = d.getPredicates();
				predicates.forEach(p -> {
					Map<String, String> args = p.getArgs();
					if (!CollectionUtils.isEmpty(args)) {
						args.forEach((k, v) -> map.put(v, service));
					}
				});
			});
		}
		return map;
	}

	/**
	 * 监听Nacos下发的动态路由配置
	 *
	 * @param dataId
	 * @param group
	 */
	public void dynamicRouteByNacosListener(String dataId, String group) {
		try {
			configService.addListener(dataId, group, new Listener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					log.info("进行网关更新:\n\r{}", configInfo);
					List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
					for (RouteDefinition definition : definitionList) {
						log.info("update route : {}", definition.toString());
						dynamicRouteService.update(definition);
					}
					gatewayProperties.setRoutes(definitionList);
				}

				@Override
				public Executor getExecutor() {
					log.info("getExecutor\n\r");
					return null;
				}
			});
		} catch (NacosException e) {
			log.error("从nacos接收动态路由配置出错!!!", e);
		}
	}

	/**
	 * 初始化网关路由 nacos config
	 *
	 * @return
	 */
	private ConfigService initConfigService() {
		try {
			Properties properties = new Properties();
			properties.setProperty("serverAddr", NacosGatewayConfig.NACOS_SERVER_ADDR);
			properties.setProperty("namespace", NacosGatewayConfig.NACOS_NAMESPACE);
			return configService = NacosFactory.createConfigService(properties);
		} catch (Exception e) {
			log.error("初始化网关路由时发生错误", e);
			return null;
		}
	}

}
