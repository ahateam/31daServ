package zyxhj.economy.repository;

import zyxhj.economy.domain.ORG;
import zyxhj.utils.data.rds.RDSRepository;

public class ORGRepository extends RDSRepository<ORG> {

	private static ORGRepository ins;

	public static synchronized ORGRepository getInstance() {
		if (null == ins) {
			ins = new ORGRepository();
		}
		return ins;
	}

	private ORGRepository() {
		super(ORG.class);
	}

}
