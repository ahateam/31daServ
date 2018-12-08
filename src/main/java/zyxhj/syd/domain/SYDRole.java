package zyxhj.syd.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 门店角色
 */
@RDSAnnEntity(alias = "tb_syd_role")
public class SYDRole {

	public static final Byte ID_MANAGER = 0;// 团体管理员
	public static final Byte ID_ASSISTANT = 1;// 团体助理
	public static final Byte ID_PRAMOTER = 2;// 团体推广员

	public static final Byte CLIENT_VOLUNTEER = 3;// 团体志愿者
	public static final Byte CLIENT_MEMBER = 4;// 团体会员

	/**
	 * 门店编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long missionId;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 身份
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte identity;

}
