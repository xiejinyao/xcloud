package com.xjinyao.xcloud.admin.controller.innner;

import com.xjinyao.xcloud.admin.api.constants.ControllerMapping;
import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import com.xjinyao.xcloud.admin.api.vo.SysOrganizationVO;
import com.xjinyao.xcloud.admin.service.SysOrganizationService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.BeanUtils;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 内部接口：组织结构管理
 *
 * @author 谢进伟
 * @createDate 2022/11/16 11:26
 */
@ApiIgnore
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerMapping.SYS_ORGANIZATION_CONTROLLER_MAPPING)
public class InnerSysOrganizationController {

	private final SysOrganizationService sysOrganizationService;


	/**
	 * 通过组织Id获取组织信息
	 *
	 * @param id 组织Id
	 * @return
	 */
	@Inner
	@GetMapping("/get/{id}")
	@Cacheable(value = CacheConstants.ORGANIZATION, key = "#id", unless = "#result.data != null")
	public R<SysOrganizationVO> getById(@PathVariable("id") String id) {
		SysOrganization sysOrganization = sysOrganizationService.lambdaQuery()
				.eq(SysOrganization::getId, id)
				.oneOpt()
				.orElse(null);
		return R.ok(BeanUtils.copyPropertiesAndGetTarget(sysOrganization, new SysOrganizationVO()));
	}

	/**
	 * 通过组织Id集合批量获取组织信息
	 *
	 * @param ids 组织Id集合
	 * @return
	 */
	@Inner
	@PostMapping("/getByIds")
	public R<Map<String, SysOrganizationVO>> getByIds(@RequestBody List<String> ids) {
		List<SysOrganization> list = sysOrganizationService.lambdaQuery()
				.in(SysOrganization::getId, ids)
				.list();
		return R.ok(Optional.ofNullable(list)
				.orElse(Collections.emptyList())
				.stream()
				.map(d -> BeanUtils.copyPropertiesAndGetTarget(d, new SysOrganizationVO()))
				.collect(Collectors.toMap(SysOrganizationVO::getId, Function.identity())));
	}


	/**
	 * 获取所有子节点id
	 *
	 * @param parentId 组织id
	 * @return {@link List}<{@link String}>
	 */
	@Inner
	@GetMapping("/getChildrenIds")
	public R<List<String>> getChildrenIds(@RequestParam String parentId) {
		return R.ok(sysOrganizationService.getChildrenIds(parentId));
	}

	/**
	 * 获取同一级别的节点Id
	 *
	 * @param id              id
	 * @param includeChildren 是否包含子节点
	 * @return {@link R}<{@link List}<{@link String}>>
	 */
	@Inner
	@GetMapping("/getSiblingsChildrenIds")
	public R<List<String>> getSiblingsChildrenIds(@RequestParam String id,
												  @RequestParam(required = false, defaultValue = "false")
												  Boolean includeChildren) {
		return R.ok(sysOrganizationService.getSiblingsChildrenIds(id, includeChildren));
	}
}
