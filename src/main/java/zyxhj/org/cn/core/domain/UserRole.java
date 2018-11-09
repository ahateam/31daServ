package zyxhj.org.cn.core.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;
import zyxhj.org.cn.utils.data.rds.RDSAnnIndex;

/**
 * 权限模块，角色表</br>
 * TODO 待实现
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
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createDate;

	/**
	 * 角色名称
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String name;
}
