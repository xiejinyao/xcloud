package com.xjinyao.xcloud.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjinyao.xcloud.admin.api.dto.UserBaseInfoDTO;
import com.xjinyao.xcloud.admin.api.dto.UserDTO;
import com.xjinyao.xcloud.admin.api.dto.UserInfo;
import com.xjinyao.xcloud.admin.api.entity.SysUser;
import com.xjinyao.xcloud.admin.api.vo.UserVO;
import com.xjinyao.xcloud.common.core.util.R;

import java.util.List;

/**
 * @date 2019/2/1
 */
public interface SysUserService extends IService<SysUser> {

	/**
	 * 检查用户名是否已经存在
	 *
	 * @param userName 用户名
	 * @return
	 */
	boolean checkUserNameExists(String userName);

	/**
	 * 获取用户信息
	 *
	 * @param sysUser               用户
	 * @param includeDataPermission 包含数据权限
	 * @return userInfo
	 */
	UserInfo getUserInfo(SysUser sysUser, boolean includeDataPermission);

	/**
	 * 分页查询用户信息（含有角色信息）
	 *
	 * @param page    分页对象
	 * @param userDTO 参数列表
	 * @return
	 */
	IPage getUserWithRolePage(Page page, UserDTO userDTO);

	/**
	 * 删除用户
	 *
	 * @param sysUser 用户
	 * @return boolean
	 */
	Boolean removeUserById(SysUser sysUser);

	/**
	 * 更新当前用户基本信息
	 *
	 * @param userDto 用户信息
	 * @return Boolean
	 */
	R updateUserInfo(UserBaseInfoDTO userDto);

	/**
	 * 更新指定用户信息
	 *
	 * @param userDto 用户信息
	 * @return
	 */
	Boolean updateUser(UserDTO userDto);

	/**
	 * 通过ID查询用户信息
	 *
	 * @param id 用户ID
	 * @return 用户信息
	 */
	UserVO getUserVoById(Integer id);

	/**
	 * 用户登出
	 *
	 * @param username 用户名
	 */
	void removeUserCache(String username);

	/**
	 * 移除绑定了指定角色的用户缓存信息
	 *
	 * @param roleId 角色id
	 */
	void removeUserCacheByRoleId(Integer roleId);

	/**
	 * 查询上级组织的用户信息
	 *
	 * @param username 用户名
	 * @return R
	 */
	List<SysUser> listAncestorUsersByUsername(String username);

	/**
	 * 保存用户信息
	 *
	 * @param userDto DTO 对象
	 * @return success/fail
	 */
	Boolean saveUser(UserDTO userDto);

}
