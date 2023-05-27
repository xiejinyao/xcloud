package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.dto.RoleDTO;
import com.xjinyao.xcloud.admin.api.entity.SysRole;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @since 2019/2/1
 */
public interface SysRoleService extends IService<SysRole> {

	/**
	 * 通过用户ID，查询角色信息
	 *
	 * @param userId
	 * @return
	 */
	List<SysRole> findRolesByUserId(Integer userId);

	/**
	 * 通过角色ID，删除角色
	 *
	 * @param id
	 * @return
	 */
	Boolean removeRoleById(Integer id);

	/**
	 * 通过角色ID，批量删除角色
	 *
	 * @param ids
	 * @return
	 */
	Boolean removeRoleByIds(List<Integer> ids);

	boolean save(RoleDTO sysRole);

	boolean updateById(RoleDTO sysRole);

	List<SysRole> buildTree(List<SysRole> list);
}
