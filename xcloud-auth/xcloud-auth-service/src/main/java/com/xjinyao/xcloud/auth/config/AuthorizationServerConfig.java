package com.xjinyao.xcloud.auth.config;

import com.xjinyao.xcloud.auth.interceptor.SsoTokenAuthHandlerInterceptor;
import com.xjinyao.xcloud.common.core.constant.SecurityConstants;
import com.xjinyao.xcloud.common.core.redis.constant.CacheConstants;
import com.xjinyao.xcloud.common.security.component.CustomWebResponseExceptionTranslator;
import com.xjinyao.xcloud.common.security.service.CustomClientDetailsService;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import com.xjinyao.xcloud.common.security.service.CustomUser;
import com.xjinyao.xcloud.common.security.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @date 2019/2/1 认证服务器配置
 */
@Configuration
@RequiredArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final DataSource dataSource;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final RedisConnectionFactory redisConnectionFactory;
    private final CacheManager cacheManager;
    private final CustomRemoteTokenServices customRemoteTokenServices;

    @Override
    @SneakyThrows
    public void configure(ClientDetailsServiceConfigurer clients) {
        CustomClientDetailsService clientDetailsService = new CustomClientDetailsService(dataSource);
        clientDetailsService.setSelectClientDetailsSql(SecurityConstants.DEFAULT_SELECT_STATEMENT);
        clientDetailsService.setFindClientDetailsSql(SecurityConstants.DEFAULT_FIND_STATEMENT);
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.allowFormAuthenticationForClients().checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST).tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancer())
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager)
                .reuseRefreshTokens(false)
                .pathMapping("/oauth/confirm_access", "/token/confirm_access")
                .exceptionTranslator(new CustomWebResponseExceptionTranslator());

        //新增单点授权拦截器
        endpoints.addInterceptor(new SsoTokenAuthHandlerInterceptor(endpoints.getOAuth2RequestFactory(),
                endpoints.getTokenGranter(),
                endpoints.getClientDetailsService(),
                endpoints.getTokenStore(), cacheManager));
    }

    @Bean
    public TokenStore tokenStore() {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        tokenStore.setPrefix(CacheConstants.PROJECT_OAUTH_ACCESS);
        return tokenStore;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            String latestToken = accessToken.getValue();
            CustomUser customUser = (CustomUser) authentication.getUserAuthentication().getPrincipal();

            //缓存账号在该终端的最新token
            String clientId = authentication.getOAuth2Request().getClientId();
            TokenUtil.storeLastToken(cacheManager, clientId, customUser.getUsername(), latestToken);

            final Map<String, Object> additionalInfo = new HashMap<>(6);
            additionalInfo.put(SecurityConstants.DETAILS_LICENSE, SecurityConstants.PROJECT_LICENSE);
            additionalInfo.put(SecurityConstants.DETAILS_USER_ID, customUser.getId());
            additionalInfo.put(SecurityConstants.DETAILS_USERNAME, customUser.getUsername());
            additionalInfo.put(SecurityConstants.DETAILS_ORGANIZATION_ID, customUser.getOrganizationId());
            additionalInfo.put(SecurityConstants.DETAILS_ORGANIZATION_CODE, customUser.getOrganizationCode());
            additionalInfo.put(SecurityConstants.DATA_PERMISSION, customUser.getDataPermission());
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

            return accessToken;
        };
    }

}
