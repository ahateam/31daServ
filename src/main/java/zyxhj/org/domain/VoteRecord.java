package zyxhj.org.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 投票记录，每次都会记录下来
 *
 */
@RDSAnnEntity(alias = "tb_org_vote_record")
public class VoteRecord {

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
	 * 投票时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date voteTime;

	/**
	 * 用户的选票数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer ballotCount;

	/**
	 * 选择（单选，多选）</br>
	 * option编号列表</br>
	 * 静态JSON数组格式存储，不使用SQL的JSON格式
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String selection;

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

}
