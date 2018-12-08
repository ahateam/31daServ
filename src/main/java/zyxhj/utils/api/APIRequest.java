package zyxhj.utils.api;

/**
 */
public class APIRequest {

	/**
	 * ClientId，客户端编号<br/>
	 * 用来唯一识别客户端请求
	 */
	public String id;

	/**
	 * 内容（json字符串）
	 */
	public String c;

	/**
	 * 签名<br>
	 * 按id->cn的顺序连接字符串，然后再使用HmacSHA1(verifyByHmacSHA1ToID64)和密钥进行签名得到v
	 */
	public String v;

}
