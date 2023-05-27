package com.area;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 谢进伟
 * @createDate 2023/3/31 14:14
 */
public class AreaTest {

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		String url = "jdbc:mysql://192.168.130.215:6033/xcloud_sys?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";
//		String url = "jdbc:mysql://192.168.100.143:6033/xcloud_sys?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true";
		String username = "tq_admin";
		String password = "jkwy@8888";

		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection connection = DriverManager.getConnection(url, username, password);

		PreparedStatement statement1 = connection.prepareStatement("SELECT * FROM sys_area WHERE parent_id !='0' AND parent_id_path IS NULL; ");
		ResultSet resultSet1 = statement1.executeQuery();
		List<A> recordList = new ArrayList<>();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(32, 64,
				30L, TimeUnit.SECONDS,
				new SynchronousQueue<>());

		while (resultSet1.next()) {
			String _id = resultSet1.getString("id");
			int _level = resultSet1.getInt("level");
			recordList.add(new A() {{
				this.setId(_id);
				this.setLevel(_level);
			}});

			if (recordList.size() >= 100) {
				List<A> subList = new ArrayList<>();
				recordList.forEach(d -> subList.add(d.clone()));
				threadPoolExecutor.execute(() -> {
					update(connection, subList);
				});
				recordList.clear();
			}
		}
		if (CollectionUtils.isNotEmpty(recordList)) {
			update(connection, recordList);
		}
		resultSet1.close();
		statement1.close();
		connection.close();

		System.out.println("ojbk");

		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}

	private static void update(Connection connection, List<A> recordList) {
		recordList.forEach(a -> {
			try {
				doUpdate(connection, a.getId(), a.getLevel());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Data
	public static class A {
		String id;
		int level;

		public A clone() {
			String _id = this.id;
			int _level = this.level;
			return new A() {{
				this.setId(_id);
				this.setLevel(_level);
			}};
		}
	}


	private static void doUpdate(Connection connection, String id, int level) throws SQLException {
		PreparedStatement statement2 = connection.prepareStatement("SELECT GROUP_CONCAT(t2.id) parentIds ,GROUP_CONCAT(t2.name) parentNames\n" +
				"FROM\n" +
				"    (\n" +
				"        SELECT \n" +
				"        @r AS _id,\n" +
				"        (SELECT @r := parent_id FROM sys_area WHERE id = _id) AS parent_id,\n" +
				"        @l := @l + 1 AS lvl\n" +
				"        FROM\n" +
				"        (SELECT @r := ?, @l := 0) vars, sys_area AS h\n" +
				"        WHERE @r <> 0 \n" +
				"    ) t1\n" +
				"JOIN sys_area t2\n" +
				"ON t1._id = t2.Id;");
		statement2.setString(1, id);

		ResultSet rs2 = statement2.executeQuery();
		if (rs2.next()) {
			String parentIdStr = rs2.getString("parentIds");
			String parentNamesStr = rs2.getString("parentNames");
			String parentIds = getString(parentIdStr, false);
			String parentNames = getString(parentNamesStr, false);

			PreparedStatement statement3 = connection.prepareStatement("UPDATE sys_area SET parent_id_path =?, parent_name_path=? WHERE id=?");
			statement3.setString(1, parentIds);
			statement3.setString(2, parentNames);
			statement3.setString(3, id);

			int i = statement3.executeUpdate();
			System.out.println(Thread.currentThread().getName() + "\t" + id + "\t" + level + "\t==> " + (i > 0 ? "success" : "error") + "\t" + parentIds);


			if (level == 4) {
				PreparedStatement statement4 = connection.prepareStatement("UPDATE sys_area_level_5 SET parent_id_path =?, parent_name_path=? WHERE parent_id=?");
				String level5ParentIds = getString(parentIdStr, true);
				String level5ParentNames = getString(parentNamesStr, true);
				statement4.setString(1, level5ParentIds);
				statement4.setString(2, level5ParentNames);
				statement4.setString(3, id);
				int j = statement4.executeUpdate();
				System.out.println(Thread.currentThread().getName() + " update level 5\t" + id + "\t==> " + (j > 0 ? "success" : "error") + "\t" + parentIds);
				statement4.close();
			}

			statement3.close();
			rs2.close();
			statement2.close();
		}
	}

	private static String getString(String string, boolean includeLastItem) {
		String[] split = string.split(",");
		List<String> list = Arrays.stream(split).collect(Collectors.toList());
		Collections.reverse(list);
		if (!includeLastItem) {
			list.remove(list.size() - 1);
		}
		return "/" + StringUtils.join(list, "/") + "/";
	}
}
