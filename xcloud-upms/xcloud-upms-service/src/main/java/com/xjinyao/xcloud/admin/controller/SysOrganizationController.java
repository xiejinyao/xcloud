package com.xjinyao.xcloud.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjinyao.xcloud.admin.api.dto.OrganizationTreeNode;
import com.xjinyao.xcloud.admin.api.entity.SysOrganization;
import com.xjinyao.xcloud.admin.api.vo.SysOrganizationVO;
import com.xjinyao.xcloud.admin.service.SysOrganizationService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 组织管理 前端控制器
 * </p>
 *
 * @since 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/organization")
@Api(value = "organization", tags = "组织管理模块")
public class SysOrganizationController {

	private final SysOrganizationService sysOrganizationService;

	/**
	 * 通过ID查询
	 *
	 * @param id ID
	 * @return SysOrganization
	 */
	@ApiOperation(value = "通过ID查询", notes = "通过ID查询")
	@GetMapping("/{id}")
	public R<SysOrganizationVO> getById(@PathVariable String id,
										@ApiParam("是否包含所有上级节点Id")
										@RequestParam(required = false, defaultValue = "false") Boolean includeParentIds) {
		SysOrganizationVO vo = new SysOrganizationVO();
		SysOrganization sysOrganization = sysOrganizationService.lambdaQuery().eq(SysOrganization::getId, id).one();
		BeanUtils.copyProperties(sysOrganization, vo);
		R<SysOrganizationVO> ok = R.ok(vo);
		if (includeParentIds) {
			vo.setParentIds(sysOrganizationService.getOrganizationParentIds(id));
		}
		return ok;
	}

	/**
	 * 通过父节点Id查询树形菜单集合
	 *
	 * @param parentId                         父节点id
	 * @param excludeNotCanSelectOrganizations 是否排除不能选择的组织
	 * @return SysOrganization
	 */
	@ApiOperation(value = "通过父节点Id查询树形菜单集合", notes = "通过父节点Id查询树形菜单集合")
	@GetMapping("/tree/parentId/{parentId}")
	public R<List<OrganizationTreeNode>> getOrganizationByParentId(
			@ApiParam("父节点id")
			@PathVariable(value = "parentId") String parentId,
			@ApiParam("是否排除不能选择的组织")
			@RequestParam(required = false, defaultValue = "false") Boolean excludeNotCanSelectOrganizations
	) {
		return R.ok(sysOrganizationService.getOrganizationByParentId(SecurityUtils.getUserId(), parentId,
				excludeNotCanSelectOrganizations));
	}

	/**
	 * 返回树形菜单集合
	 *
	 * @param name 节点名字，不传则查询所有组织
	 * @return
	 */
	@GetMapping(value = "/tree")
	@ApiOperation(value = "获取组织的树形集合", notes = "获取组织的树形集合")
	public R<List<OrganizationTreeNode>> listOrganizationTrees(String name) {
		return R.ok(sysOrganizationService.listOrganizationTrees(name));
	}

	/**
	 * 添加
	 *
	 * @param sysOrganization 实体
	 * @return success/false
	 */
	@SysLog("添加组织")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_organization_add')")
	@ApiOperation(value = "添加组织", notes = "添加组织")
	public R<SysOrganization> save(@Valid @RequestBody SysOrganization sysOrganization) {
		if (sysOrganizationService.lambdaQuery()
				.eq(SysOrganization::getCode, sysOrganization.getCode())
				.or(query -> {
					query.eq(SysOrganization::getName, sysOrganization.getName())
							.eq(SysOrganization::getParentId, sysOrganization.getParentId());
				})
				.exists()) {
			return R.failed("职务编号不能重复，且同级下职务名称不能重复");
		}
		if (sysOrganizationService.saveOrganization(sysOrganization)) {
			return R.ok(sysOrganization);
		} else {
			return R.failed("新增组织失败");
		}
	}

	/**
	 * 删除
	 *
	 * @param id ID
	 * @return success/false
	 */
	@SysLog("删除组织")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_organization_del')")
	@ApiOperation(value = "删除组织", notes = "删除组织")
	@CacheEvict(value = CacheConstants.ORGANIZATION, key = "#id")
	public R<Boolean> removeById(@PathVariable String id) {
		SysOrganization organization = sysOrganizationService.getById(id);
		if (organization != null) {
			if (Boolean.FALSE.equals(organization.getIsCanDel())) {
				return R.failed("该数据禁止删除操作!");
			}
		}
		organization.setCode(organization.getCode() + System.nanoTime());
		sysOrganizationService.updateById(organization);
		if (sysOrganizationService.removeOrganizationById(organization)) {
			return R.ok(Boolean.TRUE, "删除成功!");
		} else {
			return R.ok(Boolean.FALSE, "删除失败!");
		}
	}

	/**
	 * 编辑
	 *
	 * @param sysOrganization 实体
	 * @return success/false
	 */
	@SysLog("编辑组织")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_organization_edit')")
	@ApiOperation(value = "编辑组织", notes = "编辑组织")
	@CacheEvict(value = CacheConstants.ORGANIZATION, key = "#sysOrganization.id")
	public R<Boolean> update(@Valid @RequestBody SysOrganization sysOrganization) {
		if (sysOrganizationService.lambdaQuery()
				.eq(SysOrganization::getCode, sysOrganization.getCode())
				.ne(SysOrganization::getId, sysOrganization.getId())
				.exists()) {
			return R.failed("职务编码已存在!");
		}
		sysOrganization.setUpdateTime(LocalDateTime.now());
		return R.ok(sysOrganizationService.updateOrganizationById(sysOrganization));
	}

	/**
	 * 根据组织名查询组织信息
	 *
	 * @param organizationName 组织名
	 * @return
	 */
	@GetMapping("/details/{organizationName}")
	@ApiOperation(value = "根据组织名查询组织信息", notes = "根据组织名查询组织信息")
	public R user(@PathVariable String organizationName) {
		SysOrganization condition = new SysOrganization();
		condition.setName(organizationName);
		return R.ok(sysOrganizationService.getOne(new QueryWrapper<>(condition)));
	}

	/**
	 * 重置排序
	 *
	 * @param parentId 上级id
	 * @return
	 */
	@GetMapping("/resetSort/{parentId}")
	@ApiOperation(value = "重置排序", notes = "重置排序")
	@CacheEvict(value = CacheConstants.ORGANIZATION, allEntries = true)
	public R resetSort(@PathVariable String parentId) {
		sysOrganizationService.resetSort(parentId);
		return R.ok();
	}

	/**
	 * 保存组织排序
	 *
	 * @param sortMap 需要保存的组织集合
	 * @return
	 */
	@PostMapping("/saveSort")
	@ApiOperation(value = "保存组织排序", notes = "保存组织排序,Key为组织Id,value为排序值")
	@CacheEvict(value = CacheConstants.ORGANIZATION, allEntries = true)
	public R saveSort(@RequestBody Map<String, Integer> sortMap) {
		if (CollectionUtil.isNotEmpty(sortMap)) {
			List<SysOrganization> sysOrganizationList = new ArrayList<>();
			sortMap.forEach((organizationId, sort) -> {
				SysOrganization sysOrganization = new SysOrganization();
				sysOrganization.setId(organizationId);
				sysOrganization.setSort(sort);
				sysOrganizationList.add(sysOrganization);
			});
			sysOrganizationService.updateBatchById(sysOrganizationList);
		}
		return R.ok();
	}
}
