package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.dto.MenuTreeNode;
import com.xjinyao.xcloud.admin.api.dto.SysMenuPart;
import com.xjinyao.xcloud.admin.api.entity.SysMenu;
import com.xjinyao.xcloud.admin.api.vo.MenuVO;
import com.xjinyao.xcloud.common.core.util.R;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @since 2019/2/1
 */
public interface SysMenuService extends IService<SysMenu> {

	/**
	 * 通过角色编号查询URL 权限
	 *
	 * @param roleId 角色ID
	 * @return 菜单列表
	 */
	List<MenuVO> findMenuByRoleId(Integer roleId);

	/**
	 * 级联删除菜单
	 *
	 * @param id 菜单ID
	 * @return true成功, false失败
	 */
	R removeMenuById(Long id);

	/**
	 * 更新菜单信息
	 *
	 * @param sysMenu 菜单信息
	 * @return 成功、失败
	 */
	Boolean updateMenuById(SysMenu sysMenu);

	/**
	 * 更加菜单id更新菜单部分信息
	 *
	 * @param menuPart 菜单部分
	 * @return {@link Object}
	 */
	Boolean updateMenuPartById(SysMenuPart menuPart);


	/**
	 * 树菜单
	 * 构建树
	 *
	 * @param lazy     是否是懒加载
	 * @param parentId 父节点ID
	 * @param roleId   角色id
	 * @return {@link List}<{@link MenuTreeNode}>
	 */
	List<MenuTreeNode> treeMenu(boolean lazy, Long roleId, Long parentId);

	/**
	 * 查询菜单
	 *
	 * @param menuSet
	 * @param parentId
	 * @return
	 */
	List<MenuTreeNode> filterMenu(Set<MenuVO> menuSet, Long parentId);
}