package com.xjinyao.xcloud.report.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * 数据源配置
 *
 * @author 谢进伟
 * @createDate 2023/3/2 15:51
 */
@Data
@ConfigurationProperties(prefix = "report.datasource.builtin")
public class BuiltinDatasourceProperties {

	private List<BuiltinDatasourceInfo> datasourceInfos;

	public List<BuiltinDatasourceInfo> getDatasourceInfos() {
		return datasourceInfos;
	}

	public void setDatasourceInfos(List<BuiltinDatasourceInfo> datasourceInfos) {
		this.datasourceInfos = datasourceInfos;
	}

	@Data
	public static class BuiltinDatasourceInfo {

		private String name;

		@NestedConfigurationProperty
		private DataSourceProperties properties;
	}

}
