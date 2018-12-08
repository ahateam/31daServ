package zyxhj.custom.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import zyxhj.custom.service.WxDataService;
import zyxhj.custom.service.WxFuncService;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;

public class WxOAuth2Controller extends Controller {

	private static Logger log = LoggerFactory.getLogger(WxOAuth2Controller.class);

	private static WxOAuth2Controller ins;

	public static synchronized WxOAuth2Controller getInstance(String node) {
		if (null == ins) {
			ins = new WxOAuth2Controller(node);
		}
		return ins;
	}

	private WxDataService wxDataService;
	// private WxMpMessageRouter wxMpMessageRouter;
	private WxFuncService wxFuncService;

	private WxOAuth2Controller(String node) {
		super(node);
		try {
			wxDataService = WxDataService.getInstance();
			wxFuncService = WxFuncService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/*
	 * 添加子菜单test
	 */
	@GET(path = "addMenu")
	public void getTest(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
//		WxMpUserList wxUserList = wxMpService.getUserService().userList(null);
//		ret(resp, wxUserList.toString());
		String url = "http://3ch.org.cn/start/oauth/getAccessToken";
		String url2 = wxFuncService.getUrl(wxDataService.getWxMpService(), url);
		WxMenu wxMenu = new WxMenu();
		WxMenuButton button = new WxMenuButton();
		button.setType("view");
		button.setName("test1");
		button.setUrl(url2);
		List<WxMenuButton> list = new ArrayList<WxMenuButton>();
		list.add(button);
		wxMenu.setButtons(list);
		// 设置菜单
		wxDataService.getWxMpService().getMenuService().menuCreate(wxMenu);

	}

	/*
	 * 消息群发
	 */
	@GET(path = "messageToMany")
	public void messageToMany(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
		WxMpUserList userList = wxFuncService.getTest(wxDataService.getWxMpService());
		wxFuncService.messageToMany(wxDataService.getWxMpService(), userList.getOpenids());
	}

	/*
	 * 模板消息发送测试
	 */
	@GET(path = "templateMessage")
	public void templateMessage(HttpServerRequest req, HttpServerResponse resp, RoutingContext context)
			throws Exception {
		wxFuncService.templateMessageTest(wxDataService.getWxMpService());
	}

	// 回复编码
	private void ret(HttpServerResponse resp, String str) {
		int len = str.getBytes(CodecUtils.CHARSET_UTF8).length;
		resp.putHeader("content-type", "text/html;charset=utf-8");
		resp.putHeader("content-length", Integer.toString(len));
		resp.write(str);
	}

	/*
	 * 根据用户反馈授权获取对应token
	 */
	@GET(path = "getAccessToken")
	public void getAccessToken(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
		System.err.println("test：    get assesstoken");
		String code = req.getParam("code");
		System.err.println("code：    "+code);
		WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxDataService.getWxMpService().oauth2getAccessToken(code);
		WxMpUser wxMpUser = wxDataService.getWxMpService().oauth2getUserInfo(wxMpOAuth2AccessToken, null);
		System.err.println(wxMpUser.getOpenId());
	}
	
	/*
	 * 获取二维码
	 */
	@GET(path = "getTicket")
	public APIResponse getTicket(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) throws Exception {
		File ticket = wxFuncService.getTicket(wxDataService.getWxMpService(), "123456");
		return APIResponse.getNewSuccessResp(ticket);
	}

}
