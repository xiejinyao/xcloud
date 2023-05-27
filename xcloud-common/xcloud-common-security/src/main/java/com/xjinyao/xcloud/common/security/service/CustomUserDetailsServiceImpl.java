package com.xjinyao.xcloud.common.security.service;

import cn.hutool.core.util.ArrayUtil;
import com.xjinyao.xcloud.admin.api.dto.UserInfo;
import com.xjinyao.xcloud.admin.api.entity.SysUser;
import com.xjinyao.xcloud.admin.api.feign.RemoteUserService;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

/**
 * 用户详细信息
 */
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsServiceImpl implements UserDetailsService {

	public static final String THIRD_PARTY_ID = "thirdPartyId";
	public static final String SOURCES = "sources";

	private final RemoteUserService remoteUserService;

	private final CacheManager cacheManager;


	public static String getUsernameAndThirdPartyIdKey(String username, String thirdPartyId, String sources) {
		return username + "_" + thirdPartyId + "_" + sources;
	}


	public static String getUsernameKey(String username) {
		return username;
	}

	/**
	 * 用户密码登录
	 *
	 * @param username 用户名
	 * @return
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String username) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		String key = getUsernameKey(username);
		if (cache != null) {
			CustomUser customUser = cache.get(key, CustomUser.class);
			if (customUser != null) {
				return customUser;
			}
		}

		R<UserInfo> result = remoteUserService.info(key, SecurityConstants.FROM_IN);
		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(key, userDetails);
		}
		return userDetails;
	}

	/**
	 * 加载用户通过用户名和第三方id
	 * 用户名+第三方Id登录
	 *
	 * @param username     用户名
	 * @param thirdPartyId 第三方身份
	 * @param sources      来源
	 * @return {@link UserDetails}
	 */
	public UserDetails loadUserByUsernameAndThirdPartyId(String username, String thirdPartyId, String sources) {
		String key = getUsernameAndThirdPartyIdKey(username, thirdPartyId, sources);
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null) {
			CustomUser customUser = cache.get(key, CustomUser.class);
			if (customUser != null) {
				return customUser;
			}
		}

		R<UserInfo> result = remoteUserService.info(username, thirdPartyId, sources, SecurityConstants.FROM_IN);
		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(key, userDetails);
		}
		return userDetails;
	}

	/**
	 * 构建userdetails
	 *
	 * @param result 用户信息
	 * @return
	 */
	private UserDetails getUserDetails(R<UserInfo> result) {
		if (result == null || result.getData() == null) {
			throw new UsernameNotFoundException("用户不存在");
		}
		UserInfo info = result.getData();
		Set<String> dbAuthsSet = new HashSet<>();
		if (ArrayUtil.isNotEmpty(info.getRoleIds())) {
			// 获取角色
			Arrays.stream(info.getRoleIds()).forEach(id -> dbAuthsSet.add(SecurityConstants.ROLE_ID + id));
		}
		if (ArrayUtil.isNotEmpty(info.getRoleCodes())) {
			// 获取角色
			Arrays.stream(info.getRoleCodes()).forEach(code -> dbAuthsSet.add(SecurityConstants.ROLE_CODE + code));
		}
		// 获取资源
		dbAuthsSet.addAll(Arrays.asList(info.getPermissions()));

		Collection<? extends GrantedAuthority> authorities = AuthorityUtils
				.createAuthorityList(dbAuthsSet.toArray(new String[0]));
		SysUser user = info.getSysUser();


		//扩展参数
		Map<String, Object> extendedParameters = new HashMap<>();
		extendedParameters.put(THIRD_PARTY_ID, user.getThirdPartyId());
		extendedParameters.put(SOURCES, user.getSources());
		// 构造security用户
		return new CustomUser(user.getUserId(),
				user.getOrganizationId(),
				info.getOrganizationCode(),
				user.getUsername(),
				SecurityConstants.BCRYPT + user.getPassword(),
				BooleanUtils.isFalse(user.getLockFlag()),
				true,
				true,
				true,
				authorities,
				info.getDataPermission(),
				extendedParameters);
	}

}
