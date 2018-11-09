package zyxhj.org.cn.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import zyxhj.org.cn.utils.api.APIRequest;
import zyxhj.org.cn.utils.api.APIResponse;
import zyxhj.org.cn.utils.api.Controller;
import zyxhj.org.cn.utils.api.Param;

public class TestController extends Controller {

	private static Logger log = LoggerFactory.getLogger(TestController.class);

	private static TestController ins;

	public static synchronized TestController getInstance(String node) {
		if (null == ins) {
			ins = new TestController(node);
		}
		return ins;
	}

	private TestController(String node) {
		super(node);
	}

	@GET(path = "get")
	public void get(RoutingContext context, HttpServerRequest req, HttpServerResponse resp) throws Exception {

		System.out.println(" test get in");

		writeThings(resp, "test get ok");

		System.out.println(" test get out");
	}

	@POST(path = "post")
	public void post(RoutingContext context, HttpServerRequest req, HttpServerResponse resp) throws Exception {
		System.out.println(" test post in");

		writeThings(resp, "test post ok");

		System.out.println(" test post out");
	}

	/**
	 * 测试代码
	 * http://localhost:8080/start/test/getapi?req=%7b%22id%22%3a%22123%22%2c%22c%22%3a%22%7b%5c%22appId%5c%22%3a%5c%221%5c%22%2c%5c%22userId%5c%22%3a%5c%2223%5c%22%7d%22%2c%22v%22%3a%22v%22%7d
	 */
	@GETAPI(path = "getapi")
	public APIResponse getapi(APIRequest req) throws Exception {
		System.out.println(" test getapi in");
		JSONObject c = Param.getReqContent(req);
		Long appId = Param.getLong(c, "appId");
		Long userId = Param.getLong(c, "userId");

		System.out.println(" test getapi out");
		return APIResponse.getNewSuccessResp("success1");
	}

	@POSTAPI(path = "postapi")
	public APIResponse postapi(APIRequest req) throws Exception {
		System.out.println(" test postapi in");
		JSONObject c = Param.getReqContent(req);
		Long appId = Param.getLong(c, "appId");
		Long userId = Param.getLong(c, "userId");

		System.out.println(" test postapi out");
		return APIResponse.getNewSuccessResp("success1");
	}

}
