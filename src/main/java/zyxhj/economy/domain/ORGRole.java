package zyxhj.economy.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 组织董事表
 *
 */
@RDSAnnEntity(alias = "tb_ecm_org_role")
public class ORGRole {

	public static final Byte SHARE_NONE = 20;// 非股东
	public static final Byte SHARE_SHAREHOLDER = 21;// 股东
	public static final Byte SHARE_REPRESENTATIVE = 22;// 股东代表

	public static final Byte DUTY_NONE = 10;// 非董事
	public static final Byte DUTY_DIRECTOR = 11;// 董事
	public static final Byte DUTY_CHAIRMAN = 12;// 董事长（主席）
	public static final Byte DUTY_VICE_CHAIRMAN = 13;// 副董事长

	public static final Byte VISOR_NONE = 10;// 非监事
	public static final Byte VISOR_SUPERVISOR = 11;// 监事
	public static final Byte VISOR_CHAIRMAN = 12;// 监事长（主席）
	public static final Byte VISOR_VICE_SUPERVISOR = 13;// 副监事长

	/**
	 * 组织编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orgId;

	/**
	 * 用户编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 股份类型（NONE，董事，副董事长，董事长）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte share;

	/**
	 * 股份数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer shareAmount;

	/**
	 * 投票权重
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer weight;

	/**
	 * 董事会职务类型（NONE，董事，副董事长，董事长）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte duty;

	/**
	 * 监事会职务类型（NONE，监事，副监事长，监事长）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte visor;
}
