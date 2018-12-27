package zyxhj.economy.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.economy.domain.ORGRole;
import zyxhj.economy.domain.Vote;
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
		return getList(conn, "WHERE org_id=? AND duty<>?", new Object[] { orgId, ORGRole.DUTY.NONE.v() }, count,
				offset);
	}

	public List<ORGRole> getShareholders(DruidPooledConnection conn, Long orgId, int count, int offset)
			throws ServerException {
		return getList(conn, "WHERE org_id=? AND share<>?", new Object[] { orgId, ORGRole.SHARE.NONE.v() }, count,
				offset);
	}

	public List<ORGRole> getSuperVisors(DruidPooledConnection conn, Long orgId, int count, int offset)
			throws ServerException {
		return getList(conn, "WHERE org_id=? AND visor<>?", new Object[] { orgId, ORGRole.VISOR.NONE.v() }, count,
				offset);
	}

	/**
	 * 根据权限计算应参加人数
	 */
	public int getParticipateCount(DruidPooledConnection conn, Long orgId, JSONArray crowd) throws ServerException {
		ArrayList<Byte> cs = new ArrayList<>();
		for (int i = 0; i < crowd.size(); i++) {
			cs.add(crowd.getByte(i));
		}

		if (cs.contains(Vote.CROWD.ALL.v())) {
			// 如果包含所有人，则返回组织下全部人数
			return countByKey(conn, "org_id", orgId);
		} else {
			StringBuffer sb = new StringBuffer("WHERE ");

			boolean hasShare = true;
			if (cs.contains(Vote.CROWD.SHAREHOLDER.v()) && cs.contains(Vote.CROWD.REPRESENTATIVE.v())) {
				// 股东和股东代表都参加
				// share 不等于 none
				sb.append("share<>").append(ORGRole.SHARE.NONE.v());
			} else if (cs.contains(Vote.CROWD.SHAREHOLDER.v())) {
				// 股东参加
				sb.append("share=").append(ORGRole.SHARE.SHAREHOLDER.v());
			} else if (cs.contains(Vote.CROWD.REPRESENTATIVE.v())) {
				// 股东代表参加
				sb.append("share=").append(ORGRole.SHARE.REPRESENTATIVE.v());
			} else {
				hasShare = false;
			}

			boolean hasDuty = true;
			if (cs.contains(Vote.CROWD.DIRECTOR.v())) {

				if (hasShare) {
					// 前面被插入过share，需要增加AND连接符
					sb.append(" OR ");
				}

				// 董事会参加
				// duty 不等于 none
				sb.append("duty<>").append(ORGRole.DUTY.NONE.v());
			} else {
				hasDuty = false;
			}

			boolean hasVisor = true;
			if (cs.contains(Vote.CROWD.SUPERVISOR.v())) {

				if (hasShare || hasDuty) {
					// 前面被插入过语句，需要增加AND连接符
					sb.append(" OR ");
				}

				// 监事会参加
				// visor 不等于 none
				sb.append("visor<>").append(ORGRole.VISOR.NONE.v());
			} else {
				hasVisor = false;
			}

			System.out.println(sb.toString());
			return count(conn, sb.toString(), new Object[] {});
		}
	}

	public List<ORGRole> getORGRolesLikeIDNumber(DruidPooledConnection conn, Long orgId, String idNumber, Integer count,
			Integer offset) throws ServerException {
		return this.getList(conn, StringUtils.join("WHERE org_id=? AND id_number LIKE '%", idNumber, "%'"),
				new Object[] { orgId }, count, offset);
	}
}
