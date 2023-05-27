package com.xjinyao.xcloud.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网关路由配置
 *
 * @author 谢进伟
 * @createDate 2023/3/16 15:56
 */
@Data
@ConfigurationProperties(prefix = "gateway.route.config")
public class GatewayRouteProperties {

	/**
	 * 组
	 */
	private String group;

	/**
	 * 数据标识
	 */
	private String dataId;
}
