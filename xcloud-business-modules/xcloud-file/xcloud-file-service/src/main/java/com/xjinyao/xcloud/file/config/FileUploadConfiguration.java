package com.xjinyao.xcloud.file.config;

import com.xjinyao.xcloud.file.properties.FileUploadProperties;
import com.xjinyao.xcloud.file.store.LocalStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 谢进伟
 * @description 文件上传配置
 * @createDate 2020/5/15 15:30
 */
@RefreshScope
@Configuration
@EnableConfigurationProperties(FileUploadProperties.class)
public class FileUploadConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "file.upload.local-store", name = "enable", havingValue = "true",
			matchIfMissing = true)
	public LocalStore localStore(FileUploadProperties fileUploadProperties) {
		return new LocalStore(fileUploadProperties.getLocalStore());
	}

}
