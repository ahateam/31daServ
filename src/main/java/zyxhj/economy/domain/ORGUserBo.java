package zyxhj.economy.domain;

import java.util.Date;

public class ORGUserBo {

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

	// 组织信息
	public Long orgId;
	public Byte share;
	public Integer shareAmount;
	public Integer weight;
	public Byte duty;
	public Byte visor;
}
