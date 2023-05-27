package com.xjinyao.xcloud.common.security.service;

import com.xjinyao.xcloud.admin.api.dto.DataPermission;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @date 2019/2/1 扩展用户信息
 */
public class CustomUser extends User {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	@Getter
	private final Integer id;
	/**
	 * 组织ID
	 */
	@Getter
	@Setter
	private String organizationId;
	/**
	 * 组织编码
	 */
	@Getter
	@Setter
	private String organizationCode;

	/**
	 * 数据权限
	 */
	@Getter
	@Setter
	private DataPermission dataPermission;


	@Getter
	@Setter
	private Map<String, Object> extendedParameters;

	/**
	 * 自定义用户
	 * Construct the <code>User</code> with the details required by
	 * {@link DaoAuthenticationProvider}.
	 *
	 * @param id                    用户ID
	 * @param organizationId        组织ID
	 * @param username              the username presented to the
	 *                              <code>DaoAuthenticationProvider</code>
	 * @param password              the password that should be presented to the
	 *                              <code>DaoAuthenticationProvider</code>
	 * @param enabled               set to <code>true</code> if the user is enabled
	 * @param accountNonExpired     set to <code>true</code> if the account has not expired
	 * @param credentialsNonExpired set to <code>true</code> if the credentials have not
	 *                              expired
	 * @param accountNonLocked      set to <code>true</code> if the account is not locked
	 * @param authorities           the authorities that should be granted to the caller if they
	 *                              presented the correct username and password and the user is enabled. Not null.
	 * @param organizationCode      组织代码
	 * @param dataPermission        数据权限
	 * @param extendedParameters    扩展参数
	 */
	public CustomUser(Integer id,
					  String organizationId,
					  String organizationCode,
					  String username,
					  String password,
					  boolean enabled,
					  boolean accountNonExpired,
					  boolean credentialsNonExpired,
					  boolean accountNonLocked,
					  Collection<? extends GrantedAuthority> authorities,
					  DataPermission dataPermission,
					  Map<String, Object> extendedParameters) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.id = id;
		this.organizationId = organizationId;
		this.organizationCode = organizationCode;
		this.dataPermission = dataPermission;
		this.extendedParameters = Optional.ofNullable(extendedParameters).orElse(Collections.emptyMap());
	}

}
