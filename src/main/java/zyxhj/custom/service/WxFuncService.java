package zyxhj.custom.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMassOpenIdsMessage;
import me.chanjar.weixin.mp.bean.card.WxMpCardQrcodeCreateResult;
import me.chanjar.weixin.mp.bean.result.WxMpMassSendResult;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.result.WxMpUserList;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import zyxhj.custom.util.HttpClientUtil;

public class WxFuncService {
	
	
	private static Logger log = LoggerFactory.getLogger(WxFuncService.class);

	private static WxFuncService ins;

	public static synchronized WxFuncService getInstance() {
		if (null == ins) {
			ins = new WxFuncService();
		}
		return ins;
	}

	private WxFuncService() {
		try {
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	
	/*
	 * 获取用户列表（test）
	 */
	public WxMpUserList getTest(WxMpService wxMpService) throws Exception {
		WxMpUserList wxUserList = wxMpService.getUserService().userList(null);
		return wxUserList;
	}
	
	/*
	 * 获取卡券列表
	 */
	public Map<String, Object> getTest2(WxMpService wxMpService) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		List<String> list = new ArrayList<String>();
		list.add("CARD_STATUS_VERIFY_OK");
		map.put("offset", 0);
		map.put("count", 10);
		map.put("status_list", list);
		String json = JSONObject.toJSONString(map);
		String reJson = HttpClientUtil.post("https://api.weixin.qq.com/card/batchget?access_token="+wxMpService.getAccessToken(), json);
		Map<String, Object> json2Map = HttpClientUtil.parseJSON2Map(reJson);
		return json2Map;
	}
	
	/*
	 * 获取卡卷二维码
	 * 
	 * cardId 卡卷ID
	 * outerStr 场景值
	 * expiresIn 失效时间，单位秒，不填默认365天
	 * 
	 */
	public WxMpCardQrcodeCreateResult getTest3(WxMpService wxMpService,String cardId,String outerStr,int expiresIn) throws WxErrorException {
		WxMpCardQrcodeCreateResult card = wxMpService.getCardService().createQrcodeCard(cardId, outerStr, expiresIn);
		return card;
	}
	
	/*
	 * 获取卡卷详情
	 * 
	 * cardId 卡卷ID
	 */
	public String getTest4(WxMpService wxMpService,String cardId) throws WxErrorException {
		String detail = wxMpService.getCardService().getCardDetail(cardId);
		return detail;
	}
	
	/*
	 * 根据用户反馈授权获取对应信息
	 */
	public WxMpUser getUserMessage(WxMpService wxMpService,String code) throws Exception {
		WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
		WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
		return wxMpUser;
	}
	
	/*
	 * 消息群发
	 */
	public WxMpMassSendResult messageToMany(WxMpService wxMpService, List<String> openIds) throws WxErrorException {
		WxMpMassOpenIdsMessage massMessage = new WxMpMassOpenIdsMessage();
		massMessage.setMsgType(WxConsts.MassMsgType.TEXT);
		massMessage.setContent("牛逼牛逼！！");
		for(String openId : openIds) {
			massMessage.getToUsers().add(openId);
		}
		WxMpMassSendResult massResult = wxMpService.getMassMessageService().massOpenIdsMessageSend(massMessage);
		return massResult;
	}
	
	/*
	 * 模板消息测试（暂未开通，需申请）
	 */
	public void templateMessageTest(WxMpService wxMpService) throws WxErrorException {
		WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
				.toUser("ou_IQ1cNT-BpkpvHHtUa-6c-JivA")
				.templateId("123")
				.url("http://aha-element.oss-cn-hangzhou.aliyuncs.com/index.html")
				.build();
		
		templateMessage.addData(new WxMpTemplateData("test", "Let us test this!!", "blue"));
		
		wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
	}
	
	/*
	 * 网页授权域名获取
	 */
	public String getUrl(WxMpService wxMpService, String url) {
		System.err.println("test：get url");
		String url2 = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
		//ret(resp, url2);
		return url2;
	}
	
	/*
	 * 获取二维码
	 */
	public File getTicket(WxMpService wxMpService, String scene) throws WxErrorException {
		WxMpQrCodeTicket ticket = wxMpService.getQrcodeService().qrCodeCreateLastTicket(scene);
		File file = wxMpService.getQrcodeService().qrCodePicture(ticket);
		return file;
	}

}
