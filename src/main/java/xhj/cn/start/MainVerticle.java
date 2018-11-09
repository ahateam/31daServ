package xhj.cn.start;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import zyxhj.org.cn.cms.controller.ContentController;
import zyxhj.org.cn.core.controller.TestController;
import zyxhj.org.cn.custom.controller.HttpTestController;
import zyxhj.org.cn.utils.CodecUtils;
import zyxhj.org.cn.utils.api.Controller;
import zyxhj.org.cn.utils.data.DataSourceUtils;

public class MainVerticle extends AbstractVerticle {

	private static final String SERVER_NAME = "zero";

	protected Map<String, Controller> ctrlMap;

	public void init() {

		DataSourceUtils.initDataSourceConfig();

		ctrlMap = new HashMap<>();

		putCtrlInMap(ctrlMap, TestController.getInstance("test"));

		putCtrlInMap(ctrlMap, ContentController.getInstance("content"));

		putCtrlInMap(ctrlMap, HttpTestController.getInstance("httptest"));

		// putCtrlInMap(ctrlMap, StoreController.getInstance("store"));

	}

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}

	public void start() {
		System.out.println("started");

		init();

		HttpServer httpServer = vertx.createHttpServer();

		Set<String> allowHeaders = new HashSet<>();
		allowHeaders.add("x-requested-with");
		allowHeaders.add("Access-Control-Allow-Origin");
		allowHeaders.add("origin");
		allowHeaders.add("Content-Type");
		allowHeaders.add("accept");

		// REST 增删查改方法
		Set<HttpMethod> allowMethods = new HashSet<>();
		allowMethods.add(HttpMethod.GET);
		allowMethods.add(HttpMethod.POST);

		// 实例化一个路由器出来，用来路由不同的rest接口
		Router router = Router.router(vertx);
		// 增加一个处理器，将请求的上下文信息，放到RoutingContext中
		router.route().handler(BodyHandler.create());

		String serverUrl = StringUtils.join("/", SERVER_NAME, "/*");

		// 处理一个post方法的rest接口
		router.post(serverUrl).handler(this::handleHttpRequest);
		// 处理一个get方法的rest接口
		router.get(serverUrl).handler(this::handleHttpRequest);

		httpServer.requestHandler(router::accept);
		httpServer.listen(8080, res -> {
			if (res.succeeded()) {
				System.out.println("Server is now listening!");
			} else {
				System.out.println("Fatal error: " + res.cause());
				vertx.close(); // 严重错误，不应该继续运行，需要关闭vertx实例
				System.exit(-1); // 自定义程序非正常退出码，这里定义255
			}
		});
	}

	private void handleHttpRequest(RoutingContext context) {
		HttpServerRequest req = context.request();
		HttpServerResponse resp = context.response();

		// System.out.println("handleHttpRequest");

		resp.putHeader("content-type", "application/json;charset=UTF-8");
		resp.putHeader("Access-Control-Allow-Origin", "*");// 设置跨域，目前不限制。TODO，将来需要设定指定的来源

		exec(context, req, resp);
		resp.end();
	}

	private void putCtrlInMap(Map<String, Controller> map, Controller ctrl) {
		map.put(ctrl.getNode(), ctrl);
	}

	private static void writeThings(HttpServerResponse resp, String content) {
		resp.setStatusCode(200);
		int len = content.getBytes(CodecUtils.CHARSET_UTF8).length;
		resp.putHeader("Content-Length", Integer.toString(len));
		resp.write(content, CodecUtils.ENCODING_UTF8);
	}

	private void exec(RoutingContext context, HttpServerRequest req, HttpServerResponse resp) {
		String requestURI = req.path();

		System.out.println(StringUtils.join(req.method(), " - ", req.path()));

		String[] nodes = uri2Nodes(requestURI);

		if (null != nodes && nodes.length > 0) {
			// 可能因为nginx反向代理，在SERVER_NAME前加入多级子域名，需要过滤掉
			// 去除nodes中，SERVER_NAME之前的部分
			int startInd = 0;
			boolean flg = false;
			for (; startInd < nodes.length; startInd++) {
				if (nodes[startInd].equalsIgnoreCase(SERVER_NAME)) {
					// 找到SERVER_NAME所在的index
					flg = true;
					break;
				}
			}
			if (flg) {
				// 匹配到SERVER_NAME

				if (startInd + 1 >= nodes.length) {
					// 只有SERVER_NAME节点，显示list
					writeThings(resp, getCtrldocs());
				} else if (startInd + 2 >= nodes.length) {
					// 只有controller节点，没有method节点，返回错误
					resp.setStatusCode(404);
					resp.setStatusMessage("missing controller method");
				} else {
					String node = nodes[1];
					String method = nodes[2];
					Controller ctrl = ctrlMap.get(node);
					if (null != ctrl) {
						try {
							ctrl.exec(method, context, req, resp);
						} catch (Exception e) {
							writeThings(resp, e.getMessage());
						}
					} else {
						// 返回404错误
						resp.setStatusCode(404);
						resp.setStatusMessage("missing controller");
					}
				}
			}
		} else {
			// 返回404错误
			resp.setStatusCode(404);
			resp.setStatusMessage("missing controller");
		}
	}

	/**
	 * 斜杠
	 */
	private static final char URI_SLASH = '/';

	private static String[] uri2Nodes(String uri) {
		// 去前后空白
		String tmp = StringUtils.trim(uri);
		// 去前后斜杠
		if (tmp.length() > 0) {
			if (tmp.charAt(0) == URI_SLASH) {
				tmp = tmp.substring(1);
			}
		}
		if (tmp.length() > 0) {
			if (tmp.charAt(tmp.length() - 1) == URI_SLASH) {
				tmp = tmp.substring(0, tmp.length() - 1);
			}
		}

		// 根据斜杠拆分
		if (tmp.length() > 0) {
			String[] nodes = StringUtils.split(tmp, URI_SLASH);
			return nodes;
		} else {
			return null;
		}
	}

	private String getCtrldocs() {

		JSONArray ja = new JSONArray();

		Iterator<Entry<String, Controller>> it = ctrlMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Controller> entry = it.next();

			String key = entry.getKey();
			Controller value = entry.getValue();

			JSONObject ctrl = new JSONObject();
			ctrl.put(key, value.getJSONDocs());

			ja.add(ctrl);
		}

		return JSON.toJSONString(ja, true);
	}
}
