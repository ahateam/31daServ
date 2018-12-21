package zyxhj.economy.repository;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.VoteOption;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class VoteOptionRepository extends RDSRepository<VoteOption> {

	private static VoteOptionRepository ins;

	public static synchronized VoteOptionRepository getInstance() {
		if (null == ins) {
			ins = new VoteOptionRepository();
		}
		return ins;
	}

	private VoteOptionRepository() {
		super(VoteOption.class);
	}

	public int countTicket(DruidPooledConnection conn, String[] ids, Integer weight) throws ServerException {
		return updateKeyInValues(conn, "id", ids,
				StringUtils.join("SET ballot_count=ballot_count+1 , weight=weight+", weight), ids);
	}
}
