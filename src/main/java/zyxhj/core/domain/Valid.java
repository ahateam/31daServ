package zyxhj.core.domain;

public class Valid {

	public Long id;

	/**
	 * 有效时间，单位，分钟
	 */
	public Integer expire;

	/**
	 * 验证码，一般是4位或6位随机数
	 */
	public String code;

}
