package zyxhj.syd.repository;

import zyxhj.syd.domain.SYDStructure;
import zyxhj.utils.data.rds.RDSRepository;

public class SYDStructureRepository extends RDSRepository<SYDStructure> {

	private static SYDStructureRepository ins;

	public static synchronized SYDStructureRepository getInstance() {
		if (null == ins) {
			ins = new SYDStructureRepository();
		}
		return ins;
	}

	private SYDStructureRepository() {
		super(SYDStructure.class);
	}

}
