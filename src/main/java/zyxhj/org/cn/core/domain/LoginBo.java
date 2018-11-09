package zyxhj.org.cn.core.domain;

import java.util.Date;

public class LoginBo {

	// 用户信息
	public Long id;
	public Byte level;
	public String name;
	public String nickname;
	public String signature;

	public String mobile;
	public String email;
	public String qqOpenId;
	public String wxOpenId;
	public String wbOpenId;

	// Session信息
	public Date loginTime;
	public String loginToken;

}
