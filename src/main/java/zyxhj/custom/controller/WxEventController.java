package zyxhj.custom.controller;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import zyxhj.custom.service.WxDataService;
import zyxhj.custom.service.WxFuncService;
import zyxhj.custom.util.HttpClientUtil;
import zyxhj.utils.CodecUtils;
import zyxhj.utils.api.Controller;

public class WxEventController extends Controller {

	private static Logger log = LoggerFactory.getLogger(WxEventController.class);

	private static WxEventController ins;

	public static synchronized WxEventController getInstance(String node) {
		if (null == ins) {
			ins = new WxEventController(node);
		}
		return ins;
	}

	private WxDataService wxDataService;
	private WxMpMessageRouter wxMpMessageRouter;
	private WxFuncService wxFuncService;

	private WxEventController(String node) {
		super(node);
		try {
			wxDataService = WxDataService.getInstance();
			wxFuncService = WxFuncService.getInstance();

			/**
			 * 关注事件
			 */
			WxMpMessageHandler handler = new WxMpMessageHandler() {
				@Override
				public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
						WxMpService wxMpService, WxSessionManager sessionManager) {
					System.err.println("场景值:  "+wxMessage.getEventKey().substring(8));
					WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("欢迎关注！！！")
							.fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
					return m;
				}
			};

			/**
			 * test消息回复
			 */
			WxMpMessageHandler testHander = new WxMpMessageHandler() {
				@Override
				public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
						WxMpService wxMpService, WxSessionManager sessionManager) {
					WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("test Go")
							.fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
					return m;
				}
			};

			/*
			 * test卡券领取
			 */
			WxMpMessageHandler cardHandler = new WxMpMessageHandler() {
				@Override
				public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
						WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
					String detail = wxMpService.getCardService().getCardDetail(wxMessage.getCardId());
					Map<String, Object> json2Map = HttpClientUtil.parseJSON2Map(detail);
					String title = (String) json2Map.get("title");
					System.err.println("场景值：  " + wxMessage.getOuterStr());
					WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("领取卡券：" + title)
							.fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
					return m;
				}
			};

			/*
			 * test卡券核销
			 */
			WxMpMessageHandler consumeHandler = new WxMpMessageHandler() {
				@Override
				public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context,
						WxMpService wxMpService, WxSessionManager sessionManager) throws WxErrorException {
					WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content("核销卡券SUCCESS！！")
							.fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
					return m;
				}
			};

			// 路由器配置
			wxMpMessageRouter = new WxMpMessageRouter(wxDataService.getWxMpService());
			wxMpMessageRouter
					
					.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.SUBSCRIBE)
					.handler(handler).end()

					.rule().async(false).msgType(WxConsts.XmlMsgType.TEXT).content("test").handler(testHander).end()

					.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT).event(WxConsts.EventType.CARD_USER_GET_CARD)
					.handler(cardHandler).end()

					.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
					.event(WxConsts.EventType.CARD_USER_CONSUME_CARD).handler(consumeHandler).end();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// 微信监听入口
	@GET(path = "monitorEvent")
	public void monitorEvent(HttpServerRequest req, HttpServerResponse resp, RoutingContext context) {
		System.err.println("testIn");

		String strRet = "failure";
		try {
			// 微信加密签名
			String signature = req.getParam("signature");
			// 时间戳
			String timestamp = req.getParam("timestamp");
			// 随机数
			String nonce = req.getParam("nonce");
			// 随机字符串
			String echostr = req.getParam("echostr");

			if (!wxDataService.getWxMpService().checkSignature(timestamp, nonce, signature)) {
				// 消息签名不正确，说明不是公众平台发过来的消息
				ret(resp, "非法请求");
				return;
			}
			if (StringUtils.isNotBlank(echostr)) {
				// 说明是一个仅仅用来验证的请求，回显echostr
				ret(resp, echostr);
				return;
			}

			String encryptType = StringUtils.isBlank(req.getParam("encrypt_type")) ? "raw"
					: req.getParam("encrypt_type");

			if ("raw".equals(encryptType)) {
				System.err.println("context :  " + context.getBodyAsString(CodecUtils.ENCODING_UTF8));
				// 明文传输的消息
				WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(context.getBodyAsString(CodecUtils.ENCODING_UTF8));
				System.err.println("inms:" + inMessage);
				WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);
				System.err.println("outms:" + outMessage);
				if (outMessage == null) {
					// 为null，说明路由配置有问题，需要注意
					ret(resp, strRet);
				} else {
					ret(resp, outMessage.toXml());
				}
				return;
			}
		} catch (Exception e) {
			log.error("微信监听事件异常：", e);
			strRet = "failure";
		}
	}

	// 回复编码
	private void ret(HttpServerResponse resp, String str) {
		int len = str.getBytes(CodecUtils.CHARSET_UTF8).length;
		resp.putHeader("content-type", "text/html;charset=utf-8");
		resp.putHeader("content-length", Integer.toString(len));
		resp.write(str);
	}

}
