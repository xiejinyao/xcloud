package com.xjinyao.xcloud.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.admin.api.dto.UserDTO;
import com.xjinyao.xcloud.admin.api.entity.SysUser;
import com.xjinyao.xcloud.admin.api.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @since 2019/2/1
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

	/**
	 * 通过用户名查询用户信息（含有角色信息）
	 *
	 * @param username 用户名
	 * @return userVo
	 */
	UserVO getUserVoByUsername(String username);

	/**
	 * 分页查询用户信息（含角色）
	 *
	 * @param page    分页
	 * @param userDTO 查询参数
	 * @return list
	 */
	IPage<List<UserVO>> getUserVosPage(Page page,
                                       @Param("query") UserDTO userDTO,
                                       @Param("userOrganizationId") String userOrganizationId);

	/**
	 * 通过ID查询用户信息
	 *
	 * @param id 用户ID
	 * @return userVo
	 */
	UserVO getUserVoById(Integer id);

	/**
	 * 查询绑定指定角色的用户名
	 *
	 * @param roleId 角色id
	 * @return
	 */
	List<String> findUserNamesByRoleId(@Param("roleId") Integer roleId);

	/**
	 * 查询绑定指定角色的用户id
	 *
	 * @param roleId 角色id
	 * @return
	 */
	List<Integer> findUserIdByRoleId(@Param("roleId") Integer roleId);
}
