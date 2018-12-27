package zyxhj.custom.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.card.WxMpCardQrcodeCreateResult;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import me.chanjar.weixin.mp.bean.store.WxMpStoreInfo;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.api.Controller;

public class HttpTestController extends Controller {

	private static Logger log = LoggerFactory.getLogger(HttpTestController.class);

	private static HttpTestController ins;

	public static synchronized HttpTestController getInstance(String node) {
		if (null == ins) {
			ins = new HttpTestController(node);
		}
		return ins;
	}

	private WxMpService wxMpService;
	private WxMpInMemoryConfigStorage wxMpConfigStorage;

	private HttpTestController(String node) {
		super(node);
		try {
			// 微信参数配置
			wxMpConfigStorage = new WxMpInMemoryConfigStorage();
			wxMpConfigStorage.setAppId("wx9aaf23b05328a771"); // APPid
			wxMpConfigStorage.setSecret("6b72b49c33db086d6f62931f94e9ee1b"); // AppSecret
			wxMpConfigStorage.setToken("wx3ch"); // 设置微信公众号的token
			wxMpConfigStorage.setAesKey("6tLn50b5o97PhgdiVb5Ek0780VLx6yG97eiKTE9waxZ"); // 设置微信公众号的EncodingAESKey
			wxMpService = new WxMpServiceImpl();
			wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * 获取用户列表（test）
	 */
	@GET(path = "getUserList", //
			des = "获取用户列表（test）"//
	)
	public void getUserList(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
		WxMpUserList wxUserList = wxMpService.getUserService().userList(null);
		ret(resp, wxUserList.toString());
	}

	/**
	 */
	@GET(path = "getTest3", //
			des = "创建二维码"//
	)
	public void getTest3(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
			throws WxErrorException {
		WxMpCardQrcodeCreateResult card = wxMpService.getCardService().createQrcodeCard("pu_IQ1WJyDNTTcnd6Z53H3eMFNvI",
				"123");
		ret(resp, card.toString());
	}

	/**
	 */
	@GET(path = "getTest4", //
			des = "GET测试4"//
	)
	public void getTest4(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
			throws WxErrorException {
		String detail = wxMpService.getCardService().getCardDetail("pu_IQ1WJyDNTTcnd6Z53H3eMFNvI");
		ret(resp, detail);
	}

	/**
	 */
	@GET(path = "getTest5", //
			des = "GET测试5"//
	)
	public void getTest5(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
			throws WxErrorException {
		List<WxMpStoreInfo> list = wxMpService.getStoreService().listAll();
		WxMpStoreInfo store = list.get(0);
		ret(resp, store.getBaseInfo().toString());
	}

	// 回复编码
	private void ret(HttpServerResponse resp, String str) {
		int len = str.getBytes(CodecUtils.CHARSET_UTF8).length;
		resp.putHeader("content-type", "text/html;charset=utf-8");
		resp.putHeader("content-length", Integer.toString(len));
		resp.write(str);
	}

}
