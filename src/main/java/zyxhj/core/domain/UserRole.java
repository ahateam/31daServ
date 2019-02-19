package zyxhj.core.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 角色表
 *
 */
@RDSAnnEntity(alias = "tb_user_role")
public class UserRole {

	/**
	 * 角色编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 角色名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;
}
