package zyxhj.org.cn.core.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;
import zyxhj.org.cn.utils.data.rds.RDSAnnIndex;

@RDSAnnEntity(alias = "tb_user")
public class User {

	/**
	 * 匿名用户
	 */
	public static final Byte LEVEL_ANONYMOUS = 0;
	/**
	 * 基础用户（只是注册，没有付费等行为）
	 */
	public static final Byte LEVEL_BASIC = 1;
	/**
	 * 会员(1个月)
	 */
	public static final Byte LEVEL_MEMBER = 2;

	/**
	 * 高级会员（3个月）
	 */
	public static final Byte LEVEL_MEMBER1 = 3;

	/**
	 * 顶级会员（1年）
	 */
	public static final Byte LEVEL_MEMBER2 = 4;

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
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createDate;

	/**
	 * 用户等级</br>
	 * 目前只分基本用户（BASIC）和会员用户（MEMBER）两种
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte level;

	/**
	 * 用户类型</br>
	 * 预留给应用区分用户类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 用户名（索引）
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(32)")
	public String name;

	/**
	 * 手机号（索引）
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(16)")
	public String mobile;

	/**
	 * 邮箱（索引）
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(32)")
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
	 * 微博开放平台id(索引)
	 */
	@RDSAnnIndex(type = RDSAnnIndex.UNIQUE)
	@RDSAnnField(column = "VARCHAR(32)")
	public String wbOpenId;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 密码
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String pwd;

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
	 * 牛逼的JSON</br>
	 * 
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;

}
