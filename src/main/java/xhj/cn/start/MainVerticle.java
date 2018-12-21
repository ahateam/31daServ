package xhj.cn.start;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.templ.pebble.PebbleTemplateEngine;
import zyxhj.cms.controller.ContentController;
import zyxhj.core.controller.TagController;
import zyxhj.core.controller.TestController;
import zyxhj.core.controller.UserController;
import zyxhj.custom.controller.HttpTestController;
import zyxhj.economy.controller.ORGController;
import zyxhj.economy.controller.VoteController;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSourceUtils;

public class MainVerticle extends AbstractVerticle {

	private static final String SERVER_NAME = "zero";

	private static final String PATH_ASSET = "-assets";

	protected Map<String, Controller> ctrlMap;

	private PebbleTemplateEngine engine;

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}

	public void init() {

		DataSourceUtils.initDataSourceConfig();

		ctrlMap = new HashMap<>();

		initCtrl(ctrlMap, TestController.getInstance("test"));

		initCtrl(ctrlMap, UserController.getInstance("user"));

		initCtrl(ctrlMap, TagController.getInstance("tag"));

		initCtrl(ctrlMap, ContentController.getInstance("content"));

		initCtrl(ctrlMap, HttpTestController.getInstance("httptest"));

		initCtrl(ctrlMap, ORGController.getInstance("org"));

		initCtrl(ctrlMap, VoteController.getInstance("vote"));

		// putCtrlInMap(ctrlMap, StoreController.getInstance("store"));

	}

	private void initCtrl(Map<String, Controller> map, Controller ctrl) {
		map.put(ctrl.getNode(), ctrl);
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
		router.route("/*").handler(this::handleHttpRequest);

		// 增加一个模版引擎
		engine = PebbleTemplateEngine.create(vertx);

		httpServer.requestHandler(router);
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

		resp.putHeader("content-type", "application/json;charset=UTF-8");
		resp.putHeader("Access-Control-Allow-Origin", "*");// 设置跨域，目前不限制。TODO，将来需要设定指定的来源

		System.out.println(StringUtils.join(req.method(), " - ", req.path()));

		String reqPath = req.path();
		String[] nodes = uri2Nodes(reqPath);
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
					Controller.writeThings(resp, getCtrldocs());
				} else if (startInd + 2 >= nodes.length) {
					// 只有controller节点，没有method节点，返回错误
					String node = nodes[startInd + 1];
					Controller ctrl = ctrlMap.get(node);
					if (ctrl == null) {
						Controller.doResponseFailure(resp, BaseRC.SERVER_ERROR,
								StringUtils.join("missing controller ", node));
					} else {
						Controller.writeThings(resp, ctrl.getJSCode());
					}
				} else {
					String node = nodes[startInd + 1];
					String method = nodes[startInd + 2];
					Controller ctrl = ctrlMap.get(node);
					if (null != ctrl) {
						try {
							ctrl.exec(method, context, req, resp);
						} catch (Exception e) {
							Controller.writeThings(resp, e.getMessage());
						}
					} else {
						// 返回404错误
						// 没有找到合适的ctrl，则可能是模版或静态资源文件
						if (node.equalsIgnoreCase(PATH_ASSET)) {
							// goto template
							// -tmp 模版引擎处理
							int ind = reqPath.indexOf(PATH_ASSET) + PATH_ASSET.length();
							String temp = reqPath.substring(ind);
							JsonObject datax = new JsonObject().put("name", "Vert.x Web").put("path", "xxxxx");

							String fileName = StringUtils.join("assets/", temp);

							if (vertx.fileSystem().existsBlocking(fileName)) {
								// 静态文件存在
								resp.sendFile(fileName);
								// 需要retrun，防止本函数写入终止符
								return;
							} else {
								// 文件不存在,尝试模版
								String pebFileName = StringUtils.join(fileName, ".peb");
								if (vertx.fileSystem().existsBlocking(pebFileName)) {
									// 模版文件存在
									engine.render(datax, fileName, res -> {
										if (res.succeeded()) {
											// 异步过程结束
											resp.end(res.result());
										}
									});
									// 异步返回了文件结果，需要retrun，防止本函数写入终止符
									return;
								} else {
									Controller.doResponseFailure(resp, BaseRC.SERVER_ERROR,
											StringUtils.join("missing file ", fileName));
								}
							}

							return;
						} else {
							Controller.doResponseFailure(resp, BaseRC.SERVER_ERROR,
									StringUtils.join("missing controller ", node));
						}
					}
				}
			}
		} else {
			// 返回404错误
			Controller.doResponseFailure(resp, BaseRC.SERVER_ERROR, StringUtils.join("missing controller ", reqPath));
		}

		resp.end();
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

		StringBuffer sb = new StringBuffer();
		String ln = "\n";
		String ln2 = "\n\n";

		sb.append("\t\tSERVER <").append(SERVER_NAME).append(">").append(ln2);

		Iterator<Entry<String, Controller>> it = ctrlMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Controller> entry = it.next();

			String key = entry.getKey();
			Controller value = entry.getValue();

			sb.append(">>>>> ").append(key).append(ln);

			value.getJSONDocs(sb);

			sb.append(ln).append(
					"\t------------------------------------------------------------------------------------------\t")
					.append(ln2);
		}

		return sb.toString();
	}

}
