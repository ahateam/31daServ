package zyxhj.economy.repository;

import zyxhj.economy.domain.Vote;
import zyxhj.utils.data.rds.RDSRepository;

public class VoteRepository extends RDSRepository<Vote> {

	private static VoteRepository ins;

	public static synchronized VoteRepository getInstance() {
		if (null == ins) {
			ins = new VoteRepository();
		}
		return ins;
	}

	private VoteRepository() {
		super(Vote.class);
	}

}
