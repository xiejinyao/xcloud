package com.xjinyao.xcloud.auth.interceptor;

import com.xjinyao.xcloud.common.security.util.AuthUtils;
import com.xjinyao.xcloud.common.security.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

/**
 * @description 单点授权拦截器，拦截 org.springframework.security.oauth2.provider.endpoint.TokenEndpoint 类的授权方法
 * @createDate 2020/8/14 15:50
 */
@Slf4j
public class SsoTokenAuthHandlerInterceptor implements HandlerInterceptor {

    private final static String SSO = "sso";
    private final static String USERNAME = "username";

    private ClientDetailsService clientDetailsService;
    private TokenStore tokenStore;
    private CacheManager cacheManager;
    private OAuth2RequestFactory oAuth2RequestFactory;
    private TokenGranter tokenGranter;

    public SsoTokenAuthHandlerInterceptor(OAuth2RequestFactory oAuth2RequestFactory,
                                          TokenGranter tokenGranter,
                                          ClientDetailsService clientDetailsService,
                                          TokenStore tokenStore,
                                          CacheManager cacheManager) {
        this.oAuth2RequestFactory = oAuth2RequestFactory;
        this.tokenGranter = tokenGranter;
        this.clientDetailsService = clientDetailsService;
        this.tokenStore = tokenStore;
        this.cacheManager = cacheManager;
    }

    /**
     * 在授权方法执行前，检查当前授权请求的终端是否设置单点登录，若设置了单点登录，则清除掉最新的授权信息
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (check(request)) {
            return true;
        }
        UsernamePasswordAuthenticationToken principal = getPrincipal(request);
        if (principal != null) {
            String clientId = getClientId(principal);
            ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);
            Map<String, Object> additionalInformation = authenticatedClient.getAdditionalInformation();
            Object sso = additionalInformation.getOrDefault(SSO, "false");
            //单点登录自动移除最新的授权相关信息
            if (BooleanUtils.toBoolean(sso.toString())) {
                String username = request.getParameter(USERNAME);
                if (StringUtils.isNotBlank(username)) {
                    Object latestToken = TokenUtil.getLastToken(cacheManager, clientId, username);
                    if (latestToken != null) {
                        TokenUtil.removeToken(cacheManager, tokenStore, latestToken.toString());
                    }
                }
            }
        }
        return true;
    }

    private boolean check(HttpServletRequest request) {
        if (clientDetailsService == null || tokenStore == null || cacheManager == null || oAuth2RequestFactory == null || tokenGranter == null) {
            return true;
        }
        if (isRefreshTokenRequest(request)) {
            return true;
        }
        return false;
    }

    private boolean isRefreshTokenRequest(HttpServletRequest request) {
        return "refresh_token".equals(request.getParameter("grant_type")) && request.getParameter("refresh_token") != null;
    }

    private UsernamePasswordAuthenticationToken getPrincipal(HttpServletRequest request) {
        UsernamePasswordAuthenticationToken principal = null;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotBlank(authHeader)) {
            String[] split = AuthUtils.extractAndDecodeHeader(request);
            if (ArrayUtils.isNotEmpty(split)) {
                principal = new UsernamePasswordAuthenticationToken(split[0], split[1], Collections.emptyList());
            }
        }
        return principal;
    }

    private String getClientId(Principal principal) {
        Authentication client = (Authentication) principal;
        if (!client.isAuthenticated()) {
            throw new InsufficientAuthenticationException("The client is not authenticated.");
        }
        String clientId = client.getName();
        if (client instanceof OAuth2Authentication) {
            // Might be a client and user combined authentication
            clientId = ((OAuth2Authentication) client).getOAuth2Request().getClientId();
        }
        return clientId;
    }
}
