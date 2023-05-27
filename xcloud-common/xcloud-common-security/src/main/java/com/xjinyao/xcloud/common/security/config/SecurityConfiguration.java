package com.xjinyao.xcloud.common.security.config;

import com.xjinyao.xcloud.admin.api.feign.RemoteUserService;
import com.xjinyao.xcloud.common.security.component.CustomUserAuthenticationConverter;
import com.xjinyao.xcloud.common.security.properties.SecurityProperties;
import com.xjinyao.xcloud.common.security.service.CustomClientDetailsService;
import com.xjinyao.xcloud.common.security.service.CustomRemoteTokenServices;
import com.xjinyao.xcloud.common.security.service.CustomUserDetailsServiceImpl;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 * @author 谢进伟
 * @createDate 2023/2/22 10:41
 */
@EnableConfigurationProperties(value = {SecurityProperties.class})
public class SecurityConfiguration {

	@Bean
//	@Primary
	public CustomClientDetailsService customClientDetailsService(DataSource dataSource) {
		return new CustomClientDetailsService(dataSource);
	}

	@Bean
	public CustomUserDetailsServiceImpl customUserDetailsServiceImpl(RemoteUserService remoteUserService,
																	 CacheManager cacheManager) {
		return new CustomUserDetailsServiceImpl(remoteUserService, cacheManager);
	}

	@Bean
	public CustomUserAuthenticationConverter customUserAuthenticationConverter() {
		return new CustomUserAuthenticationConverter();
	}

	@Bean
	@Primary
	public CustomRemoteTokenServices customRemoteTokenServices(RestTemplate lbRestTemplate,
															   SecurityProperties securityProperties,
															   ResourceServerProperties resource) {
		CustomRemoteTokenServices customRemoteTokenServices = new CustomRemoteTokenServices();
		customRemoteTokenServices.setLbRestTemplate(lbRestTemplate);
		customRemoteTokenServices.setCheckTokenUrlInfos(securityProperties.getTokenInfoUrls());
		customRemoteTokenServices.setResource(resource);
		return customRemoteTokenServices;
	}


}
