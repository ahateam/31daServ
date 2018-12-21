package zyxhj.org.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 参加者
 *
 */
@RDSAnnEntity(alias = "tb_org_vote_party")
public class VoteParty {

	/**
	 * 表决（投票）编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long projectId;

	/**
	 * 用户编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 用户的选票数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer ballotCount;

}
