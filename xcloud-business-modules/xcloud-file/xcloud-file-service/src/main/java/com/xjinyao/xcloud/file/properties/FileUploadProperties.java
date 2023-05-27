package com.xjinyao.xcloud.file.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author 谢进伟
 * @createDate 2023/2/27 12:29
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

	@NestedConfigurationProperty
	private LocalStoreProperties localStore;
}
