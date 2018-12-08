package zyxhj.economy.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表决（投票）
 *
 */
@RDSAnnEntity(alias = "tb_ecm_vote")
public class Vote {

	public static final Byte TYPE_SINGLE = 0;
	public static final Byte TYPE_MULTIPLE = 1;

	public static final Byte OPT_ABSTAINED = 0;
	public static final Byte OPT_AGREE = 1;
	public static final Byte OPT_DISAGREE = 2;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 组织编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orgId;

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
	 * 类型</br>
	 * 单选，多选
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 最多选择的数量，前端限制
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte choiceCount;

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

}
