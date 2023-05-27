package com.xjinyao.xcloud.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.dto.MenuTreeNode;
import com.xjinyao.xcloud.admin.api.dto.SysMenuPart;
import com.xjinyao.xcloud.admin.api.entity.SysMenu;
import com.xjinyao.xcloud.admin.api.entity.SysRole;
import com.xjinyao.xcloud.admin.api.entity.SysRoleMenu;
import com.xjinyao.xcloud.admin.api.enums.RoleCodes;
import com.xjinyao.xcloud.admin.api.vo.MenuVO;
import com.xjinyao.xcloud.admin.mapper.SysMenuMapper;
import com.xjinyao.xcloud.admin.mapper.SysRoleMenuMapper;
import com.xjinyao.xcloud.admin.service.SysMenuService;
import com.xjinyao.xcloud.admin.service.SysRoleMenuService;
import com.xjinyao.xcloud.admin.service.SysRoleService;
import com.xjinyao.xcloud.common.core.constant.CommonConstants;
import com.xjinyao.xcloud.common.core.constant.enums.MenuTypeEnum;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.tree.TreeUtil;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.xjinyao.xcloud.common.core.constant.CommonConstants.TREE_ROOT_ID_ZERO;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @since 2017-10-29
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

	private final SysRoleMenuMapper sysRoleMenuMapper;

	private final SysRoleMenuService sysRoleMenuService;

	private final SysRoleService roleService;

	@Override
	@Cacheable(value = CacheConstants.MENU_DETAILS, key = "#roleId  + '_menu'", unless = "#result == null || #result.size() ==0")
	public List<MenuVO> findMenuByRoleId(Integer roleId) {
		return baseMapper.listMenusByRoleId(roleId);
	}

	/**
	 * 级联删除菜单
	 *
	 * @param id 菜单ID
	 * @return true成功, false失败
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public R removeMenuById(Long id) {
		// 查询父节点为当前节点的节点
		List<SysMenu> menuList = this.list(Wrappers.<SysMenu>query().lambda().eq(SysMenu::getParentId, id));

		if (CollUtil.isNotEmpty(menuList)) {
			return R.failed("菜单含有下级不能删除");
		}

		sysRoleMenuMapper.delete(Wrappers.<SysRoleMenu>query().lambda().eq(SysRoleMenu::getMenuId, id));
		// 删除当前菜单及其子菜单
		return R.ok(this.removeById(id));
	}

	@Override
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public Boolean updateMenuById(SysMenu sysMenu) {
		return this.updateById(sysMenu);
	}

	/**
	 * 更加菜单id更新菜单部分信息
	 *
	 * @param menuPart 菜单部分
	 * @return {@link Object}
	 */
	@Override
	@CacheEvict(value = CacheConstants.MENU_DETAILS, allEntries = true)
	public Boolean updateMenuPartById(SysMenuPart menuPart) {
		Long menuId = menuPart.getMenuId();
		if (menuId == null) {
			return false;
		}
		String name = menuPart.getName();
		String permission = menuPart.getPermission();
		String icon = menuPart.getIcon();
		String path = menuPart.getPath();
		Integer sort = menuPart.getSort();
		String type = menuPart.getType();
		String keepAlive = menuPart.getKeepAlive();
		if (StringUtils.isAllBlank(name, permission, icon, path, type, keepAlive) && sort == null) {
			return false;
		}
		LambdaUpdateChainWrapper<SysMenu> wrapper = this.lambdaUpdate().eq(SysMenu::getMenuId, menuId);
		if (StringUtils.isNotBlank(name)) {
			wrapper.set(SysMenu::getName, name);
		}
		if (StringUtils.isNotBlank(permission)) {
			wrapper.set(SysMenu::getPermission, permission);
		}
		if (StringUtils.isNotBlank(icon)) {
			wrapper.set(SysMenu::getIcon, icon);
		}
		if (StringUtils.isNotBlank(path)) {
			wrapper.set(SysMenu::getPath, path);
		}
		if (sort != null) {
			wrapper.set(SysMenu::getSort, sort);
		}
		if (StringUtils.isNotBlank(type)) {
			wrapper.set(SysMenu::getType, type);
		}
		if (StringUtils.isNotBlank(keepAlive)) {
			wrapper.set(SysMenu::getKeepAlive, keepAlive);
		}
		return wrapper.update();
	}

	/**
	 * 查询菜单
	 *
	 * @param all      全部菜单
	 * @param parentId 父节点ID
	 * @return
	 */
	@Override
	public List<MenuTreeNode> filterMenu(Set<MenuVO> all, Long parentId) {
		List<MenuTreeNode> menuTreeList = all.stream()
				.filter(vo -> MenuTypeEnum.LEFT_MENU.getType().equals(vo.getType()))
				.map(MenuTreeNode::new)
				.sorted(Comparator.comparingInt(MenuTreeNode::getSort))
				.collect(Collectors.toList());
		Number parent = parentId == null ? CommonConstants.TREE_ROOT_ID : parentId;
		return TreeUtil.buildTree(menuTreeList, parent, 3);
	}

	/**
	 * 树菜单
	 * 构建树查询 1. 不是懒加载情况，查询全部 2. 是懒加载，根据parentId 查询 2.1 父节点为空，则查询ID -1
	 *
	 * @param lazy     是否是懒加载
	 * @param parentId 父节点ID
	 * @param roleId   角色id
	 * @return {@link List}<{@link MenuTreeNode}>
	 */
	@Override
	public List<MenuTreeNode> treeMenu(boolean lazy, Long roleId, Long parentId) {
		List<SysRoleMenu> allMenuIds = getRoleAllMenuIds(roleId);
		List<Long> reAuthMenuIds = allMenuIds.stream()
				.filter(SysRoleMenu::getReAuth)
				.map(SysRoleMenu::getMenuId)
				.collect(Collectors.toList());
		if (!lazy) {
			if (SecurityUtils.isSuperAdmin() && RoleCodes.ROLE_ADMIN.getRoleId().equals(roleId)) {
				return buildTree(this.lambdaQuery()
						.orderByAsc(SysMenu::getSort)
						.list(), CommonConstants.TREE_ROOT_ID, reAuthMenuIds);
			}
			if (CollectionUtils.isEmpty(reAuthMenuIds)) {
				return Collections.emptyList();
			}
			return buildTree(this.lambdaQuery()
					.in(SysMenu::getMenuId, reAuthMenuIds)
					.orderByAsc(SysMenu::getSort)
					.list(), CommonConstants.TREE_ROOT_ID, reAuthMenuIds);
		}

		Number parent = parentId == null ? CommonConstants.TREE_ROOT_ID : parentId;
		LambdaQueryChainWrapper<SysMenu> wrapper = this.lambdaQuery()
				.eq(SysMenu::getParentId, parent)
				.orderByAsc(SysMenu::getSort);
		if (parent.equals(CommonConstants.TREE_ROOT_ID)
				&& SecurityUtils.isSuperAdmin()
				&& RoleCodes.ROLE_ADMIN.getRoleId().equals(roleId)) {
			return buildTree(wrapper.list(), parent, reAuthMenuIds);
		}
		if (CollectionUtils.isEmpty(reAuthMenuIds)) {
			return Collections.emptyList();
		}
		return buildTree(wrapper.in(SysMenu::getMenuId, reAuthMenuIds).list(), parent, reAuthMenuIds);
	}

	/**
	 * 得到指定角色的所有菜单信息
	 *
	 * @param roleId 角色id
	 * @return {@link List}<{@link Long}>
	 */
	private List<SysRoleMenu> getRoleAllMenuIds(Long roleId) {
		List<SysRoleMenu> list = null;
		LambdaQueryChainWrapper<SysRoleMenu> wrapper = sysRoleMenuService.lambdaQuery();
		if (roleId == null) {
			if (SecurityUtils.isSuperAdmin()) {
				list = wrapper.list();
			} else {
				List<Integer> roleIds = SecurityUtils.getRoleIds();
				if (CollectionUtils.isNotEmpty(roleIds)) {
					list = wrapper.in(SysRoleMenu::getRoleId, roleIds).list();
				}
			}
		} else {
			SysRole sysRole = roleService.getById(roleId);
			if (sysRole != null) {
				Integer parentRoleId = sysRole.getParentRoleId();
				if (parentRoleId != null) {
					Integer zero = TREE_ROOT_ID_ZERO.intValue();
					Long parentId = Objects.equals(zero, parentRoleId) ? roleId : parentRoleId;
					list = wrapper.eq(SysRoleMenu::getRoleId, parentId).list();
				}
			}
		}
		return Optional.ofNullable(list).orElse(Collections.emptyList());
	}

	/**
	 * 通过sysMenu创建树形节点
	 *
	 * @param menus
	 * @param root
	 * @return
	 */
	private List<MenuTreeNode> buildTree(List<SysMenu> menus, Number root, List<Long> reAuthMenuIds) {
		return TreeUtil.buildTree(Optional.ofNullable(menus).orElse(Collections.emptyList())
				.stream()
				.map(menu -> {
					MenuTreeNode node = new MenuTreeNode();
					node.setId(menu.getMenuId());
					node.setParentId(menu.getParentId());
					node.setName(menu.getName());
					node.setPath(menu.getPath());
					node.setPermission(menu.getPermission());
					node.setLabel(menu.getName());
					node.setIcon(menu.getIcon());
					node.setType(menu.getType());
					node.setSort(menu.getSort());
					node.setKeepAlive(menu.getKeepAlive());
					node.setReAuth(reAuthMenuIds.contains(menu.getMenuId()));
					return node;
				})
				.collect(Collectors.toList()), root, 3);
	}

}
