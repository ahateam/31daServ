package zyxhj.economy.repository;

import zyxhj.economy.domain.VoteProject;
import zyxhj.utils.data.rds.RDSRepository;

public class VoteProjectRepository extends RDSRepository<VoteProject> {

	private static VoteProjectRepository ins;

	public static synchronized VoteProjectRepository getInstance() {
		if (null == ins) {
			ins = new VoteProjectRepository();
		}
		return ins;
	}

	private VoteProjectRepository() {
		super(VoteProject.class);
	}

}
