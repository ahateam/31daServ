package zyxhj.custom.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
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
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = new ArrayList<String>();
		list.add("CARD_STATUS_VERIFY_OK");
		map.put("offset", 0);
		map.put("count", 10);
		map.put("status_list", list);
		String json = JSONObject.toJSONString(map);
		String reJson = post("https://api.weixin.qq.com/card/batchget?access_token=" + wxMpService.getAccessToken(),
				json);
		Map<String, Object> json2Map = parseJSON2Map(reJson);
		return json2Map;
	}

	/*
	 * 获取卡卷二维码
	 * 
	 * cardId 卡卷ID outerStr 场景值 expiresIn 失效时间，单位秒，不填默认365天
	 * 
	 */
	public WxMpCardQrcodeCreateResult getTest3(WxMpService wxMpService, String cardId, String outerStr, int expiresIn)
			throws WxErrorException {
		WxMpCardQrcodeCreateResult card = wxMpService.getCardService().createQrcodeCard(cardId, outerStr, expiresIn);
		return card;
	}

	/*
	 * 获取卡卷详情
	 * 
	 * cardId 卡卷ID
	 */
	public String getTest4(WxMpService wxMpService, String cardId) throws WxErrorException {
		String detail = wxMpService.getCardService().getCardDetail(cardId);
		return detail;
	}

	/*
	 * 根据用户反馈授权获取对应信息
	 */
	public WxMpUser getUserMessage(WxMpService wxMpService, String code) throws Exception {
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
		for (String openId : openIds) {
			massMessage.getToUsers().add(openId);
		}
		WxMpMassSendResult massResult = wxMpService.getMassMessageService().massOpenIdsMessageSend(massMessage);
		return massResult;
	}

	/*
	 * 模板消息测试（暂未开通，需申请）
	 */
	public void templateMessageTest(WxMpService wxMpService) throws WxErrorException {
		WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder().toUser("ou_IQ1cNT-BpkpvHHtUa-6c-JivA")
				.templateId("123").url("http://aha-element.oss-cn-hangzhou.aliyuncs.com/index.html").build();

		templateMessage.addData(new WxMpTemplateData("test", "Let us test this!!", "blue"));

		wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
	}

	/*
	 * 网页授权域名获取
	 */
	public String getUrl(WxMpService wxMpService, String url) {
		System.err.println("test：get url");
		String url2 = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, null);
		// ret(resp, url2);
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

	/**
	 * @描述 get方式获取外部接口返回json串
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String get(String url) throws IOException {
		String returnVal = "";
		// 定义httpClient的实例
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();// 设置请求和传输超时时间
		httpGet.setConfig(requestConfig);
		try {
			CloseableHttpResponse response2 = httpclient.execute(httpGet);// 执行请求
			// log.info();
			HttpEntity entity2 = (HttpEntity) response2.getEntity();
			if (entity2 != null) {

				returnVal = EntityUtils.toString(entity2, "UTF-8");

			} else {
				returnVal = null;
			}

		} catch (ClientProtocolException e) {
			// log.info();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.info();

		} finally {

			if (httpclient != null) {

				httpclient.close();
			}

		}

		return returnVal;

	}

	/**
	 * @描述 json串转换map
	 * @param bizData
	 * @return
	 */
	public static Map<String, Object> parseJSON2Map(String bizData) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			JSONObject bizDataJson = JSONObject.parseObject(bizData);
			// 获取json对象值
			for (Object key : bizDataJson.keySet()) {
				Object value = bizDataJson.get(key);
				// 判断值是否为json数组类型
				if (value instanceof JSONArray) {
					// 如果为json数组类型迭代循环取值
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					Iterator<Object> it = ((JSONArray) value).iterator();

					while (it.hasNext()) {
						JSONObject json2 = (JSONObject) it.next();
						list.add(parseJSON2Map(json2.toString()));
					}
					ret.put(String.valueOf(key), list);
				} else {
					ret.put(String.valueOf(key), String.valueOf(value));
				}
			}
		} catch (Exception e) {
			// log.info();
		}
		return ret;
	}

	/**
	 * post请求（用于请求json格式的参数）
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, String params) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);// 创建httpPost
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		String charSet = "UTF-8";
		StringEntity entity = new StringEntity(params, charSet);
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			StatusLine status = response.getStatusLine();
			int state = status.getStatusCode();
			if (state == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				String jsonString = EntityUtils.toString(responseEntity);
				return jsonString;
			} else {
				// log.error("请求返回:"+state+"("+url+")");
			}
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
