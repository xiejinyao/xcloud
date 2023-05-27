package com.xjinyao.xcloud.report.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 谢进伟
 * @createDate 2023/2/28 10:15
 */
@Data
@ConfigurationProperties(prefix = "report.provider.jdbc")
public class JdbcReportProviderProperties {

	private Boolean disabled = Boolean.FALSE;

	private String name = "TCS报表库";

	private String prefix = "jdbc:";
}
