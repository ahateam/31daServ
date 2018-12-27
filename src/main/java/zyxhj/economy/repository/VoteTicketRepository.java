package zyxhj.economy.repository;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.VoteTicket;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class VoteTicketRepository extends RDSRepository<VoteTicket> {

	private static VoteTicketRepository ins;

	public static synchronized VoteTicketRepository getInstance() {
		if (null == ins) {
			ins = new VoteTicketRepository();
		}
		return ins;
	}

	private VoteTicketRepository() {
		super(VoteTicket.class);
	}

	public int getTicketCount(DruidPooledConnection conn, Long voteId) throws ServerException {
		return countByKey(conn, "vote_id", voteId);
	}
}
