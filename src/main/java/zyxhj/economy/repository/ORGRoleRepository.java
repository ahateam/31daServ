package zyxhj.economy.repository;

import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.ORGRole;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ORGRoleRepository extends RDSRepository<ORGRole> {

	private static ORGRoleRepository ins;

	public static synchronized ORGRoleRepository getInstance() {
		if (null == ins) {
			ins = new ORGRoleRepository();
		}
		return ins;
	}

	private ORGRoleRepository() {
		super(ORGRole.class);
	}

	public List<ORGRole> getDirectors(DruidPooledConnection conn, Long orgId, int count, int offset)
			throws ServerException {
		return getList(conn, "WHERE org_id=? AND duty<>?", new Object[] { orgId, ORGRole.DUTY_NONE }, count, offset);
	}

	public List<ORGRole> getShareholders(DruidPooledConnection conn, Long orgId, int count, int offset)
			throws ServerException {
		return getList(conn, "WHERE org_id=? AND share<>?", new Object[] { orgId, ORGRole.SHARE_NONE }, count, offset);
	}

	public List<ORGRole> getSuperVisors(DruidPooledConnection conn, Long orgId, int count, int offset)
			throws ServerException {
		return getList(conn, "WHERE org_id=? AND visor<>?", new Object[] { orgId, ORGRole.VISOR_NONE }, count, offset);
	}
}
