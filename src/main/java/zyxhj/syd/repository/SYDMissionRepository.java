package zyxhj.syd.repository;

import zyxhj.syd.domain.SYDMission;
import zyxhj.utils.data.rds.RDSRepository;

public class SYDMissionRepository extends RDSRepository<SYDMission> {

	private static SYDMissionRepository ins;

	public static synchronized SYDMissionRepository getInstance() {
		if (null == ins) {
			ins = new SYDMissionRepository();
		}
		return ins;
	}

	private SYDMissionRepository() {
		super(SYDMission.class);
	}

}
