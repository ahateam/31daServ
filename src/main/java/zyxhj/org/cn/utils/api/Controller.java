package zyxhj.org.cn.utils.api;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import zyxhj.org.cn.utils.CodecUtils;

public abstract class Controller {

	private static Logger log = LoggerFactory.getLogger(Controller.class);

	public static final byte TYPE_GET = 0;
	public static final byte TYPE_POST = 1;
	public static final byte TYPE_GETAPI = 2;
	public static final byte TYPE_POSTAPI = 3;

	/**
	 * HTTP GET方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface GET {
		public String path();
	}

	/**
	 * HTTP POST方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface POST {
		public String path();
	}

	/**
	 * GET API方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface GETAPI {
		public String path();

		// 默认需要验证，如登录，注册等方法
		public boolean verify() default true;
	}

	/**
	 * POST API方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface POSTAPI {
		public String path();

		// 默认需要验证，如登录，注册等方法
		public boolean verify() default true;
	}

	/**
	 * Controller的url根目录节点
	 */
	protected String node;

	protected Map<String, Object[]> methods = new HashMap<String, Object[]>();

	/**
	 * 无效，并被警告的方法
	 */
	protected Map<String, Method> warningMethods = new HashMap<String, Method>();

	private void addMethodIntoMap(Method m, String path, byte type, boolean verify) {
		if (StringUtils.isBlank(path)) {
			log.info(">>>Method add nothing");
		} else {
			if (methods.containsKey(path)) {
				// 如果要添加的path，已经被暂用，则将此方法列入警告
				warningMethods.put(path, m);
			} else {
				methods.put(path, new Object[] { m, type, verify });
			}
		}
	}

	protected Controller(String node) {
		this.node = node;

		Method[] ms = this.getClass().getMethods();
		for (Method m : ms) {

			// 识别GET
			{
				GET method = m.getAnnotation(GET.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), TYPE_GET, false);
					continue;
				}
			}

