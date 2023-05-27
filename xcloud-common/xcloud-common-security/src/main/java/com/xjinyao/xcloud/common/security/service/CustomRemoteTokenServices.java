package com.xjinyao.xcloud.common.security.service;

import com.xjinyao.xcloud.common.core.util.StringUtils;
import com.xjinyao.xcloud.common.security.component.AuthenticationConverter;
import com.xjinyao.xcloud.common.security.component.CustomUserAuthenticationConverter;
import com.xjinyao.xcloud.common.security.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 查询 /oauth/check_token 终结点以获取访问令牌的内容。如果终结点返回 400 响应，则表示令牌无效。
 * 支持多个认证中心同时对token进行验证
 *
 * <pre>
 * 多认证中心认证配置示例：
 *
 * security:
 *   oauth2:
 *     resource:
 * 		 token-info-uri: http://platform-auth/token/check
 *       token-info-urls:
 *         - {
 *           url: http://platform-auth/oauth/check_token
 *         }
 *         - {
 *           url: https://gateway-test.tq-service.com/uaa-center/oauth/check_token,
 *           client-id: tcs,
 *           client-secret: tcs,
 *           token-name: token,
 *           in-the-same-registry-center: false
 *         }
 * </pre>
 *
 * @author 谢进伟
 * @createDate 2023/2/22 10:39
 */
@Slf4j
public class CustomRemoteTokenServices implements ResourceServerTokenServices, InitializingBean, ApplicationContextAware {

    private final String ACTIVE = "active";
    private RestOperations lbRestTemplate;
    private RestOperations restTemplate;

    private List<SecurityProperties.CheckTokenUrlInfo> checkTokenUrlInfos;

    /**
     * 令牌转换器
     */
    private ConcurrentHashMap<String, AccessTokenConverter> accessTokenConverters = new ConcurrentHashMap<>();

    private ResourceServerProperties resource;

    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        Optional.of(applicationContext.getBeansOfType(AuthenticationConverter.class, true, true))
                .orElse(Collections.emptyMap())
                .values()
                .forEach(bean -> {
                    DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
                    accessTokenConverter.setUserTokenConverter(new CustomUserAuthenticationConverter());
                    accessTokenConverters.put(bean.getType(), accessTokenConverter);
                });
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public CustomRemoteTokenServices() {
        restTemplate = new RestTemplate();
        ((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });
    }

    public void setLbRestTemplate(RestOperations lbRestTemplate) {
        this.lbRestTemplate = lbRestTemplate;
    }

    public void setCheckTokenUrlInfos(List<SecurityProperties.CheckTokenUrlInfo> checkTokenUrlInfos) {
        this.checkTokenUrlInfos = checkTokenUrlInfos;
    }

    public void setResource(ResourceServerProperties resource) {
        this.resource = resource;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        return loadAuthentication(accessToken, false);
    }

    public OAuth2Authentication loadAuthentication(String accessToken, boolean ignoreError)
            throws AuthenticationException, InvalidTokenException {
        String tokenInfoUri = resource.getTokenInfoUri();
        if (CollectionUtils.isEmpty(checkTokenUrlInfos)) {
            if (StringUtils.isBlank(tokenInfoUri)) {
                throw new InvalidTokenException(accessToken);
            }
            this.checkTokenUrlInfos.add(new SecurityProperties.CheckTokenUrlInfo() {{
                this.setUrl(tokenInfoUri);
            }});
        }

        for (int i = 0; i < checkTokenUrlInfos.size(); i++) {
            SecurityProperties.CheckTokenUrlInfo checkTokenUrlInfo = checkTokenUrlInfos.get(i);

            String checkTokenEndpointUrl = checkTokenUrlInfo.getUrl();
            String tokenName = checkTokenUrlInfo.getTokenName();
            String clientId = StringUtils.defaultString(checkTokenUrlInfo.getClientId(),
                    resource.getClientId());
            String clientSecret = StringUtils.defaultString(checkTokenUrlInfo.getClientSecret(),
                    resource.getClientSecret());
            String tokenConverterTypes = checkTokenUrlInfo.getTokenConverterTypes();
            AccessTokenConverter tokenConverter = this.accessTokenConverters.get(tokenConverterTypes);
            if (tokenConverter == null) {
                continue;
            }
            Map<String, String> additionalParameters = checkTokenUrlInfo.getAdditionalParameters();
            Boolean inTheSameRegistryCenter = checkTokenUrlInfo.getInTheSameRegistryCenter();

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            if (additionalParameters != null) {
                formData.setAll(additionalParameters);
            }

            formData.add(tokenName, accessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
            Map<String, Object> map = postForMap(checkTokenEndpointUrl, formData, headers, inTheSameRegistryCenter,
                    ignoreError);

            log.info("check_token result is {}", map);
            if (CollectionUtils.isEmpty(map)) {
                if (log.isDebugEnabled()) {
                    log.info("check_token returned empty");
                }
                continue;
            }

            if (map.containsKey("error")) {
                if (log.isDebugEnabled()) {
                    log.info("check_token returned error: " + map.get("error"));
                }
                continue;
            }

            if (BooleanUtils.toBoolean(map.getOrDefault(ACTIVE, false).toString())) {
                return tokenConverter.extractAuthentication(map);
            } else if (map.containsKey(AccessTokenConverter.AUD)
                    && map.containsKey(AccessTokenConverter.CLIENT_ID)
                    && map.containsKey(AccessTokenConverter.EXP)
                    && map.containsKey(AccessTokenConverter.SCOPE)) {
                return tokenConverter.extractAuthentication(map);
            } else {
                log.info("check_token returned active attribute: " + map.get("active"));
            }
        }


        throw new InvalidTokenException(accessToken);

    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        if (clientId == null || clientSecret == null) {
            log.warn("Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.");
        }

        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers,
                                           Boolean inTheSameRegistryCenter, boolean ignoreError) {
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        @SuppressWarnings("rawtypes")
        Map map = null;
        try {
            if (inTheSameRegistryCenter) {
                map = lbRestTemplate.exchange(path, HttpMethod.POST,
                        new HttpEntity<>(formData, headers), Map.class).getBody();
            } else {
                map = restTemplate.exchange(path, HttpMethod.POST,
                        new HttpEntity<>(formData, headers), Map.class).getBody();
            }
        } catch (RestClientException e) {
            if (ignoreError) {
                //ignore
            } else {
                throw e;
            }

        }
        @SuppressWarnings("unchecked")
        Map<String, Object> result = map;
        return result;
    }
}
