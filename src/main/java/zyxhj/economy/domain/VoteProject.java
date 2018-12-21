package zyxhj.economy.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_ecm_vote_project")
public class VoteProject {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 所有者编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orgId;

	/**
	 * 创建者编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long creatorId;

	/**
	 * 是否可用
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean isActive;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 开始时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date startTime;

	/**
	 * 终止时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date expiryTime;

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
	 * 扩展信息（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String ext;

	/**
	 * 结果（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String outcome;

}
