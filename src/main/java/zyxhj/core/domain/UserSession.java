package zyxhj.core.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.ots.OTSAnnEntity;
import zyxhj.org.cn.utils.data.ots.OTSAnnField;
import zyxhj.org.cn.utils.data.ots.OTSAnnID;

/**
 * 用户Session，使用OTS存储</br>
 * 缓存有效期30分钟，OTS存储有效期2天
 */
@OTSAnnEntity(alias = "useSession")
public class UserSession {

	/**
	 * 用户编号
	 */
	@OTSAnnID(keyType = OTSAnnID.KeyType.PRIMARY_KEY_1)
	@OTSAnnField(column = OTSAnnField.ColumnType.INTEGER)
	public Long userId;

	/**
	 * 用户等级
	 */
	@OTSAnnField(column = OTSAnnField.ColumnType.INTEGER)
	public Byte level;

	/**
	 * 登录时间
	 */
	@OTSAnnField(column = OTSAnnField.ColumnType.INTEGER)
	public Date loginTime;

	/**
	 * 登录令牌
	 */
	@OTSAnnField(column = OTSAnnField.ColumnType.STRING)
	public String loginToken;

}
