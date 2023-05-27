package com.xjinyao.xcloud.common.security.component;

import com.xjinyao.xcloud.common.security.properties.PermitAllUrlProperties;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.client.RestTemplate;

/**
 * @date 2019/03/08
 *
 * <p>
 * 1. 支持remoteTokenServices 负载均衡 2. 支持 获取用户全部信息 3. 接口对外暴露，不校验 Authentication Header 头
 */
@Slf4j
public class CustomResourceServerConfigurerAdapter extends ResourceServerConfigurerAdapter {

    @Autowired
    protected ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

    @Autowired
    protected CustomRemoteTokenServices remoteTokenServices;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private PermitAllUrlProperties permitAllUrl;

    @Autowired
    private RestTemplate lbRestTemplate;

    @Autowired
    private CustomBearerTokenExtractor bearerTokenExtractor;

    /**
     * 默认的配置，对外暴露
     *
     * @param httpSecurity
     */
    @Override
    @SneakyThrows
    public void configure(HttpSecurity httpSecurity) {
        // 允许使用iframe 嵌套，避免swagger-ui 不被加载的问题
        httpSecurity.headers().frameOptions().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();
        registry.antMatchers(permitAllUrl.getUrls().toArray(new String[]{})).permitAll();
        registry.anyRequest().authenticated().and().csrf().disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
//        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
//        UserAuthenticationConverter userTokenConverter = new CustomUserAuthenticationConverter();
//        accessTokenConverter.setUserTokenConverter(userTokenConverter);

        remoteTokenServices.setLbRestTemplate(lbRestTemplate);
//        remoteTokenServices.setRestTemplate(lbRestTemplate);
//        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);
        resources.authenticationEntryPoint(resourceAuthExceptionEntryPoint)
                .tokenExtractor(bearerTokenExtractor)
                .accessDeniedHandler(accessDeniedHandler)
                .resourceId("tcs")
                .tokenServices(remoteTokenServices);
    }

}
