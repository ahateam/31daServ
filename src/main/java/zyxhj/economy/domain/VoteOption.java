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
	 * 是否弃权选项
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean isAbstain;

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
	 * 扩展（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String ext;

	/**
	 * 选票计数器
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer ballotCount;

	/**
	 * 选票权重计数器
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer weight;
}
