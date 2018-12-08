package zyxhj.economy.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表决（投票）
 *
 */
@RDSAnnEntity(alias = "tb_ecm_vote_option")
public class VoteOption {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.ID)
	public Long voteId;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

	/**
	 * 赞成计数器
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer agreeCounter;

	/**
	 * 反对计数器
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer disagreeCounter;

	/**
	 * 弃权计数器
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer abstainedCounter;
	/**
	 * 赞成权重
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer agreeWeight;

	/**
	 * 反对权重
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer disagreeWeight;

	/**
	 * 弃权权重
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer abstainedWeight;

}
