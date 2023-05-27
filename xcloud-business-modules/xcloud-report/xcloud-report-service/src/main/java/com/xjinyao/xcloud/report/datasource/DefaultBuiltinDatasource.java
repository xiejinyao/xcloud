package com.xjinyao.xcloud.report.datasource;

import com.xjinyao.report.core.definition.datasource.BuiltinDatasource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 默认内置数据源
 *
 * @author 谢进伟
 * @createDate 2023/3/2 16:17
 */
public class DefaultBuiltinDatasource implements BuiltinDatasource {

	private String name;

	private DataSource dataSource;


	private DataSourceProperties properties;

	public DefaultBuiltinDatasource(String name, DataSourceProperties properties) {
		this.name = name;
		this.properties = properties;
		this.dataSource = this.properties.initializeDataSourceBuilder().build();
	}

	/**
	 * @return 返回数据源名称
	 */
	@Override
	public String name() {
		return this.name;
	}

	@Override
	public DataSourceProperties getProperties() {
		return properties;
	}

	/**
	 * @return 返回当前采用数据源的一个连接
	 */
	@Override
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
