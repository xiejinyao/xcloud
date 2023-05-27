package com.xjinyao.xcloud.report;

import com.mysql.cj.jdbc.ConnectionImpl;

import java.sql.*;

/**
 * @author 谢进伟
 * @createDate 2023/3/4 14:51
 */
public class DatasourceTest {


	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		String user = "tq_admin";
		String password = "jkwy@8888";
		String url = "jdbc:mysql://tcs.sys.mysql:6033/tcs_bill?characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Asia/Shanghai";
		Connection connection = DriverManager.getConnection(url, user, password);
		DatabaseMetaData metaData = connection.getMetaData();
		ResultSet rs = metaData.getTables(((ConnectionImpl) connection).getDatabase(), null, "%", new String[]{"TABLE", "VIEW"});
		extracted(rs);

		connection.close();


	}

	private static void extracted(ResultSet rs) throws SQLException {
		ResultSetMetaData resultSetMetaData = rs.getMetaData();
		int columnCount = resultSetMetaData.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnCount; i++) {
				Object object = rs.getObject(i);
				String columnName = resultSetMetaData.getColumnName(i);
				System.out.print(columnName + "=" + object + "\t");
			}
			System.out.println("\n");
		}
		rs.close();

	}


}
