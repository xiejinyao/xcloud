package com.xjinyao.xcloud.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjinyao.xcloud.admin.api.dto.*;
import com.xjinyao.xcloud.admin.api.entity.*;
import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionDimensionEnum;
import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionTypeEnum;
import com.xjinyao.xcloud.admin.api.enums.UserDataPermissionDimensionEnum;
import com.xjinyao.xcloud.admin.api.vo.MenuVO;
import com.xjinyao.xcloud.admin.api.vo.UserVO;
import com.xjinyao.xcloud.admin.mapper.SysUserMapper;
import com.xjinyao.xcloud.admin.service.*;
import com.xjinyao.xcloud.common.core.constant.CommonConstants;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.desensitization.util.PrivacyUtil;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import com.xjinyao.xcloud.common.security.util.TokenUtil;
import com.xjinyao.xcloud.file.api.feign.RemoteSysFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @date 2019/2/1
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

	private static final PasswordEncoder ENCODER = new BCryptPasswordEncoder();

	private final SysMenuService sysMenuService;

	private final SysRoleService sysRoleService;

	private final SysOrganizationService sysOrganizationService;

	private final SysUserRoleService sysUserRoleService;

	private final SysOauthClientDetailsService sysOauthClientDetailsService;

	private final CacheManager cacheManager;

	private final TokenStore tokenStore;

	private final RemoteSysFileService remoteSysFileService;

	private final ISysRoleDataPermissionService sysRoleDataPermissionService;

	private final SysUserDataPermissionService sysUserDataPermissionService;

	private final SysUserMapper sysUserMapper;

	/**
	 * 检查用户名是否已经存在在
	 *
	 * @param userName 用户名
	 * @return
	 */
	@Override
	public boolean checkUserNameExists(String userName) {
		return this.lambdaQuery().eq(SysUser::getUsername, userName).exists();
	}

	/**
	 * 保存用户信息
	 *
	 * @param userDto DTO 对象
	 * @return success/fail
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveUser(UserDTO userDto) {
		SysUser sysUser = new SysUser();
		BeanUtils.copyProperties(userDto, sysUser);
		sysUser.setDelFlag(CommonConstants.STATUS_NORMAL);
		sysUser.setPassword(ENCODER.encode(userDto.getPassword()));
		baseMapper.insert(sysUser);
		List<SysUserRole> userRoleList = userDto.getRole().stream().map(roleId -> {
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(sysUser.getUserId());
			userRole.setRoleId(roleId);
			userRole.setIsCanCancel(true);
			return userRole;
		}).collect(Collectors.toList());
		return sysUserRoleService.saveBatch(userRoleList);
	}

	/**
	 * 通过查用户的全部信息
	 *
	 * @param sysUser 用户
	 * @return
	 */
	@Override
	public UserInfo getUserInfo(SysUser sysUser, boolean includeDataPermission) {
		UserInfo userInfo = new UserInfo();
		userInfo.setSysUser(sysUser);
		//设置角色列表  （ID）
		List<SysRole> userRoles = Optional.ofNullable(sysRoleService.findRolesByUserId(sysUser.getUserId()))
				.orElse(Collections.emptyList());

		userInfo.setRoleIds(ArrayUtil.toArray(Optional.of(userRoles)
				.orElse(Collections.emptyList())
				.stream()
				.map(SysRole::getRoleId)
				.collect(Collectors.toList()), Integer.class));

		userInfo.setRoleCodes(ArrayUtil.toArray(Optional.of(userRoles)
				.orElse(Collections.emptyList())
				.stream()
				.map(SysRole::getRoleCode)
				.collect(Collectors.toList()), String.class));

		if (includeDataPermission) {
			DataPermission dp = new DataPermission();
			//角色数据权限
			setUserRoleDataPermission(dp, userRoles);

			//自身数据权限
			setUserDataPermission(dp, sysUser.getUserId());

			userInfo.setDataPermission(dp);
		}

		//设置权限列表（menu.permission）
		Set<String> permissions = new HashSet<>();
		Arrays.stream(userInfo.getRoleIds()).forEach(roleId -> {
			List<String> permissionList = sysMenuService.findMenuByRoleId(roleId)
					.stream()
					.map(MenuVO::getPermission)
					.filter(StringUtils::isNotEmpty)
					.collect(Collectors.toList());
			permissions.addAll(permissionList);
		});
		userInfo.setPermissions(ArrayUtil.toArray(permissions, String.class));

		//组织编码设置
		String organizationId = sysUser.getOrganizationId();
		if (organizationId != null) {
			SysOrganization sySorganization = sysOrganizationService.getById(organizationId);
			if (sySorganization != null) {
				userInfo.setOrganizationCode(sySorganization.getCode());
			}
		}
		return userInfo;
	}

	/**
	 * 分页查询用户信息（含有角色信息）
	 *
	 * @param page    分页对象
	 * @param userDTO 参数列表
	 * @return
	 */
	@Override
	public IPage getUserWithRolePage(Page page, UserDTO userDTO) {
		CustomUser user = SecurityUtils.getUser();
		String organizationId = "-1";
		if (user != null) {
			organizationId = user.getOrganizationId();
		}
		if (SecurityUtils.isSuperAdmin()) {
			organizationId = null;
		}
		return baseMapper.getUserVosPage(page, userDTO, organizationId);
	}

	/**
	 * 通过ID查询用户信息
	 *
	 * @param id 用户ID
	 * @return 用户信息
	 */
	@Override
	public UserVO getUserVoById(Integer id) {
		return baseMapper.getUserVoById(id);
	}

	/**
	 * 删除用户
	 *
	 * @param sysUser 用户
	 * @return Boolean
	 */
	@Override
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#sysUser.username")
	public Boolean removeUserById(SysUser sysUser) {
		String uuid = UUID.fastUUID().toString();
		String username = sysUser.getUsername();
		String newUserName = String.format(username + "_" + uuid);
		sysUser.setUsername(newUserName.length() > 64 ? uuid : newUserName);
		CustomUser loginUser = SecurityUtils.getUser();
		sysUser.setRemark(sysUser.getRemark() + " " + new Timestamp(System.currentTimeMillis()) + " 已被" +
				(loginUser != null ? loginUser.getUsername() : "") + "删除");
		sysUserRoleService.removeRoleByUserId(sysUser.getUserId());
		this.removeById(sysUser.getUserId());
		removeUserCache(username);
		sysOrganizationService.clearUserOrganizationCache(sysUser.getUserId());
		return Boolean.TRUE;
	}

	@Override
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public R<Boolean> updateUserInfo(UserBaseInfoDTO userDto) {
		SysUser user = baseMapper.selectById(userDto.getUserId());
		SysUser sysUser = new SysUser();
		if (StrUtil.isNotBlank(userDto.getPassword())
				&& StrUtil.isNotBlank(userDto.getNewpassword1())) {
			if (!ENCODER.matches(userDto.getPassword(), user.getPassword())) {
				sysUser.setPassword(ENCODER.encode(userDto.getNewpassword1()));
			} else {
				log.warn("原密码错误，修改密码失败:{}", sysUser.getUsername());
				return R.failed("原密码错误，修改失败");
			}
		}

		removeOldAvatar(userDto.getAvatar(), user.getAvatar());

		sysUser.setRealname(userDto.getRealname());
		sysUser.setPhone(userDto.getPhone());
		sysUser.setUserId(user.getUserId());
		sysUser.setAvatar(userDto.getAvatar());
		boolean data = this.updateById(sysUser);
		if (data) {
			removeUserCache(sysUser.getUsername());
		}
		return R.ok(data);
	}

	@Override
	@CacheEvict(value = CacheConstants.USER_DETAILS, key = "#userDto.username")
	public Boolean updateUser(UserDTO userDto) {
		SysUser sysUser = this.getById(userDto.getUserId());

		removeOldAvatar(userDto.getAvatar(), sysUser.getAvatar());

		String oldOrganizationId = sysUser.getOrganizationId();
		String newOrganizationId = userDto.getOrganizationId();

		BeanUtils.copyProperties(userDto, sysUser,
				SysUser_.username.getProperty(),
				SysUser_.password.getProperty(),
				SysUser_.phone.getProperty(),
				SysUser_.salt.getProperty(),
				SysUser_.createTime.getProperty()
		);
		if (!Objects.equals(userDto.getPhone(), PrivacyUtil.hidePhone(sysUser.getPhone()))) {
			sysUser.setPhone(userDto.getPhone());
		}
		sysUser.setUpdateTime(LocalDateTime.now());

		this.updateById(sysUser);

		sysUserRoleService.remove(Wrappers.<SysUserRole>update().lambda()
				.and(updateWrapper -> updateWrapper.eq(SysUserRole::getUserId, userDto.getUserId()))
				.and(updateWrapper -> updateWrapper.eq(SysUserRole::getIsCanCancel, true)));

		//不可编辑角色
		List<SysUserRole> userRoleList = sysUserRoleService.list(Wrappers.<SysUserRole>lambdaQuery()
				.eq(SysUserRole::getIsCanCancel, false)
				.eq(SysUserRole::getUserId, userDto.getUserId()));
		Set<Integer> notCanCancelRoleIdList = new HashSet<>();
		userRoleList.forEach(u -> notCanCancelRoleIdList.add(u.getRoleId()));

		userDto.getRole().forEach(roleId -> {
			if (notCanCancelRoleIdList.contains(roleId)) {
				return;
			}
			SysUserRole userRole = new SysUserRole();
			userRole.setUserId(sysUser.getUserId());
			userRole.setRoleId(roleId);
			userRole.setIsCanCancel(true);

			userRole.insert();
		});

		//组织更新则更新缓存
		if (oldOrganizationId != newOrganizationId) {
			removeUserCache(sysUser.getUsername());
			sysOrganizationService.clearUserOrganizationCache(sysUser.getUserId());
		}
		return Boolean.TRUE;
	}

	/**
	 * 更换头像时，删除历史头像
	 *
	 * @param newAvatar 新的头像
	 * @param oldAvatar 旧的头像
	 */
	private void removeOldAvatar(String newAvatar, String oldAvatar) {
		if (newAvatar != null && !newAvatar.equals(oldAvatar)) {
			if (StringUtils.isNotBlank(oldAvatar)) {//删除旧的头像文件
				remoteSysFileService.deleteFile(Integer.parseInt(oldAvatar), SecurityConstants.FROM_IN);
			}
		}
	}

	/**
	 * 用户登出
	 *
	 * @param username 用户名
	 */
	@Override
	public void removeUserCache(String username) {
		Optional.ofNullable(cacheManager.getCache(CacheConstants.USER_DETAILS))
				.ifPresent(cache -> cache.evict(username));
		//更新缓存
		List<SysOauthClientDetails> clientDetailsList = sysOauthClientDetailsService.list();
		if (CollectionUtil.isNotEmpty(clientDetailsList)) {
			clientDetailsList.forEach(sysOauthClientDetails -> {
				String lastToken = TokenUtil.getLastToken(cacheManager, sysOauthClientDetails.getClientId(), username);
				if (StringUtils.isNotBlank(lastToken)) {
					//清除缓存信息，强制该用户退出登录
					TokenUtil.removeToken(cacheManager, tokenStore, lastToken);
				}
			});
		}
	}


	@Override
	public void removeUserCacheByRoleId(Integer roleId) {
		//清除用户缓存信息
		List<String> userNameList = sysUserMapper.findUserNamesByRoleId(roleId);
		Optional.ofNullable(userNameList)
				.ifPresent(_userNameList -> _userNameList.forEach(this::removeUserCache));

		//清除组织缓冲
		Optional.ofNullable(sysUserMapper.findUserIdByRoleId(roleId))
				.orElse(Collections.emptyList())
				.forEach(sysOrganizationService::clearUserOrganizationCache);
	}

	/**
	 * 查询上级组织的用户信息
	 *
	 * @param username 用户名
	 * @return R
	 */
	@Override
	public List<SysUser> listAncestorUsersByUsername(String username) {
		SysUser sysUser = this.getOne(Wrappers.<SysUser>query().lambda().eq(SysUser::getUsername, username));

		SysOrganization organization = sysOrganizationService.getById(sysUser.getOrganizationId());
		if (organization == null) {
			return null;
		}

		String parentId = organization.getParentId();
		return this.list(Wrappers.<SysUser>query().lambda().eq(SysUser::getOrganizationId, parentId));
	}


	/**
	 * 设置用户角色数据权限
	 *
	 * @param dp        dp
	 * @param userRoles 用户拥有的角色
	 */
	private void setUserRoleDataPermission(DataPermission dp, List<SysRole> userRoles) {
		SysRole dpRole = Optional.ofNullable(userRoles)
				.orElse(Collections.emptyList())
				.stream()
				.filter(o -> Objects.nonNull(o.getDataPermission()))
				.max(Comparator.comparing(o -> {
					Integer dataPermission = o.getDataPermission();
					RoleDataPermissionTypeEnum permissionType = RoleDataPermissionTypeEnum.ofByValue(dataPermission);
					if (permissionType == null) {
						return -1;
					}
					return permissionType.getWeight();
				}))
				.orElse(null);
		if (dpRole != null) {
			RoleDataPermissionTypeEnum type = RoleDataPermissionTypeEnum.ofByValue(dpRole.getDataPermission());
			dp.setType(type);
			if (RoleDataPermissionTypeEnum.CUSTOM.equals(type)) {
				List<RoleDataPermissionDimensionIdentifier> permissions = new ArrayList<>();
				Optional.ofNullable(sysRoleDataPermissionService
								.lambdaQuery()
								.select(SysRoleDataPermission::getId,
										SysRoleDataPermission::getDimension,
										SysRoleDataPermission::getIdentifierValue)
								.eq(SysRoleDataPermission::getRoleId, dpRole.getRoleId())
								.list())
						.orElse(Collections.emptyList())
						.stream()
						.collect(Collectors.groupingBy(SysRoleDataPermission::getDimension))
						.forEach((_dimension, roleDataPermissions) ->
								permissions.add(RoleDataPermissionDimensionIdentifier.builder()
										.dimension(RoleDataPermissionDimensionEnum.ofByValue(_dimension))
										.identifierValues(roleDataPermissions.stream()
												.map(SysRoleDataPermission::getIdentifierValue)
												.filter(StringUtils::isNotBlank)
												.collect(Collectors.toList()))
										.build()));
				dp.setRolePermissions(permissions);
			}
		}
	}

	/**
	 * 设置用户数据权限
	 *
	 * @param dp     dp
	 * @param userId 用户id
	 */
	private void setUserDataPermission(DataPermission dp, Integer userId) {
		List<UserDataPermissionDimensionIdentifier> permissions = new ArrayList<>();
		Optional.ofNullable(sysUserDataPermissionService
						.lambdaQuery()
						.select(SysUserDataPermission::getId,
								SysUserDataPermission::getDimension,
								SysUserDataPermission::getIdentifierValue)
						.eq(SysUserDataPermission::getUserId, userId)
						.list())
				.orElse(Collections.emptyList())
				.stream()
				.collect(Collectors.groupingBy(SysUserDataPermission::getDimension))
				.forEach((_dimension, roleDataPermissions) ->
						permissions.add(UserDataPermissionDimensionIdentifier.builder()
								.dimension(UserDataPermissionDimensionEnum.ofByValue(_dimension))
								.identifierValues(roleDataPermissions.stream()
										.map(SysUserDataPermission::getIdentifierValue)
										.filter(StringUtils::isNotBlank)
										.collect(Collectors.toList()))
								.build()));
		dp.setUserPermissions(permissions);
	}
}
