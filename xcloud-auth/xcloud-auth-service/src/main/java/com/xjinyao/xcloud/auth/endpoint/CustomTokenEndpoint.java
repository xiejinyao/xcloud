package com.xjinyao.xcloud.auth.endpoint;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjinyao.xcloud.common.core.constant.CommonConstants;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.core.util.R;
import com.xjinyao.xcloud.common.security.annotation.Inner;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.service.CustomUserDetailsServiceImpl;
import com.xjinyao.xcloud.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @date 2019/2/1 删除token端点
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class CustomTokenEndpoint {

	private final ClientDetailsService clientDetailsService;

	private final TokenStore tokenStore;

	private final CustomRemoteTokenServices remoteTokenServices;

	private final RedisTemplate redisTemplate;

	private final CacheManager cacheManager;

	/**
	 * 认证页面
	 *
	 * @param modelAndView
	 * @param error        表单登录失败处理回调的错误信息
	 * @return ModelAndView
	 */
	@GetMapping("/login")
	public ModelAndView require(ModelAndView modelAndView, @RequestParam(required = false) String error) {
		modelAndView.setViewName("ftl/login");
		modelAndView.addObject("error", error);
		return modelAndView;
	}


	/**
	 * 通过令牌直接登录
	 *
	 * @param token 令牌
	 * @return {@link R}
	 */
	@GetMapping("check_token")
	public R<Boolean> checkToken(String token) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
		if (accessToken == null || StrUtil.isBlank(accessToken.getValue())) {
			// 非第三方系统token
			log.info("检查非第三方系统token：{}", token);
			OAuth2Authentication oAuth2Authentication = remoteTokenServices.loadAuthentication(token, true);
			if (oAuth2Authentication != null) {
				return R.ok(Boolean.TRUE, "登录成功！");
			}
		}
		return R.failed(Boolean.FALSE, "无法识别的 token，登录失败");
	}

	/**
	 * 确认授权页面
	 *
	 * @param request
	 * @param session
	 * @param modelAndView
	 * @return
	 */
	@GetMapping("/confirm_access")
	public ModelAndView confirm(HttpServletRequest request, HttpSession session, ModelAndView modelAndView) {
		Map<String, Object> scopeList = (Map<String, Object>) request.getAttribute("scopes");
		modelAndView.addObject("scopeList", scopeList.keySet());

		Object auth = session.getAttribute("authorizationRequest");
		if (auth != null) {
			AuthorizationRequest authorizationRequest = (AuthorizationRequest) auth;
			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
			modelAndView.addObject("app", clientDetails.getAdditionalInformation());
			modelAndView.addObject("user", SecurityUtils.getUser());
		}

		modelAndView.setViewName("ftl/confirm");
		return modelAndView;
	}

	/**
	 * 退出并删除token
	 *
	 * @param authHeader Authorization
	 */
	@DeleteMapping("/logout")
	public R<Boolean> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
		if (StrUtil.isBlank(authHeader)) {
			return R.ok();
		}

		String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StrUtil.EMPTY).trim();
		return removeToken(tokenValue);
	}

	/**
	 * 令牌管理调用
	 *
	 * @param token token
	 */
	@Inner
	@DeleteMapping("/{token}")
	public R<Boolean> removeToken(@PathVariable("token") String token) {
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(token);
		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache == null) {
			return R.ok();
		}
		if (accessToken == null || StrUtil.isBlank(accessToken.getValue())) {
			//判断是否为第三方token登录 + 清空用户信息
			OAuth2Authentication oAuth2Authentication = remoteTokenServices.loadAuthentication(token, true);
			if (oAuth2Authentication != null) {
				Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();
				Object principal = userAuthentication.getPrincipal();
				if (principal instanceof CustomUser) {
					CustomUser user = (CustomUser) principal;
					Map<String, Object> extendedParameters = Optional.ofNullable(user.getExtendedParameters())
							.orElse(Collections.emptyMap());
					String thirdPartyId = extendedParameters.getOrDefault(CustomUserDetailsServiceImpl.THIRD_PARTY_ID,
							StringUtils.EMPTY).toString();
					String sources = extendedParameters.getOrDefault(CustomUserDetailsServiceImpl.SOURCES,
							StringUtils.EMPTY).toString();
					String username = user.getUsername();
					String key = CustomUserDetailsServiceImpl.getUsernameAndThirdPartyIdKey(username, thirdPartyId,
							sources);
					// 清空用户信息
					cache.evict(key);
				}
			}
			return R.ok();
		} else {
			//本系统token
			OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(accessToken);
			// 清空用户信息
			String key = CustomUserDetailsServiceImpl.getUsernameKey(auth2Authentication.getName());
			cache.evict(key);

			// 清空access token
			tokenStore.removeAccessToken(accessToken);

			// 清空 refresh token
			OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
			tokenStore.removeRefreshToken(refreshToken);
		}
		return R.ok();
	}

	/**
	 * 查询token
	 *
	 * @param params 分页参数
	 * @return
	 */
	@Inner
	@PostMapping("/page")
	public R<Page> tokenList(@RequestBody Map<String, Object> params) {
		// 根据分页参数获取对应数据
		String key = String.format("%sauth_to_access:*", CacheConstants.PROJECT_OAUTH_ACCESS);
		List<String> pages = findKeysForPage(key, MapUtil.getInt(params, CommonConstants.CURRENT),
				MapUtil.getInt(params, CommonConstants.SIZE));

		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		Page result = new Page(MapUtil.getInt(params, CommonConstants.CURRENT),
				MapUtil.getInt(params, CommonConstants.SIZE));
		result.setRecords(redisTemplate.opsForValue().multiGet(pages));
		result.setTotal(redisTemplate.keys(key).size());
		return R.ok(result);
	}

	private List<String> findKeysForPage(String patternKey, int pageNum, int pageSize) {
		Cursor cursor = null;
		List<String> result = null;
		try {
			ScanOptions options = ScanOptions.scanOptions().count(1000L).match(patternKey).build();
			RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
			cursor = (Cursor) redisTemplate.executeWithStickyConnection(
					redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
			result = new ArrayList<>();
			int tmpIndex = 0;
			int startIndex = (pageNum - 1) * pageSize;
			int end = pageNum * pageSize;

			assert cursor != null;
			while (cursor.hasNext()) {
				if (tmpIndex >= startIndex && tmpIndex < end) {
					result.add(cursor.next().toString());
					tmpIndex++;
					continue;
				}
				if (tmpIndex >= end) {
					break;
				}
				tmpIndex++;
				cursor.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

}
