package com.xjinyao.xcloud.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.entity.SysRoute;
import com.xjinyao.xcloud.admin.api.vo.SysRoutePageVO;
import com.xjinyao.xcloud.admin.api.vo.SysRouteVO;
import com.xjinyao.xcloud.admin.config.NacosGatewayConfig;
import com.xjinyao.xcloud.admin.service.ISysRouteService;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.mybatis.wrappers.HightQueryWrapper;
import com.xjinyao.xcloud.core.rule.po.RouterStatusList;
import com.xjinyao.xcloud.core.rule.service.IRuleCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 系统路由表
 *
 * @author 谢进伟
 * @date 2020-11-10 10:38:03
 */
@RestController
@RequestMapping("/route")
@Api(value = "系统路由表", tags = "系统路由表接口")
public class SysRouteController {

	private final ISysRouteService sysRouteService;
	private final IRuleCacheService ruleCacheService;

	private NamingMaintainService maintainService;
	private NamingService namingService;
	private NacosGatewayConfig nacosGatewayConfig;

	public SysRouteController(ISysRouteService sysRouteService, IRuleCacheService ruleCacheService,
							  NacosGatewayConfig nacosGatewayConfig) throws NacosException {
		this.sysRouteService = sysRouteService;
		this.ruleCacheService = ruleCacheService;
		this.nacosGatewayConfig = nacosGatewayConfig;

		Properties properties = new Properties();
		properties.setProperty("serverAddr", nacosGatewayConfig.getNacosServerAddr());
		properties.setProperty("namespace", nacosGatewayConfig.getNacosNamespace());
		maintainService = NacosFactory.createMaintainService(properties);
		namingService = NacosFactory.createNamingService(properties);
	}

	@ApiOperation(value = "分页查询", notes = "分页查询")
	@GetMapping("/page")
	public R<Page<SysRoutePageVO>> getSysRoutePage(Page page, SysRoute sysRoute, HttpServletRequest request) {
		Page<SysRoute> p = sysRouteService.page(page, HightQueryWrapper.wrapper(sysRoute, request.getParameterMap()));
		Page<SysRoutePageVO> resultPage = new Page(p.getCurrent(), p.getSize(), p.getTotal());
		List<SysRoutePageVO> voList = new ArrayList<>();
		resultPage.setRecords(voList);
		List<SysRoute> records = p.getRecords();
		if (CollectionUtil.isNotEmpty(records)) {
			records.forEach(sr -> {
				SysRoutePageVO routePageVO = new SysRoutePageVO();
				BeanUtils.copyProperties(sr, routePageVO);
				routePageVO.setRunStatus("off-line");
				routePageVO.setInstanceCount(0);
				routePageVO.setEnableInstanceCount(0);
				routePageVO.setHealthyInstanceCount(0);
				String serviceName = sr.getServiceId();
				try {
					List<Instance> allInstances = namingService.getAllInstances(serviceName, nacosGatewayConfig.getNacosRouteGroup());
					if (CollectionUtil.isNotEmpty(allInstances)) {
						AtomicInteger enableInstanceCount = new AtomicInteger();
						AtomicInteger healthyInstanceCount = new AtomicInteger();
						allInstances.forEach(instance -> {
							if (instance.isEnabled()) {
								enableInstanceCount.getAndIncrement();
							}
							if (instance.isHealthy()) {
								healthyInstanceCount.getAndIncrement();
							}
						});
						routePageVO.setInstanceCount(allInstances.size());
						routePageVO.setEnableInstanceCount(enableInstanceCount.get());
						routePageVO.setHealthyInstanceCount(healthyInstanceCount.get());
						routePageVO.setRunStatus("on-line");
					}
				} catch (NacosException e) {
					e.printStackTrace();
				}
				voList.add(routePageVO);
			});
		}
		return R.ok(resultPage);
	}

	@ApiOperation(value = "通过id查询", notes = "通过id查询")
	@GetMapping("/{id}")
	public R<SysRoute> getById(@PathVariable("id") Long id) {
		return R.ok(sysRouteService.getById(id));
	}

	@ApiOperation(value = "新增系统路由表", notes = "新增系统路由表(权限标识:'admin_sys_route_add')")
	@SysLog("新增系统路由表")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('admin_sys_route_add')")
	public R<Boolean> save(@RequestBody SysRoute sysRoute) {
		boolean save = sysRouteService.save(sysRoute);
		if (save) {
			ruleCacheService.cacheRoutes(Collections.singletonList(
					new RouterStatusList(sysRoute.getServiceId(), sysRoute.getStatus())));
		}
		return R.ok(save);
	}

