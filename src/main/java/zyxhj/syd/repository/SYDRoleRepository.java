package zyxhj.syd.repository;

import zyxhj.syd.domain.SYDRole;
import zyxhj.utils.data.rds.RDSRepository;

public class SYDRoleRepository extends RDSRepository<SYDRole> {

	private static SYDRoleRepository ins;

	public static synchronized SYDRoleRepository getInstance() {
		if (null == ins) {
			ins = new SYDRoleRepository();
		}
		return ins;
	}

	private SYDRoleRepository() {
		super(SYDRole.class);
	}

}
