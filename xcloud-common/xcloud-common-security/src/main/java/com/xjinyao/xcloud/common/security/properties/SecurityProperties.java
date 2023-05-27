package com.xjinyao.xcloud.common.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

import static com.xjinyao.xcloud.common.security.component.CustomUserAuthenticationConverter.DEFAULT_TOKEN_CONVERTER_TYPE;

/**
 * @author 谢进伟
 * @createDate 2023/2/22 09:04
 */
@Data
@ConfigurationProperties(prefix = "security.oauth2.resource")
public class SecurityProperties {

	/**
	 * 令牌信息uris
	 */
	private List<CheckTokenUrlInfo> tokenInfoUrls;

	@Data
	public static class CheckTokenUrlInfo {

		/**
		 * url
		 */
		private String url;

		/**
		 * 客户机id
		 */
		private String clientId;

		/**
		 * 客户秘密
		 */
		private String clientSecret;

		/**
		 * 是否在相同注册中心
		 */
		private Boolean inTheSameRegistryCenter = true;

		/**
		 * 令牌名称
		 */
		private String tokenName = "token";

		/**
		 * 令牌转换器类型
		 */
		private String tokenConverterTypes;

		/**
		 * 额外参数
		 */
		private Map<String, String> additionalParameters;

		public CheckTokenUrlInfo() {
			this.setTokenConverterTypes(DEFAULT_TOKEN_CONVERTER_TYPE);
		}
	}
}
