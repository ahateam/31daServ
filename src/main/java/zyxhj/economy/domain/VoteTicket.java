package zyxhj.economy.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 选票
 *
 */
@RDSAnnEntity(alias = "tb_ecm_vote_ticket")
public class VoteTicket {

	/**
	 * 表决（投票）编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long voteId;

	/**
	 * 用户编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date voteTime;

	/**
	 * 选择（单选，多选）</br>
	 * option编号列表</br>
	 * 静态JSON数组格式存储，不使用SQL的JSON格式
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String selection;

	/**
	 * 用户的权重
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer weight;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

}
