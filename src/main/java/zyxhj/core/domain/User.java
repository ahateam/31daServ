package zyxhj.core.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.rds.RDSAnnIndex;

@RDSAnnEntity(alias = "tb_user")
public class User {

	public static final Byte STATUS_PENDING = -1; // 待审核
	public static final Byte STATUS_NORMAL = 0; // 正常
	public static final Byte STATUS_STOPED = 1; // 停止使用
	public static final Byte STATUS_DELETED = 2; // 删除

	/**
	 * 用户编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 用户名（索引）
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 身份证号
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(32)")
	public String idNumber;

	/**
	 * 手机号（索引）
	 */
	@RDSAnnIndex(type = RDSAnnIndex.NORMAL)
	@RDSAnnField(column = "VARCHAR(16)")
	public String mobile;

	/**
	 * 邮箱（索引）
	 */
	@RDSAnnIndex(type = RDSAnnIndex.NORMAL)
	@RDSAnnField(column = "VARCHAR(64)")
	public String email;

	/**
	 * QQ开放平台id(索引)
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(32)")
	public String qqOpenId;

	/**
	 * 微信开放平台id(索引)
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(32)")
	public String wxOpenId;

	/**
	 * 用户真名
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String realName;

	/**
	 * 昵称
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String nickname;

	/**
	 * 个性签名
	 */
	@RDSAnnField(column = "VARCHAR(128)")
	public String signature;

	/**
	 * 密码
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String pwd;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createDate;
	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 用户角色列表</br>
	 * 静态JSON数组格式存储，不使用SQL的JSON格式
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String roles;

	/**
	 * 牛逼的JSON</br>
	 * 
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;

	/**
	 * 牛逼的JSON</br>
	 * 
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String ext;

}
