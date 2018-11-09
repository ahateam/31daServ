package xhj.cn.start;


import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.org.cn.cms.domain.Content;
import zyxhj.org.cn.utils.data.DataSource;
import zyxhj.org.cn.utils.data.DataSourceUtils;
import zyxhj.org.cn.utils.data.rds.RDSUtils;

public class Test {

	private static DruidPooledConnection conn;

	static {
		DataSourceUtils.initDataSourceConfig();
		// contentService = ContentService.getInstance();

		try {
			conn = (DruidPooledConnection) DataSourceUtils.getDataSource("rdsDefault").openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		testDB();

	}

	private static void testDB() {
		System.out.println("testDB");
		try {
			DataSource dsRds = DataSourceUtils.getDataSource("rdsDefault");

			// RDSUtils.dropTableByEntity(dsRds, Tunnel.class);

			RDSUtils.createTableByEntity(dsRds, Content.class);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
