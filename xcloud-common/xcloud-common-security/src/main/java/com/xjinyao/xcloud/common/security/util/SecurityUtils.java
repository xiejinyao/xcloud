package com.xjinyao.xcloud.common.security.util;


import cn.hutool.core.util.StrUtil;
import com.xjinyao.xcloud.admin.api.dto.DataPermission;
import com.xjinyao.xcloud.admin.api.enums.RoleCodes;
import com.xjinyao.xcloud.admin.api.enums.RoleDataPermissionTypeEnum;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

/**
 * 安全工具类
 */
@UtilityClass
public class SecurityUtils {

	/**
	 * 获取Authentication
	 */
	public Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * 获取用户
	 */
	public CustomUser getUser(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof CustomUser) {
			return (CustomUser) principal;
		}
		return null;
	}

	/**
	 * 获取用户
	 */
	public CustomUser getUser() {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return null;
		}
		return getUser(authentication);
	}

	/**
	 * 获取用户Id
	 */
	public Integer getUserId() {
		Authentication authentication = getAuthentication();
		if (authentication == null) {
			return null;
		}
		CustomUser user = getUser(authentication);
		if (user != null) {
			return user.getId();
		}
		return null;
	}

	/**
	 * 获取用户角色id信息
	 *
	 * @return 角色id集合
	 */
	public List<Integer> getRoleIds() {
		Authentication authentication = getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		List<Integer> roleIds = new ArrayList<>();
		authorities.stream()
				.filter(granted -> StrUtil.startWith(granted.getAuthority(), SecurityConstants.ROLE_ID))
				.forEach(granted -> {
					String id = StrUtil.removePrefix(granted.getAuthority(), SecurityConstants.ROLE_ID);
					roleIds.add(Integer.parseInt(id));
				});
		return roleIds;
	}

	/**
	 * 获取用户角色编码信息
	 *
	 * @return 角色编码集合
	 */
	public List<String> getRoleCodes() {
		Authentication authentication = getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return getRoleCodes(authorities);
	}

	/**
	 * 获取用户角色编码信息
	 *
	 * @param authorities 授权信息
	 * @return {@link List}<{@link String}>
	 */
	public List<String> getRoleCodes(Collection<? extends GrantedAuthority> authorities) {
		List<String> roleCodes = new ArrayList<>();
		authorities.stream()
				.filter(granted -> StrUtil.startWith(granted.getAuthority(), SecurityConstants.ROLE_CODE))
				.forEach(granted -> {
					String code = StrUtil.removePrefix(granted.getAuthority(), SecurityConstants.ROLE_CODE);
					roleCodes.add(code);
				});
		return roleCodes;
	}

	/**
	 * 是否是超级管理员
	 *
	 * @return
	 */
	public boolean isSuperAdmin() {
		return Optional.ofNullable(getRoleCodes())
				.orElse(Collections.emptyList())
				.contains(RoleCodes.ROLE_ADMIN.name());
	}

	/**
	 * 是否是超级管理员
	 *
	 * @return
	 */
	public boolean isSuperAdmin(CustomUser user) {
		Collection<GrantedAuthority> authorities = user.getAuthorities();
		return Optional.ofNullable(getRoleCodes(authorities))
				.orElse(Collections.emptyList())
				.contains(RoleCodes.ROLE_ADMIN.name());
	}

	/**
	 * 获取当前登录用户组织编码
	 *
	 * @return
	 */
	public String getOrganizationCode() {
		CustomUser user = getUser();
		if (user != null) {
			return user.getOrganizationCode();
		}
		return null;
	}

	/**
	 * 获取当前登录用户组织Id
	 *
	 * @return
	 */
	public String getOrganizationId() {
		CustomUser user = getUser();
		if (user != null) {
			return user.getOrganizationId();
		}
		return null;
	}

	/**
	 * 获取用户的数据权限
	 *
	 * @return
	 */
	public DataPermission getUserDataPermission() {
		DataPermission none = new DataPermission() {{
			this.setType(RoleDataPermissionTypeEnum.NONE);
		}};
		CustomUser user = getUser();
		if (user != null) {
			return Optional.ofNullable(user.getDataPermission()).orElse(none);
		}
		return none;
	}
}
