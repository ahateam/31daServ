package zyxhj.syd.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 慈善团体
 *
 */
@RDSAnnEntity(alias = "tb_syd_structure")
public class SYDStructure {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long missionId;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long childrenMissionId;
}
