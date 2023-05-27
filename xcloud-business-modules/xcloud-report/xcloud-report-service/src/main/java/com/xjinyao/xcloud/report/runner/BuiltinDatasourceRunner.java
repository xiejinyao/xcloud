package com.xjinyao.xcloud.report.runner;

import com.xjinyao.xcloud.report.datasource.DefaultBuiltinDatasource;
import com.xjinyao.xcloud.report.properties.BuiltinDatasourceProperties;
import com.xjinyao.report.core.Utils;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据源注册
 *
 * @author 谢进伟
 * @createDate 2023/3/2 16:01
 */
@Component
@AllArgsConstructor
public class BuiltinDatasourceRunner implements ApplicationRunner {

	private final BuiltinDatasourceProperties properties;

	@Override
	public void run(ApplicationArguments args) {
		List<BuiltinDatasourceProperties.BuiltinDatasourceInfo> datasourceList = properties.getDatasourceInfos();
		if (CollectionUtils.isNotEmpty(datasourceList)) {
			datasourceList.forEach(datasourceInfo -> {
				String name = datasourceInfo.getName();
				DataSourceProperties datasourceProperties = datasourceInfo.getProperties();
				Utils.getBuildinDatasources().add(new DefaultBuiltinDatasource(name, datasourceProperties));
			});
		}
	}
}
