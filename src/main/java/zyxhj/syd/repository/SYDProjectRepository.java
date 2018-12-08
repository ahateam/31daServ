package zyxhj.syd.repository;

import zyxhj.syd.domain.SYDProject;
import zyxhj.utils.data.rds.RDSRepository;

public class SYDProjectRepository extends RDSRepository<SYDProject> {

	private static SYDProjectRepository ins;

	public static synchronized SYDProjectRepository getInstance() {
		if (null == ins) {
			ins = new SYDProjectRepository();
		}
		return ins;
	}

	private SYDProjectRepository() {
		super(SYDProject.class);
	}

}
