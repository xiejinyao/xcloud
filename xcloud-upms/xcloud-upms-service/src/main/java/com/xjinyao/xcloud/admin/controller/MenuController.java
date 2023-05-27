package com.xjinyao.xcloud.admin.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.xjinyao.xcloud.admin.api.dto.MenuTreeNode;
import com.xjinyao.xcloud.admin.api.dto.SortdMenu;
import com.xjinyao.xcloud.admin.api.dto.SysMenuPart;
import com.xjinyao.xcloud.admin.api.entity.SysMenu;
import com.xjinyao.xcloud.admin.api.vo.MenuVO;
import com.xjinyao.xcloud.admin.service.SysMenuService;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.log.annotation.SysLog;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2017/10/31
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/menu")
@Api(value = "menu", tags = "菜单管理模块")
public class MenuController {

	private final SysMenuService sysMenuService;

	/**
	 * 返回当前用户的树形菜单集合
	 *
	 * @param parentId 父节点ID
	 * @return 当前用户的树形菜单
	 */
	@GetMapping
	@ApiOperation(value = "获取当前用户的树形菜单集合", notes = "获取当前用户的树形菜单集合")
	public R<List<MenuTreeNode>> getUserMenu(Long parentId) {
		// 获取符合条件的菜单
		Set<MenuVO> all = new HashSet<>();
		SecurityUtils.getRoleIds()
				.forEach(roleId -> all.addAll(sysMenuService.findMenuByRoleId(roleId)));
		return R.ok(sysMenuService.filterMenu(all, parentId));
	}

	/**
	 * 让树
	 * 返回树形菜单集合
	 *
	 * @param lazy     是否是懒加载
	 * @param parentId 父节点ID
	 * @param roleId   角色id
	 * @return 树形菜单
	 */
	@GetMapping(value = "/tree")
	@ApiOperation(value = "获取树形菜单集合", notes = "获取树形菜单集合")
	public R<List<MenuTreeNode>> getTree(boolean lazy, Long roleId, Long parentId) {
		return R.ok(sysMenuService.treeMenu(lazy, roleId, parentId));
	}

	/**
	 * 返回角色的菜单集合
	 *
	 * @param roleId 角色ID
	 * @return 属性集合
	 */
	@GetMapping("/tree/{roleId}")
	@ApiOperation(value = "获取角色的菜单集合", notes = "获取角色的菜单集合")
	public R<List<Long>> getRoleTree(@PathVariable Integer roleId) {
		return R.ok(sysMenuService.findMenuByRoleId(roleId)
				.stream()
				.map(MenuVO::getMenuId)
				.collect(Collectors.toList()));
	}

	/**
	 * 通过ID查询菜单的详细信息
	 *
	 * @param id 菜单ID
	 * @return 菜单详细信息
	 */
	@GetMapping("/{id}")
	@ApiOperation(value = "通过ID查询菜单的详细信息", notes = "通过ID查询菜单的详细信息")
	public R<SysMenu> getById(@PathVariable Long id) {
		return R.ok(sysMenuService.getById(id));
	}

	/**
	 * 新增菜单
	 *
	 * @param sysMenu 菜单信息
	 * @return success/false
	 */
	@SysLog("新增菜单")
	@PostMapping
	@PreAuthorize("@pms.hasPermission('sys_menu_add')")
	@ApiOperation(value = "新增菜单", notes = "新增菜单,权限编码：sys_menu_add")
	public R<Boolean> save(@Valid @RequestBody SysMenu sysMenu) {
		return R.ok(sysMenuService.save(sysMenu));
	}


	/**
	 * 删除菜单
	 *
	 * @param id 菜单ID
	 * @return success/false
	 */
	@SysLog("删除菜单")
	@DeleteMapping("/{id}")
	@PreAuthorize("@pms.hasPermission('sys_menu_del')")
	@ApiOperation(value = "删除菜单", notes = "删除菜单,权限编码：sys_menu_del")
	public R<Boolean> removeById(@PathVariable Long id) {
		return sysMenuService.removeMenuById(id);
	}

	/**
	 * 更新菜单
	 *
	 * @param sysMenu
	 * @return
	 */
	@SysLog("更新菜单")
	@PutMapping
	@PreAuthorize("@pms.hasPermission('sys_menu_edit')")
	@ApiOperation(value = "更新菜单", notes = "更新菜单,权限编码：sys_menu_edit")
	public R<Boolean> update(@Valid @RequestBody SysMenu sysMenu) {
		return R.ok(sysMenuService.updateMenuById(sysMenu));
	}

	/**
	 * 更新菜单部分信息
	 *
	 * @param menuPart
	 * @return
	 */
	@SysLog("更新菜单部分信息")
	@PutMapping("/update/part")
	@PreAuthorize("@pms.hasPermission('sys_menu_part_edit')")
	@ApiOperation(value = "更新菜单部分信息", notes = "更新菜单部分信息,权限编码：sys_menu_part_edit")
	public R<Boolean> updatePart(@Valid @RequestBody SysMenuPart menuPart) {
		return R.ok(sysMenuService.updateMenuPartById(menuPart));
	}

	/**
	 * 更新菜单顺序
	 *
	 * @param sortdMenuList
	 * @return
	 */
	@SysLog("更新菜单顺序")
	@PostMapping("updateMenuSort")
	@PreAuthorize("@pms.hasPermission('sys_menu_set_sort')")
	@ApiOperation(value = "更新菜单顺序", notes = "更新菜单顺序,权限编码：sys_menu_set_sort")
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public R<Boolean> updateMenuSort(@RequestBody List<SortdMenu> sortdMenuList) {
		List<SysMenu> sortdMenus = new ArrayList<>();
		if (CollectionUtil.isNotEmpty(sortdMenuList)) {
			for (SortdMenu sortdMenu : sortdMenuList) {
				if (sortdMenu.getId() == null) {
					continue;
				}
				SysMenu menu = new SysMenu();
				menu.setMenuId(sortdMenu.getId());
				menu.setParentId(sortdMenu.getParentId());
				menu.setSort(sortdMenu.getSort());
				sortdMenus.add(menu);
			}
			if (CollectionUtil.isNotEmpty(sortdMenus)) {
				return R.ok(sysMenuService.updateBatchById(sortdMenus));
			}
		}
		return R.ok(Boolean.TRUE);
	}
}
