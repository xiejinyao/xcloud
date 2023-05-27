package com.xjinyao.xcloud.common.security.util;

import cn.hutool.core.util.StrUtil;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @description 授权token工具类
 * @createDate 2020/8/14 17:38
 */
public class TokenUtil {

	/**
	 * 移除授权相关信息
	 *
	 * @param token        授权的token
	 * @param cacheManager 缓存管理器
	 * @param tokenStore   token保存工具实列
	 */
	public static void removeToken(CacheManager cacheManager, TokenStore tokenStore, String token) {
		if (cacheManager != null && tokenStore != null) {
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
			if (accessToken == null || StrUtil.isBlank(accessToken.getValue())) {
				return;
			}
			OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(accessToken);
			String clientId = auth2Authentication.getOAuth2Request().getClientId();
			// 清空用户信息
			Optional.ofNullable(cacheManager.getCache(CacheConstants.USER_DETAILS))
					.ifPresent(cache -> {
						cache.evict(auth2Authentication.getName());
						cache.evict(getUserLastTokenCacheKey(clientId, auth2Authentication.getName()));
					});


			// 清空access token
			tokenStore.removeAccessToken(accessToken);

			// 清空 refresh token
			OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
			tokenStore.removeRefreshToken(refreshToken);
		}
	}

	/**
	 * 保存用户在某一终端的最新授权token
	 *
	 * @param cacheManager 缓存管理器
	 * @param clientId     终端id
	 * @param username     用户名
	 * @param latestToken  最新授权token
	 */
	public static void storeLastToken(CacheManager cacheManager, String clientId, String username, String latestToken) {
		if (cacheManager != null) {
			//按终端缓存用户最新Token
			Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
			cache.put(getUserLastTokenCacheKey(clientId, username), latestToken);
		}
	}

	/**
	 * 获取用户在指定终端的最新token信息
	 *
	 * @param cacheManager 缓存管理器
	 * @param clientId     终端id
	 * @param username     用户名
	 * @return
	 */
	public static String getLastToken(CacheManager cacheManager, String clientId, String username) {
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(getUserLastTokenCacheKey(clientId, username)) != null) {
			AtomicReference<String> latestToken = new AtomicReference<>();
			Optional.ofNullable(cache.get(getUserLastTokenCacheKey(clientId, username)))
					.map(Cache.ValueWrapper::get)
					.ifPresent(d -> latestToken.set(d.toString()));
			return latestToken.get() == null ? null : latestToken.toString();
		}
		return null;
	}

	private static String getUserLastTokenCacheKey(String clientId, String username) {
		return clientId + ":" + username + ":lasttoken";
	}
}
