package zyxhj.economy.repository;

import zyxhj.economy.domain.VoteTicket;
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

}
