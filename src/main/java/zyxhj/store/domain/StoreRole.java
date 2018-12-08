package zyxhj.store.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 门店角色
 */
@RDSAnnEntity(alias = "tb_store_role")
public class StoreRole {

	public static final Byte DUTY_MANAGER = 0;// 店长
	public static final Byte DUTY_ASSISTANT = 1;// 店员
	public static final Byte DUTY_PRAMOTER = 2;// 推广员

	public static final Byte CLIENT_MEMBER = 3;// 会员顾客
	public static final Byte CLIENT_CUSTOMER = 3;// 普通顾客

	/**
	 * 门店编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long storeId;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 职务（店长，店员，推广员）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte duty;

	/**
	 * 顾客（会员，普通顾客）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte client;

}
