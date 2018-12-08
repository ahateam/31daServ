package zyxhj.core.domain;

import java.util.Date;

/**
 * 用户Session，使用OTS存储</br>
 * 缓存有效期30分钟，OTS存储有效期2天
 */
public class UserSession {

	/**
	 * 用户编号
	 */
	public Long userId;

	/**
	 * 登录时间
	 */
	public Date loginTime;

	/**
	 * 登录令牌
	 */
	public String loginToken;

}