	@ApiOperation(value = "修改系统路由表", notes = "修改系统路由表(权限标识:'admin_sys_route_edit')")
	@SysLog("修改系统路由表")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('admin_sys_route_edit')")
	public R<Boolean> updateById(@RequestBody SysRoute sysRoute) {
		SysRoute existsRoute = sysRouteService.getById(sysRoute.getId());
		if (existsRoute != null) {
			Boolean status = existsRoute.getStatus();
			if (sysRouteService.updateById(sysRoute)) {
				Boolean status1 = sysRoute.getStatus();
				if (status != null && status1 != null && status != status1) {
					ruleCacheService.deleteRouteCache(existsRoute.getServiceId());
					ruleCacheService.deleteRouteCache(sysRoute.getServiceId());
					ruleCacheService.cacheRoutes(Collections.singletonList(
							new RouterStatusList(sysRoute.getServiceId(), sysRoute.getStatus())));
				}
				return R.ok(Boolean.TRUE);
			} else {
				return R.failed("修改路由信息失败!");
			}
		} else {
			return R.failed("路由信息不存在!");
		}
	}

	@ApiOperation(value = "通过id删除系统路由表", notes = "通过id删除系统路由表(权限标识:'admin_sys_route_del')")
	@SysLog("通过id删除系统路由表")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('admin_sys_route_del')")
	public R<Boolean> removeById(@PathVariable Long id) {
		SysRoute sysRoute = sysRouteService.getById(id);
		if (sysRoute != null) {
			if (sysRouteService.removeById(id)) {
				ruleCacheService.deleteRouteCache(sysRoute.getServiceId());
				return R.ok(Boolean.TRUE);
			} else {
				return R.failed("删除失败!");
			}
		} else {
			return R.failed("路由信息不存在!");
		}
	}

	@GetMapping("/list-item")
	@ApiOperation(value = "系统路由列表", notes = "系统路由列表")
	public R<List<SysRouteVO>> listItem() throws NacosException {
		List<SysRouteVO> data = sysRouteService.listItem();
		if (CollectionUtil.isNotEmpty(data)) {
			ListView<String> servicesOfServer = namingService.getServicesOfServer(1, Integer.MAX_VALUE,
                    nacosGatewayConfig.getNacosRouteGroup());
			List<String> registeredServiceNames = servicesOfServer.getData();
			if (CollectionUtil.isNotEmpty(registeredServiceNames)) {
				data.forEach(d -> {
					if (registeredServiceNames.contains(d.getServiceId())) {
						d.setStatus("up");
					} else {
						d.setStatus("down");
					}
				});
			}
		}
		return R.ok(data);
	}

	@ApiOperation(value = "获取指定服务的实例信息", notes = "获取指定服务的实例信息(权限标识:'admin_sys_route_instance_infos')")
	@PreAuthorize("@pms.hasPermission('admin_sys_route_instance_infos')")
	@GetMapping("/instance/{serviceName}")
	public R<List<Instance>> getAllInstances(@ApiParam("服务名")
											 @PathVariable("serviceName") String serviceName) throws NacosException {
		return R.ok(namingService.getAllInstances(serviceName, nacosGatewayConfig.getNacosRouteGroup()));
	}

	@ApiOperation(value = "修改指定服务的实例信息",
			notes = "修改指定服务的实例信息(权限标识:'admin_sys_route_instance_set_update')")
	@PreAuthorize("@pms.hasPermission('admin_sys_route_instance_set_update')")
	@PutMapping("/instance")
	public R<Boolean> updateInstance(@ApiParam("服务名")
									 @RequestParam String serviceName,
									 @ApiParam("实列Id")
									 @RequestParam String instanceId,
									 @ApiParam("权重值")
									 @RequestParam Double weight,
									 @ApiParam("是否上线")
									 @RequestParam Boolean enabled) throws NacosException {
		String group = nacosGatewayConfig.getNacosRouteGroup();
		Instance instance = getInstance(serviceName, group, instanceId);
		if (instance != null) {
			instance.setWeight(weight);
			instance.setEnabled(enabled);
			maintainService.updateInstance(serviceName, group, instance);
		}
		return R.ok(Boolean.TRUE);
	}

	private Instance getInstance(String serviceName, String group, String instanceId) throws NacosException {
		List<Instance> allInstances = namingService.getAllInstances(serviceName, group);
		Optional<Instance> first = allInstances.parallelStream()
				.filter(in -> in.getInstanceId().equals(instanceId))
				.findFirst();
		if (first.isPresent()) {
			return first.get();
		}
		return null;
	}
}
