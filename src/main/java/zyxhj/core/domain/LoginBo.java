package zyxhj.core.domain;

import java.util.Date;

public class LoginBo {

	// 用户信息
	public Long id;
	public String name;
	public String realName;
	public String nickname;
	public String signature;

	public String idNumber;
	public String mobile;
	public String email;
	public String qqOpenId;
	public String wxOpenId;
	public String wbOpenId;

	public String roles;

	// Session信息
	public Date loginTime;
	public String loginToken;

}
