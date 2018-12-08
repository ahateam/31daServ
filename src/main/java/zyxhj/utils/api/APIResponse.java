package zyxhj.utils.api;

import com.alibaba.fastjson.JSON;

/**
 */
public class APIResponse {

	/**
	 * 内容（json字符串）
	 */
	public String c;

	/**
	 * 返回值编码(ResponseCode)
	 */
	public String rc;

	/**
	 * 返回值编码(ResponseMessage)
	 */
	public String rm;

	public static APIResponse getNewFailureResp(RC rc) {
		APIResponse resp = new APIResponse();
		resp.rc = rc.code;
		resp.rm = rc.msg;
		return resp;
	}

	public static APIResponse getNewFailureResp(RC rc, String content) {
		APIResponse resp = new APIResponse();
		resp.rc = rc.code;
		resp.rm = rc.msg;
		resp.c = content;
		return resp;
	}

	public static APIResponse getNewSuccessResp(Object content) {
		return getNewSuccessResp(JSON.toJSONString(content));
	}

	public static APIResponse getNewSuccessResp(String content) {
		APIResponse resp = new APIResponse();
		resp.rc = BaseRC.SUCCESS.code;
		// success消息的msg不带，不关心
		resp.c = content;
		return resp;
	}
	
	public static APIResponse getNewSuccessResp() {
		APIResponse resp = new APIResponse();
		resp.rc = BaseRC.SUCCESS.code;
		return resp;
	}
}
