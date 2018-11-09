package zyxhj.org.cn.custom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;

public class WxDataService {
	
	private static Logger log = LoggerFactory.getLogger(WxDataService.class);

	private static WxDataService ins;

	public static synchronized WxDataService getInstance() {
		if (null == ins) {
			ins = new WxDataService();
		}
		return ins;
	}
	
	private WxMpInMemoryConfigStorage wxMpConfigStorage;
	private WxMpService wxMpService;

	private WxDataService() {
		try {
			// 微信参数配置
			wxMpConfigStorage=new WxMpInMemoryConfigStorage();
			wxMpConfigStorage.setAppId(WxDataService.APPID); // APPid
			wxMpConfigStorage.setSecret(WxDataService.APPSECRET); // AppSecret
			wxMpConfigStorage.setToken(WxDataService.TOKEN); // 设置微信公众号的token
			wxMpConfigStorage.setAesKey(WxDataService.AESKEY); // 设置微信公众号的EncodingAESKey
			wxMpService=new WxMpServiceImpl();
			wxMpService.setWxMpConfigStorage(wxMpConfigStorage);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	

	public static final String APPID = "wx9aaf23b05328a771";
	public static final String APPSECRET = "6b72b49c33db086d6f62931f94e9ee1b";
	public static final String TOKEN = "wx3ch";
	public static final String AESKEY = "XZ2ZdYwchouGBDzZEzpAJEKdAqTwKrcwiOMP7n2cNDJ";

	
	public WxMpInMemoryConfigStorage getWxMpConfigStorage() {
		return wxMpConfigStorage;
	}

	public WxMpService getWxMpService() {
		return wxMpService;
	}

}
