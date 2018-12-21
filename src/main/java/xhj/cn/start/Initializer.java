package xhj.cn.start;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.Tag;
import zyxhj.core.domain.TagGroup;
import zyxhj.core.domain.User;
import zyxhj.economy.domain.ORG;
import zyxhj.economy.domain.ORGRole;
import zyxhj.economy.domain.Vote;
import zyxhj.economy.domain.VoteOption;
import zyxhj.economy.domain.VoteProject;
import zyxhj.economy.domain.VoteTicket;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;
import zyxhj.utils.data.rds.RDSUtils;

public class Initializer {

	private static DruidPooledConnection conn;

	static {
		DataSourceUtils.initDataSourceConfig();

		try {
			conn = (DruidPooledConnection) DataSourceUtils.getDataSource("rdsDefault").openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println("Initializer");

		DataSource dsRds = null;
		DruidPooledConnection conn = null;
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			conn = (DruidPooledConnection) dsRds.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		initDB(dsRds);

		initData(conn);
	}

	private static void initDB(DataSource dsRds) {

		RDSUtils.createTableByEntity(dsRds, Tag.class);
		RDSUtils.createTableByEntity(dsRds, TagGroup.class);
		RDSUtils.createTableByEntity(dsRds, User.class);

		RDSUtils.createTableByEntity(dsRds, ORG.class);
		RDSUtils.createTableByEntity(dsRds, ORGRole.class);
		RDSUtils.createTableByEntity(dsRds, VoteProject.class);
		RDSUtils.createTableByEntity(dsRds, Vote.class);
		RDSUtils.createTableByEntity(dsRds, VoteOption.class);
		RDSUtils.createTableByEntity(dsRds, VoteTicket.class);
		
	}

	private static void initData(DruidPooledConnection conn) {

		initUserRole(conn);

		initTag(conn);
	}

	private static void initUserRole(DruidPooledConnection conn) {
	}

	private static void initTag(DruidPooledConnection conn) {

	}
}
