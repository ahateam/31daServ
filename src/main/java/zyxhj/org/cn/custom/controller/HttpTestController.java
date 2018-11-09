package zyxhj.org.cn.custom.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.card.WxMpCardQrcodeCreateResult;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import me.chanjar.weixin.mp.bean.store.WxMpStoreInfo;
import me.chanjar.weixin.mp.bean.store.WxMpStoreListResult;
import zyxhj.org.cn.custom.util.HttpClientUtil;
import zyxhj.org.cn.utils.CodecUtils;
import zyxhj.org.cn.utils.api.Controller;

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
	@GET(path = "getTest")
	public void getTest(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
		WxMpUserList wxUserList = wxMpService.getUserService().userList(null);
		ret(resp, wxUserList.toString());

//		WxMenu wxMenu = new WxMenu();
//		WxMenuButton button = new WxMenuButton();
//		button.setType("view");
//		button.setName("test");
//		button.setUrl("http://aha-element.oss-cn-hangzhou.aliyuncs.com/index.html");
//		List<WxMenuButton> list = new ArrayList<WxMenuButton>();
//		list.add(button);
//		wxMenu.setButtons(list);
//		// 设置菜单
//		wxMpService.getMenuService().menuCreate(wxMenu);

	}

	/**
	 * @描述 获取卡券列表
	 * @param req
	 * @param resp
	 * @param context
	 * @throws Exception
	 */
	@GET(path = "getTest2")
	public void getTest2(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		list.add("CARD_STATUS_VERIFY_OK");
		map.put("offset", 0);
		map.put("count", 10);
		map.put("status_list", list);
		String json = JSONObject.toJSONString(map);
		String reJson = HttpClientUtil
				.post("https://api.weixin.qq.com/card/batchget?access_token=" + wxMpService.getAccessToken(), json);
		System.err.println("accessToken:  " + wxMpService.getAccessToken());
		Map<String, Object> json2Map = HttpClientUtil.parseJSON2Map(reJson);
		System.err.println(json2Map);
		ret(resp, reJson);
	}

	/**
	 * @描述 获取卡卷二维码（test）
	 * @param req
	 * @param resp
	 * @param context
	 * @throws WxErrorException
	 */
	@GET(path = "getTest3")
	public void getTest3(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
			throws WxErrorException {
		WxMpCardQrcodeCreateResult card = wxMpService.getCardService().createQrcodeCard("pu_IQ1WJyDNTTcnd6Z53H3eMFNvI",
				"123");
		ret(resp, card.toString());
	}

	/**
	 * @描述 查看卡卷详情（test）
	 * @param req
	 * @param resp
	 * @param context
	 * @throws WxErrorException
	 */
	@GET(path = "getTest4")
	public void getTest4(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
			throws WxErrorException {
		String detail = wxMpService.getCardService().getCardDetail("pu_IQ1WJyDNTTcnd6Z53H3eMFNvI");
		ret(resp, detail);
	}

	/**
	 * @描述 门店列表（test5）
	 * @param req
	 * @param resp
	 * @param context
	 * @throws WxErrorException
	 */
	@GET(path = "getTest5")
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