			// 识别POST
			{
				POST method = m.getAnnotation(POST.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), TYPE_POST, false);
					continue;
				}
			}

			// 识别GETAPI
			{
				GETAPI method = m.getAnnotation(GETAPI.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), TYPE_GETAPI, method.verify());
					continue;
				}
			}

			// 识别POSTAPI
			{
				POSTAPI method = m.getAnnotation(POSTAPI.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), TYPE_POSTAPI, method.verify());
					continue;
				}
			}

		}
	}

	/**
	 * 获取当前Controller的url节点字符串
	 */
	public String getNode() {
		return node;
	}

	/**
	 * 执行方法，供Servlet入口进行调用
	 */
	public void exec(String method, RoutingContext context, HttpServerRequest req, HttpServerResponse resp)
			throws IOException {

		Object[] ms = methods.get(method);
		if (null != ms) {
			Method m = (Method) ms[0];
			byte t = (byte) ms[1];
			boolean verify = (boolean) ms[2];

			if (t == TYPE_GET) {
				execGet(m, context, req, resp);
			} else if (t == TYPE_POST) {
				execPost(m, context, req, resp);
			} else if (t == TYPE_GETAPI) {
				execGetAPI(m, verify, context, req, resp);
			} else if (t == TYPE_POSTAPI) {
				execPostAPI(m, verify, context, req, resp);
			} else {
				// 返回404错误
				resp.setStatusCode(404);
				resp.setStatusMessage("missing controller method");
			}
		} else {
			// 返回404错误
			resp.setStatusCode(404);
			resp.setStatusMessage("missing controller method");
		}

	}

	private void execGet(Method m, RoutingContext context, HttpServerRequest req, HttpServerResponse resp)
			throws IOException {
		try {
			HttpMethod reqMethod = req.method();
			if (reqMethod.equals(HttpMethod.GET)) {
				m.invoke(this, context, req, resp);
			} else {
				// 不是GET方法
				resp.setStatusCode(500);
				resp.setStatusMessage("must be HTTP GET method");
			}
		} catch (InvocationTargetException ite) {
			// 反射的代理异常，剥壳后才是真实的异常
			String targetException = ite.getTargetException().getMessage();
			resp.setStatusCode(500);
			resp.setStatusMessage(targetException);
			ite.printStackTrace();
		} catch (Exception e) {
			String targetException = e.getMessage();
			resp.setStatusCode(500);
			resp.setStatusMessage(targetException);
			e.printStackTrace();
		}
	}

	private void execPost(Method m, RoutingContext context, HttpServerRequest req, HttpServerResponse resp)
			throws IOException {
		try {
			HttpMethod reqMethod = req.method();
			if (reqMethod.equals(HttpMethod.POST)) {
				m.invoke(this, context, req, resp);
			} else {
				// 不是POST方法
				resp.setStatusCode(500);
				resp.setStatusMessage("must be HTTP POST method");
			}
		} catch (InvocationTargetException ite) {
			// 反射的代理异常，剥壳后才是真实的异常
			String targetException = ite.getTargetException().getMessage();
			log.debug("execPost299>>>{}", targetException);
			resp.setStatusCode(500);
			resp.setStatusMessage(targetException);
		} catch (Exception e) {
			String targetException = e.getMessage();
			resp.setStatusCode(500);
			resp.setStatusMessage(targetException);
		}
	}

	private void execGetAPI(Method m, boolean verify, RoutingContext context, HttpServerRequest req,
			HttpServerResponse resp) throws IOException {
		try {
			HttpMethod reqMethod = req.method();
			if (reqMethod.equals(HttpMethod.GET)) {
				// GET方法的内容在url里，获取GET方法的req参数
				// 无需进行urlDecode
				String strRequest = req.getParam("req");

				if (StringUtils.isNotBlank(strRequest)) {
					APIRequest jsonRequest = JSON.parseObject(strRequest, APIRequest.class);
					if (verify) {
						String id = jsonRequest.id;
						String v = jsonRequest.v;

						// getsession，使用token验证，然后跟v对比

						// 暂时不验证，直接通过

						APIResponse jsonResponse = (APIResponse) m.invoke(this, jsonRequest);
						doResponseSuccess(resp, jsonResponse);
					} else {
						APIResponse jsonResponse = (APIResponse) m.invoke(this, jsonRequest);
						doResponseSuccess(resp, jsonResponse);
					}
				} else {
					doResponseFailure(resp, BaseRC.EMPTY_REQUEST, "");
				}
			} else {
				// 不是GET方法，抛异常
				doResponseFailure(resp, BaseRC.METHOD_NOT_FOUND, "");
			}
		} catch (InvocationTargetException ite) {
			// 反射的代理异常，剥壳后才是真实的异常
			Throwable targetException = ite.getTargetException();
			dealException(resp, targetException);
		} catch (Exception e) {
			e.printStackTrace();
			dealException(resp, e);
		}
	}

	private void execPostAPI(Method m, boolean verify, RoutingContext context, HttpServerRequest req,
			HttpServerResponse resp) throws IOException {
		try {
			HttpMethod reqMethod = req.method();
			if (reqMethod.equals(HttpMethod.POST)) {
				String strRequest = context.getBodyAsString(CodecUtils.ENCODING_UTF8);

				if (StringUtils.isNotBlank(strRequest)) {
					APIRequest jsonRequest = JSON.parseObject(strRequest, APIRequest.class);
					if (verify) {
						String id = jsonRequest.id;
						String v = jsonRequest.v;

						// getsession，使用token验证，然后跟v对比

						// 暂时不验证，直接通过

						APIResponse jsonResponse = (APIResponse) m.invoke(this, jsonRequest);
						doResponseSuccess(resp, jsonResponse);
					} else {
						APIResponse jsonResponse = (APIResponse) m.invoke(this, jsonRequest);
						doResponseSuccess(resp, jsonResponse);
					}
				} else {
					doResponseFailure(resp, BaseRC.EMPTY_REQUEST, "");
				}
			} else {
				// 不是POST方法，抛异常
				doResponseFailure(resp, BaseRC.METHOD_NOT_FOUND, "");
			}
		} catch (InvocationTargetException ite) {
			// 反射的代理异常，剥壳后才是真实的异常
			Throwable targetException = ite.getTargetException();
			dealException(resp, targetException);
		} catch (Exception e) {
			e.printStackTrace();
			dealException(resp, e);
		}
	}

	private void dealException(HttpServerResponse resp, Throwable e) throws IOException {
		if (e instanceof ServerException) {
			ServerException serverException = (ServerException) e;
			e.printStackTrace();
			// 获取错误码并按APIResponse格式返回
			doResponseFailure(resp, serverException.getRC(), serverException.getMessage());
		} else {
			// 其它异常，则生成默认的response
			String content = e.getMessage();
			if (StringUtils.isBlank(content)) {
				doResponseFailure(resp, BaseRC.FAILURE, e.toString());
			} else {
				doResponseFailure(resp, BaseRC.FAILURE, content);
			}
		}
	}

	private static void doResponseSuccess(HttpServerResponse resp, APIResponse response) throws IOException {
		resp.setStatusCode(200);
		writeThings(resp, JSON.toJSONString(response));
	}

	private static void doResponseFailure(HttpServerResponse resp, RC rc, String content) throws IOException {
		resp.setStatusCode(200);
		writeThings(resp, JSON.toJSONString(APIResponse.getNewFailureResp(rc, content)));
	}

	protected static void writeThings(HttpServerResponse resp, String content) {
		resp.setStatusCode(200);
		int len = content.getBytes(CodecUtils.CHARSET_UTF8).length;
		resp.putHeader("Content-Length", Integer.toString(len));
		resp.write(content, CodecUtils.ENCODING_UTF8);
	}

	public JSONArray getJSONDocs() {

		JSONArray ret = new JSONArray();

		Iterator<Entry<String, Object[]>> it = methods.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Object[]> entry = it.next();
			String key = entry.getKey();
			Object[] value = entry.getValue();

			Method m = (Method) value[0];
			byte type = (byte) value[1];
			boolean verify = (boolean) value[2];

			String temp = "";
			if (type == TYPE_GET) {
				temp = "GET";
			} else if (type == TYPE_POST) {
				temp = "POST";
			} else if (type == TYPE_GETAPI) {
				temp = "GETAPI";
			} else if (type == TYPE_POSTAPI) {
				temp = "POSTAPI";
			} else {
				temp = "null";
			}

			ret.add(StringUtils.join(key, "     <", temp, " - ", verify, "> "));
		}

		return ret;
	}
}
