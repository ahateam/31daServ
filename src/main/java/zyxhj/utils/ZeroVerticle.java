package zyxhj.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.Controller;

public abstract class ZeroVerticle extends AbstractVerticle {

	private static Logger log = LoggerFactory.getLogger(ZeroVerticle.class);

	private static final String PATH_ASSET = "-assets";

	protected Map<String, Controller> ctrlMap = new LinkedHashMap<>();

	protected abstract void init() throws Exception;

	protected abstract String name();

	protected void initCtrl(Map<String, Controller> map, Controller ctrl) {
		map.put(ctrl.getNode(), ctrl);
	}

	public void start() {
		log.error("verticle<{}> started", name());

		try {
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

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void handleHttpRequest(RoutingContext context) {

		HttpServerRequest req = context.request();
		HttpServerResponse resp = context.response();

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
				if (nodes[startInd].equalsIgnoreCase(name())) {
					// 找到SERVER_NAME所在的index
					flg = true;
					break;
				}
			}
			if (flg) {
				// 匹配到SERVER_NAME

				if (startInd + 1 >= nodes.length) {
					// 只有SERVER_NAME节点，显示list
					resp.putHeader("content-type", "application/json;charset=UTF-8");
					Controller.writeThings(resp, getCtrldocs());
				} else if (startInd + 2 >= nodes.length) {
					// 只有controller节点，没有method节点，返回错误
					resp.putHeader("content-type", "application/json;charset=UTF-8");

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
						resp.putHeader("content-type", "application/json;charset=UTF-8");
						try {
							ctrl.exec(method, context, req, resp);
						} catch (Exception e) {
							Controller.writeThings(resp, e.getMessage());
						}
					} else {
						// 最好不设置content-type的header，否则文件处理可能出错

						// 返回404错误
						// 没有找到合适的ctrl，则可能是模版或静态资源文件
						if (node.equalsIgnoreCase(PATH_ASSET)) {
							// goto template
							// -tmp 模版引擎处理
							int ind = reqPath.indexOf(PATH_ASSET) + PATH_ASSET.length();
							String temp = reqPath.substring(ind);

							String fileName = StringUtils.join("assets", temp);
							if (vertx.fileSystem().existsBlocking(fileName)) {
								// 处理静态文件
								resp.sendFile(fileName);
								// 需要retrun，防止本函数写入终止符
								return;
							} else {
								// 模版和静态文件都不存在
								Controller.doResponseFailure(resp, BaseRC.SERVER_ERROR,
										StringUtils.join("missing file >", fileName));
							}
						} else {
							Controller.doResponseFailure(resp, BaseRC.SERVER_ERROR,
									StringUtils.join("missing controller ", node));
						}
					}
				}
			}
		} else {
			// 返回404错误
			resp.putHeader("content-type", "application/json;charset=UTF-8");
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

	public Map<String, Controller> getCtrlList() {
		return ctrlMap;
	}

	public Controller getCtrlDetail(String name) {
		return ctrlMap.get(name);
	}

	private String getCtrldocs() {

		StringBuffer sb = new StringBuffer();
		String ln = "\n";
		String ln2 = "\n\n";

		sb.append("\t\tSERVER <").append(name()).append(">").append(ln2);

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
