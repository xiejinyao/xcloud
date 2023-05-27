package com.xjinyao.xcloud.report.config;

import com.xjinyao.xcloud.report.properties.BuiltinDatasourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author 谢进伟
 * @createDate 2023/2/23 10:28
 */
@Configuration
@EnableConfigurationProperties({
		BuiltinDatasourceProperties.class
})
@ImportResource("classpath*:ureport-console-context.xml")
public class XReportConfig {

}
