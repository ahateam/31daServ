package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import zyxhj.core.domain.Valid;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;

public class TestController extends Controller {

	private static Logger log = LoggerFactory.getLogger(TestController.class);

	public TestController(String node) {
		super(node);
	}

	@POSTAPI(//
			path = "newapi", //
			des = "新接口，普通参数测试"//
	)
	public void newapi(String some, Integer i2, Integer i3) throws Exception {
		System.out.println("newapi called>>" + some);
		System.out.println("newapi called>>" + i2);
		System.out.println("newapi called>>" + i3);
	}

	@POSTAPI(//
			path = "newapi2", //
			des = "新接口，对象参数测试"//
	)
	public void newapi2(JSONObject jo, Valid v, JSONArray ja) throws Exception {
		System.out.println("newapi2 called>>" + JSON.toJSONString(jo));
		System.out.println("newapi2 called>>" + JSON.toJSONString(v));
		System.out.println("newapi2 called>>" + JSON.toJSONString(ja));
	}

	@GET(//
			path = "get", //
			des = "GET方法测试"//
	)
	public void get(RoutingContext context, HttpServerRequest req, HttpServerResponse resp) throws Exception {

		System.out.println(" test get in");

		writeThings(resp, "test get ok");

		System.out.println(" test get out");
	}

	@POST(//
			path = "post", //
			des = "POST方法测试"//
	)
	public void post(RoutingContext context, HttpServerRequest req, HttpServerResponse resp) throws Exception {
		System.out.println(" test post in");

		writeThings(resp, "test post ok");

		System.out.println(" test post out");
	}

	/**
	 * 测试代码
	 * http://localhost:8080/start/test/getapi?req=%7b%22id%22%3a%22123%22%2c%22c%22%3a%22%7b%5c%22appId%5c%22%3a%5c%221%5c%22%2c%5c%22userId%5c%22%3a%5c%2223%5c%22%7d%22%2c%22v%22%3a%22v%22%7d
	 */
	@GETAPI(//
			path = "getapi", //
			des = "GETAPI测试"//
	)
	public APIResponse getapi(//
			@P(t = "应用编号") Long appId, //
			@P(t = "用户编号") Long userId//
	) throws Exception {
		System.out.println(" test getapi in");

		System.out.println(" test getapi out");
		return APIResponse.getNewSuccessResp("success1");
	}

	@POSTAPI(//
			path = "postapi", //
			des = "POSTAPI测试"//
	)
	public APIResponse postapi(//
			@P(t = "应用编号") Long appId, //
			@P(t = "用户编号") Long userId//
	) throws Exception {
		System.out.println(" test postapi in");

		System.out.println(" test postapi out");
		return APIResponse.getNewSuccessResp("success1");
	}

}
