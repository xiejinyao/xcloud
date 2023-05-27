package com.xjinyao.xcloud.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.xjinyao.xcloud.admin.api.dto.RoleDTO;
import com.xjinyao.xcloud.admin.api.dto.RoleMenuDTO;
import com.xjinyao.xcloud.admin.api.entity.SysDictItem;
import com.xjinyao.xcloud.admin.api.entity.SysRole;
import com.xjinyao.xcloud.admin.api.entity.SysRoleDataPermission;
import com.xjinyao.xcloud.admin.api.vo.SysRoleDataPermissionVO;
import com.xjinyao.xcloud.admin.service.*;
import com.xjinyao.xcloud.common.core.constant.CommonConstants;
import com.xjinyao.xcloud.common.core.tree.DefaultTreeNode;
import com.xjinyao.xcloud.common.core.tree.TreeUtil;
import com.xjinyao.xcloud.common.core.util.NumberUtils;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.constant.CommonConstants.TREE_ROOT_ID_ZERO;
import static com.xjinyao.xcloud.common.core.util.StringUtils.SLASH_SEPARATOR;

/**
 * @date 2019/2/1
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
@Api(value = "role", tags = "角色管理模块")
public class RoleController {

	private final SysRoleService sysRoleService;

	private final SysRoleMenuService sysRoleMenuService;

	private final SysUserService sysUserService;

	private final SysDictItemService sysDictItemService;

	private final ISysRoleDataPermissionService sysRoleDataPermissionService;

	/**
	 * 通过ID查询角色信息
	 *
	 * @param id ID
	 * @return 角色信息
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "通过ID查询角色信息", notes = "通过ID查询角色信息")
	public R<SysRole> getById(@PathVariable Integer id) {
		return R.ok(sysRoleService.getById(id));
	}

	/**
	 * 添加角色
	 *
	 * @param sysRole 角色信息
	 * @return success、false
	 */
	@SysLog("添加角色")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_role_add')")
	@ApiOperation(value = "添加角色", notes = "添加角色")
	public R<SysRole> save(@Valid @RequestBody RoleDTO sysRole) {
		List<SysRole> list = sysRoleService.list(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleCode, sysRole.getRoleCode()));
		if (list != null && !list.isEmpty()) {
			return R.failed("角色标识存在!");
		}
		List<SysRole> sysRoles = sysRoleService.list(Wrappers.<SysRole>lambdaQuery().eq(SysRole::getRoleName, sysRole.getRoleName()));
		if (sysRoles != null && !sysRoles.isEmpty()) {
			return R.failed("角色名称存在!");
		}
		if (sysRoleService.save(sysRole)) {
			sysUserService.removeUserCacheByRoleId(sysRole.getRoleId());
		}
		SysRole resultObj = sysRoleService.getById(sysRole.getRoleId());
		Optional.ofNullable(resultObj).ifPresent(o -> o.setHasChildren(false));
		return R.ok(resultObj);
	}

	/**
	 * 修改角色
	 *
	 * @param sysRole 角色信息
	 * @return success/false
	 */
	@SysLog("修改角色")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_role_edit')")
	@ApiOperation(value = "修改角色", notes = "修改角色")
	public R<Boolean> update(@Valid @RequestBody RoleDTO sysRole) {
		if (!sysRole.getIsCanEdit()) {
			return R.failed("该角色不可编辑!");
		}
		boolean result = sysRoleService.updateById(sysRole);
		if (result) {
			sysUserService.removeUserCacheByRoleId(sysRole.getRoleId());
		}
		return R.ok(result);
	}

	/**
	 * 删除角色
	 *
	 * @param id
	 * @return
	 */
	@SysLog("删除角色")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_role_del')")
	@ApiOperation(value = "删除角色", notes = "删除角色")
	public R<Boolean> removeById(@PathVariable Integer id) {
		SysRole sysRole = sysRoleService.getById(id);
		if (sysRole != null) {
			if (!sysRole.getIsCanDelete()) {
				return R.failed("该角色不可删除!");
			}
			Boolean result = sysRoleService.removeRoleById(id);
			if (result) {
				sysUserService.removeUserCacheByRoleId(id);
			}
			return R.ok(result);
		}
		return R.failed("角色不存在!");
	}

	/**
	 * 删除角色
	 *
	 * @param ids
	 * @return
	 */
	@SysLog("删除角色")
	@DeleteMapping("/batchDelete")
	@PreAuthorize("@pms.hasPermission('sys_role_batch_delete')")
	@ApiOperation(value = "删除角色", notes = "删除角色")
	public R<Boolean> removeById(@RequestBody List<Integer> ids) {
		List<SysRole> sysRoles = Optional.ofNullable(sysRoleService.lambdaQuery()
						.in(SysRole::getRoleId, ids)
						.list())
				.orElse(Collections.emptyList());
		if (CollectionUtils.isEmpty(sysRoles)) {
			return R.failed("角色不存在!");
		}
		List<SysRole> waitDeleteList = sysRoles.stream()
				.filter(d -> d.getIsCanDelete() != null)
				.filter(SysRole::getIsCanDelete)
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(waitDeleteList)) {
			sysRoleService.removeRoleByIds(waitDeleteList.stream()
					.map(SysRole::getRoleId)
					.collect(Collectors.toList()));
		}
		List<SysRole> notDeleteList = sysRoles.stream()
				.filter(d -> d.getIsCanDelete() != null && !d.getIsCanDelete())
				.collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(notDeleteList)) {
			List<String> list = notDeleteList.stream()
					.map(d -> d.getRoleName() + "(" + d.getRoleId() + ")")
					.collect(Collectors.toList());
			if (list.size() == sysRoles.size()) {
				return R.failed(false, "删除失败");
			}
			return R.ok(true, "部分删除成功(角色：" + StringUtils.join(list, ",") + " 不可删除)");
		}
		return R.ok(true, "部分删除成功");
	}

	/**
	 * 获取角色列表
	 *
	 * @return 角色列表
	 */
	@GetMapping("/list")
	@ApiOperation(value = "获取角色列表", notes = "获取角色列表")
	public R<List<SysRole>> listRoles() {
		LambdaQueryChainWrapper<SysRole> wrapper = sysRoleService.lambdaQuery();

		wrapperUserPermission(wrapper);

		return R.ok(wrapper.list());
	}

	/**
	 * 获取角色树
	 *
	 * @return 获取角色树
	 */
	@GetMapping("/getRoleByParentId")
	@ApiOperation(value = "获取角色树", notes = "获取角色树")
	public R<List<SysRole>> getRoleByParentId(@ApiParam("上级角色Id") @RequestParam Integer parentId) {
		List<SysRole> list = Collections.emptyList();
		if (TREE_ROOT_ID_ZERO.intValue() == parentId) {
			if (SecurityUtils.isSuperAdmin()) {
				list = sysRoleService.lambdaQuery()
						.eq(SysRole::getParentRoleId, parentId)
						.list();
			} else {
				List<Integer> roleIds = SecurityUtils.getRoleIds();
				if (CollectionUtils.isNotEmpty(roleIds)) {
					list = sysRoleService.lambdaQuery()
							.in(SysRole::getRoleId, roleIds)
							.list();
					if (CollectionUtils.isNotEmpty(list)) {
						// 如果当前用户的据角色存在上下级关系，仅保留所有的最上级角色
						List<SysRole> finalList = list;
						list.removeAll(list.stream()
								.filter(next -> finalList.stream()
										.anyMatch(d -> Objects.equals(d.getRoleId(), next.getParentRoleId())))
								.collect(Collectors.toList()));
					}
				}
			}
		} else {
			list = sysRoleService.lambdaQuery()
					.eq(SysRole::getParentRoleId, parentId)
					.list();
		}
		if (CollectionUtils.isNotEmpty(list)) {
			Map<Integer, List<SysRole>> map = Optional.ofNullable(sysRoleService.lambdaQuery()
							.select(SysRole::getParentRoleId)
							.in(SysRole::getParentRoleId, list.stream()
									.map(SysRole::getRoleId)
									.collect(Collectors.toList()))
							.list())
					.orElse(Collections.emptyList())
					.stream()
					.collect(Collectors.groupingBy(SysRole::getParentRoleId));
			list.forEach(d -> d.setHasChildren(!map.getOrDefault(d.getRoleId(), Collections.emptyList()).isEmpty()));

		}

		R<List<SysRole>> result = R.ok(list);
		result.addExtendData("superAdmin", SecurityUtils.isSuperAdmin());
		return result;
	}


	/**
	 * 获取角色树
	 *
	 * @return 获取角色树
	 */
	@GetMapping("/listTree")
	@ApiOperation(value = "获取角色树", notes = "获取角色树")
	public R<List<SysRole>> listTree() {
		List<SysRole> list = Collections.emptyList();
		if (SecurityUtils.isSuperAdmin()) {
			list = sysRoleService.lambdaQuery()
					.eq(SysRole::getParentRoleId, TREE_ROOT_ID_ZERO)
					.list();
		} else {
			List<Integer> roleIds = SecurityUtils.getRoleIds();
			if (CollectionUtils.isNotEmpty(roleIds)) {
				list = sysRoleService.lambdaQuery()
						.in(SysRole::getRoleId, roleIds)
						.list();
			}
		}
		if (CollectionUtils.isNotEmpty(list)) {
			list = sysRoleService.buildTree(list);
		}
		R<List<SysRole>> result = R.ok(list);
		result.addExtendData("superAdmin", SecurityUtils.isSuperAdmin());
		return result;
	}

	/**
	 * 获取角色树
	 *
	 * @return 获取角色树
	 */
	@GetMapping("/tree")
	@ApiOperation(value = "获取角色树", notes = "获取角色树")
	public R<List<DefaultTreeNode>> roleTree() {
		LambdaQueryChainWrapper<SysRole> wrapper = sysRoleService.lambdaQuery();

		wrapperUserPermission(wrapper);

		List<SysRole> list = wrapper.list();

		//角色转树节点
		List<DefaultTreeNode> collect = Optional.ofNullable(list).orElse(Collections.emptyList())
				.stream()
				.map(d -> DefaultTreeNode.builder()
						.id(d.getRoleId())
						.parentId(d.getRoleGroup())
						.name(d.getRoleName())
						.hasChildren(Boolean.FALSE)
						.build())
				.collect(Collectors.toList());

		//分组字典映射
		Map<String, String> roleGroupDictMap = Optional.ofNullable(sysDictItemService.lambdaQuery()
						.eq(SysDictItem::getType, CommonConstants.ROLE_GROUP_DICT_TYPE)
						.list())
				.orElse(Collections.emptyList())
				.stream()
				.collect(Collectors.toMap(SysDictItem::getValue, SysDictItem::getLabel));

		//分组自成节点
		List<DefaultTreeNode> groupNodes = Optional.ofNullable(list).orElse(Collections.emptyList())
				.stream()
				.map(SysRole::getRoleGroup)
				.distinct()
				.map(group -> DefaultTreeNode.builder()
						.id(group)
						.parentId(CommonConstants.TREE_ROOT_ID)
						.name(roleGroupDictMap.get(group))
						.build())
				.collect(Collectors.toList());

		List<DefaultTreeNode> nodes = new ArrayList<>();
		nodes.addAll(collect);
		nodes.addAll(groupNodes);

		return R.ok(TreeUtil.buildTree(nodes,
				d -> NumberUtils.equals(d.getParentId(), CommonConstants.TREE_ROOT_ID),
				(p, c) -> Objects.equals(p.getId(), c.getParentId()),
				DefaultTreeNode::getChildren,
				DefaultTreeNode::setChildren));
	}

	/**
	 * 更新角色菜单
	 *
	 * @param roleMenuDTO 角色对象
	 * @return success、false
	 */
	@SysLog("更新角色菜单")
	@PutMapping("/menu")
	@PreAuthorize("@pms.hasPermission('sys_role_perm')")
	@ApiOperation(value = "更新角色菜单", notes = "更新角色菜单")
	public R<Boolean> saveRoleMenus(@RequestBody RoleMenuDTO roleMenuDTO) {
		SysRole sysRole = sysRoleService.getById(roleMenuDTO.getRoleId());
		return R.ok(sysRoleMenuService.saveRoleMenus(sysRole.getRoleCode()
				, roleMenuDTO.getRoleId(), roleMenuDTO.getMenuIds()));
	}

	/**
	 * 获取指定角色的数据标识值
	 *
	 * @param roleId 角色id
	 * @return
	 */
	@ApiOperation(value = "获取指定角色和维度的数据标识值", notes = "获取指定角色和维度的数据标识值")
	@GetMapping("/getRoleDataPermissions")
	public R<List<SysRoleDataPermissionVO>> getRoleDataPermissions(@ApiParam(value = "角色id")
																   @RequestParam Integer roleId) {
		return R.ok(new ArrayList<>() {{
			Optional.ofNullable(sysRoleDataPermissionService.lambdaQuery()
							.select(SysRoleDataPermission::getDimension, SysRoleDataPermission::getIdentifierValue)
							.eq(SysRoleDataPermission::getRoleId, roleId)
							.list())
					.orElse(Collections.emptyList())
					.stream()
					.collect(Collectors.groupingBy(SysRoleDataPermission::getDimension))
					.forEach((k, v) -> add(SysRoleDataPermissionVO.builder()
							.dimensionValue(k)
							.identifierValues(Optional.ofNullable(v)
									.orElse(Collections.emptyList())
									.stream()
									.map(SysRoleDataPermission::getIdentifierValue)
									.collect(Collectors.toList()))
							.build()));
		}});
	}

	private void wrapperUserPermission(LambdaQueryChainWrapper<SysRole> wrapper) {
		if (SecurityUtils.isSuperAdmin()) {
			return;
		}

		List<Integer> roleIds = getRoleIds();

		//自身拥有的所有角色
		wrapper.eq(SysRole::getCanSelect, true)
				.and(q -> {
					q.in(SysRole::getRoleId, roleIds);
					//自身用用所有角色的子角色
					roleIds.forEach(roleId -> q.or().like(SysRole::getParentRoleIdPath,
							SLASH_SEPARATOR + roleId + SLASH_SEPARATOR));
				});
	}

	private List<Integer> getRoleIds() {
		List<Integer> roleIds = Optional.ofNullable(SecurityUtils.getRoleIds())
				.orElse(new ArrayList<>());
		roleIds.add(Integer.MIN_VALUE);
		return roleIds;
	}

}
