package com.xjinyao.xcloud.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.xjinyao.xcloud.admin.config.NacosGatewayConfig;
import com.xjinyao.xcloud.common.core.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

/**
 * @author 谢进伟
 * @description nacos 配置文件控制器
 * @createDate 2020/11/17 15:29
 */
@RestController
@RequestMapping("/nacos/config")
@Api(value = "nacos 配置文件控制器", tags = "nacos 配置文件控制器")
public class NacosConfigController {

	private ConfigService configService;
	private NacosGatewayConfig nacosGatewayConfig;

	public NacosConfigController(NacosGatewayConfig nacosGatewayConfig) throws NacosException {
		this.nacosGatewayConfig = nacosGatewayConfig;
		Properties properties = new Properties();
		properties.setProperty("serverAddr", nacosGatewayConfig.nacosServerAddr);
		properties.setProperty("namespace", nacosGatewayConfig.nacosNamespace);
		configService = NacosFactory.createConfigService(properties);
	}

	@GetMapping("getRouteDataConfig")
	@ApiOperation(value = "发布路由", notes = "获取路由配置")
	@PreAuthorize("@pms.hasPermission('get_route_data_config')")
	public R getRouteDataConfig() throws NacosException {
		String config = configService.getConfig(nacosGatewayConfig.getNacosRouteDataId(),
				nacosGatewayConfig.getNacosRouteDataGroup(),
				NacosGatewayConfig.DEFAULT_TIMEOUT);
		return R.ok(JSON.parse(config, Feature.OrderedField));
	}

	@PostMapping("publish")
	@ApiOperation(value = "发布路由", notes = "发布路由")
	@PreAuthorize("@pms.hasPermission('publish_route_data_config')")
	public R publish(@RequestBody String content) throws NacosException {
		return R.ok(configService.publishConfig(nacosGatewayConfig.getNacosRouteDataId(),
				nacosGatewayConfig.getNacosRouteDataGroup(), content));
	}

}
