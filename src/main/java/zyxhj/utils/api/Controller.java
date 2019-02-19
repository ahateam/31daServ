package zyxhj.utils.api;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import zyxhj.utils.CodecUtils;

public abstract class Controller {

	private static Logger log = LoggerFactory.getLogger(Controller.class);

	private static final byte TYPE_GET = 0;
	private static final byte TYPE_POST = 1;
	private static final byte TYPE_GETAPI = 2;
	private static final byte TYPE_POSTAPI = 3;

	public interface ENUMVALUE {
		public byte v();

		public String txt();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface NODE {

		public String name();
	}

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface P {
		public boolean r() default true;

		public String t();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface ENUM {

		public String des();
	}

	/**
	 * HTTP GET方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface GET {
		public String path();

		public String des();

		public String ret() default "";
	}

	/**
	 * HTTP POST方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface POST {
		public String path();

		public String des();

		public String ret() default "";
	}

	/**
	 * GET API方法
	 */
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	protected @interface GETAPI {
		public String path();

		public String des();

		public String ret()

		default "";

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

		public String des();

		public String ret()

		default "";

		// 默认需要验证，如登录，注册等方法
		public boolean verify() default true;
	}

	/**
	 * Controller的url根目录节点
	 */
	protected String node;

	protected Map<String, Object[]> methods = new TreeMap<>();

	/**
	 * 无效，并被警告的方法
	 */
	protected Map<String, Method> warningMethods = new HashMap<>();

	protected Map<String, Object[]> enumFields = new TreeMap<>();

	private void addMethodIntoMap(Method m, String path, String des, String ret, byte type, boolean verify) {
		if (StringUtils.isBlank(path)) {
			log.info(">>>Method add nothing");
		} else {
			if (methods.containsKey(path)) {
				// 如果要添加的path，已经被暂用，则将此方法列入警告
				warningMethods.put(path, m);
			} else {

				int count = m.getParameterCount();
				Parameter[] parm = m.getParameters();

				P[] ann = null;
				if (parm != null && parm.length > 0) {

					ann = new P[parm.length];
					for (int i = 0; i < parm.length; i++) {
						ann[i] = parm[i].getAnnotation(P.class);
					}
				}

				methods.put(path, new Object[] { m, des, ret, type, verify, count, parm, ann });
			}
		}
	}

	protected Controller(String name) {
		// NODE nn = this.getClass().getAnnotation(NODE.class);
		// this.node = nn.name();
		this.node = name;

		Method[] ms = this.getClass().getMethods();

		Field[] fs = this.getClass().getDeclaredFields();
		for (Field f : fs) {
			ENUM en = f.getAnnotation(ENUM.class);
			if (null != en) {
				enumFields.put(f.getName(), new Object[] { f, en });
			}
		}

		for (Method m : ms) {

			// 识别GET
			{
				GET method = m.getAnnotation(GET.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), method.des(), method.ret(), TYPE_GET, false);
					continue;
				}
			}

			// 识别POST
			{
				POST method = m.getAnnotation(POST.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), method.des(), method.ret(), TYPE_POST, false);
					continue;
				}
			}

			// 识别GETAPI
			{
				GETAPI method = m.getAnnotation(GETAPI.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), method.des(), method.ret(), TYPE_GETAPI, method.verify());
					continue;
				}
			}

			// 识别POSTAPI
			{
				POSTAPI method = m.getAnnotation(POSTAPI.class);
				if (null != method) {
					addMethodIntoMap(m, method.path(), method.des(), method.ret(), TYPE_POSTAPI, method.verify());
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
			int xx = 0;
			Method m = (Method) ms[0];
			String des = (String) ms[1];
			String ret = (String) ms[2];
			byte t = (byte) ms[3];
			boolean verify = (boolean) ms[4];
			int count = (int) ms[5];
			Parameter[] parm = (ms[6] == null ? null : (Parameter[]) ms[6]);
			P[] ann = (ms[7] == null ? null : (P[]) ms[7]);

			if (t == TYPE_GET) {
				execGet(m, context, req, resp);
			} else if (t == TYPE_POST) {
				execPost(m, context, req, resp);
			} else if (t == TYPE_GETAPI) {
				execGetAPI(m, verify, context, req, resp, count, parm, ann);
			} else if (t == TYPE_POSTAPI) {
				execPostAPI(m, verify, context, req, resp, count, parm, ann);
			} else {
				doResponseFailure(resp, BaseRC.SERVER_ERROR, "missing controller method");
			}
		} else {
			doResponseFailure(resp, BaseRC.SERVER_ERROR, "missing controller method");
		}
	}

	private void execGet(Method m, RoutingContext context, HttpServerRequest req, HttpServerResponse resp)
			throws IOException {
		try {
			m.invoke(this, context, req, resp);
		} catch (InvocationTargetException ite) {
			// 反射的代理异常，剥壳后才是真实的异常
			String targetException = ite.getTargetException().getMessage();
			doResponseFailure(resp, BaseRC.SERVER_ERROR, targetException);
		} catch (Exception e) {
			String targetException = e.getMessage();
			doResponseFailure(resp, BaseRC.SERVER_ERROR, targetException);
		}
	}

	private void execPost(Method m, RoutingContext context, HttpServerRequest req, HttpServerResponse resp)
			throws IOException {
		try {
			m.invoke(this, context, req, resp);
		} catch (InvocationTargetException ite) {
			// 反射的代理异常，剥壳后才是真实的异常
			String targetException = ite.getTargetException().getMessage();
			doResponseFailure(resp, BaseRC.SERVER_ERROR, targetException);
		} catch (Exception e) {
			String targetException = e.getMessage();
			doResponseFailure(resp, BaseRC.SERVER_ERROR, targetException);
		}
	}

	private Object getP(JSONObject req, Parameter p, boolean required) throws ServerException {
		Class<?> c = p.getType();
		String name = p.getName();
		if (required) {
			// 必填参数，不允许为空
			if (c.equals(Byte.class)) {
				return getByte(req, name);
			} else if (c.equals(Integer.class)) {
				return getInteger(req, name);
			} else if (c.equals(Long.class)) {
				return getLong(req, name);
			} else if (c.equals(Float.class)) {
				return getFloat(req, name);
			} else if (c.equals(Double.class)) {
				return getDouble(req, name);
			} else if (c.equals(Boolean.class)) {
				return getBoolean(req, name);
			} else if (c.equals(String.class)) {
				return getString(req, name);
			} else if (c.equals(JSONObject.class)) {
				return getJSONObject(req, name);
			} else if (c.equals(JSONArray.class)) {
				return getJSONArray(req, name);
			} else {
				return getObject(req, name, c);
			}
		} else {
			// 非必填参数
			if (c.equals(Byte.class)) {
				return getByteCanNull(req, name);
			} else if (c.equals(Integer.class)) {
				return getIntegerCanNull(req, name);
			} else if (c.equals(Long.class)) {
				return getLongCanNull(req, name);
			} else if (c.equals(Float.class)) {
				return getFloatCanNull(req, name);
			} else if (c.equals(Double.class)) {
				return getDoubleCanNull(req, name);
			} else if (c.equals(Boolean.class)) {
				return getBooleanCanNull(req, name);
			} else if (c.equals(String.class)) {
				return getStringCanNull(req, name);
			} else if (c.equals(JSONObject.class)) {
				return getJSONObjectCanNull(req, name);
			} else if (c.equals(JSONArray.class)) {
				return getJSONArrayCanNull(req, name);
			} else {
				return getObjectCanNull(req, name, c);
			}
		}
	}

	private void invoke(String strRequest, boolean verify, Method m, HttpServerResponse resp, int count,
			Parameter[] parm, P[] ann) throws IOException {
		if (StringUtils.isNotBlank(strRequest)) {
			APIRequest jsonRequest = JSON.parseObject(strRequest, APIRequest.class);
			try {
				if (verify) {
					String id = jsonRequest.id;
					String v = jsonRequest.v;

					// getsession，使用token验证，然后跟v对比

					// 暂时不验证，直接通过
				}

				APIResponse jsonResponse = null;
				if (count > 0) {

					JSONObject req = getReqContent(jsonRequest);

					Object[] parms = new Object[count];
					for (int i = 0; i < count; i++) {
						Parameter p = parm[i];
						P an = ann[i];
						boolean required = an == null ? true : an.r();

						parms[i] = getP(req, p, required);
					}
					jsonResponse = (APIResponse) m.invoke(this, parms);
				} else {
					// 没有参数
					jsonResponse = (APIResponse) m.invoke(this);
				}
				doResponseSuccess(resp, jsonResponse);

			} catch (InvocationTargetException ite) {
				// 反射的代理异常，剥壳后才是真实的异常
				Throwable targetException = ite.getTargetException();
				dealException(resp, targetException);
			} catch (Exception e) {
				e.printStackTrace();
				dealException(resp, e);
			}
		} else {
			doResponseFailure(resp, BaseRC.EMPTY_REQUEST, "");
		}
	}

	private void execGetAPI(Method m, boolean verify, RoutingContext context, HttpServerRequest req,
			HttpServerResponse resp, int count, Parameter[] parm, P[] ann) throws IOException {
		HttpMethod reqMethod = req.method();
		if (reqMethod.equals(HttpMethod.GET)) {
			// GET方法的内容在url里，获取GET方法的req参数
			// 无需进行urlDecode
			String strRequest = req.getParam("req");
			invoke(strRequest, verify, m, resp, count, parm, ann);
		} else {
			// 不是GET方法，抛异常
			doResponseFailure(resp, BaseRC.METHOD_NOT_FOUND, "");
		}
	}

	private void execPostAPI(Method m, boolean verify, RoutingContext context, HttpServerRequest req,
			HttpServerResponse resp, int count, Parameter[] parm, P[] ann) throws IOException {

		HttpMethod reqMethod = req.method();
		if (reqMethod.equals(HttpMethod.POST)) {
			String strRequest = context.getBodyAsString(CodecUtils.ENCODING_UTF8);
			invoke(strRequest, verify, m, resp, count, parm, ann);
		} else {
			// 不是POST方法，抛异常
			doResponseFailure(resp, BaseRC.METHOD_NOT_FOUND, "");
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

	private static void doResponseSuccess(HttpServerResponse resp, APIResponse response) {
		writeThings(resp, JSON.toJSONString(response));
	}

	public static void doResponseFailure(HttpServerResponse resp, RC rc, String content) {
		writeThings(resp, JSON.toJSONString(APIResponse.getNewFailureResp(rc, content)));
	}

	public static void writeThings(HttpServerResponse resp, String content) {
		resp.setStatusCode(200);
		int len = content.getBytes(CodecUtils.CHARSET_UTF8).length;
		resp.putHeader("Content-Length", Integer.toString(len));
		resp.write(content, CodecUtils.ENCODING_UTF8);
	}

	public Map<String, Object> getJSCodeNew() {

		// 常量

		Map<String, Object> retMap = new LinkedHashMap<>();

		{

			// 常量
			JSONArray enums = new JSONArray();
			Iterator<Entry<String, Object[]>> item = enumFields.entrySet().iterator();
			while (item.hasNext()) {
				Entry<String, Object[]> entry = item.next();
				String key = entry.getKey();
				Object[] value = (Object[]) entry.getValue();
				Field fff = (Field) value[0];
				ENUM eee = (ENUM) value[1];
				try {
					Object[] objs = (Object[]) fff.get(this);

					JSONObject jo = new JSONObject();
					jo.put("simpleName", fff.getType().getComponentType().getSimpleName());
					jo.put("name", fff.getType().getComponentType().getName());
					jo.put("des", eee.des());

					ArrayList<Object> items = new ArrayList<>();
					for (int i = 0; i < objs.length; i++) {
						Object obj = objs[i];
						ENUMVALUE ev = (ENUMVALUE) obj;

						JSONObject it = new JSONObject();
						it.put("key", ev.toString());
						it.put("v", ev.v());
						it.put("txt", ev.txt());

						items.add(it);
					}

					jo.put("items", items);

					enums.add(jo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			retMap.put("enums", enums);
		}

		{
			// 方法
			JSONArray ms = new JSONArray();

			Iterator<Entry<String, Object[]>> it = methods.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Object[]> entry = it.next();
				String key = entry.getKey();
				Object[] value = entry.getValue();
				// { m, des, ret, type, verify, count, parm, ann }
				int xx = 0;
				Method m = (Method) value[xx++];
				String des = (String) value[xx++];
				String ret = (String) value[xx++];
				byte type = (byte) value[xx++];
				boolean verify = (boolean) value[xx++];
				int count = (int) value[xx++];
				Parameter[] parm = (Parameter[]) value[xx++];

				JSONObject jm = new JSONObject();
				jm.put("node", node);
				jm.put("key", key);
				jm.put("des", des);
				jm.put("ret", ret);

				ArrayList<Object> ps = new ArrayList<>();

				for (int i = 0; i < count; i++) {
					Parameter p = parm[i];

					JSONObject item = new JSONObject();
					item.put("name", p.getName());
					item.put("type", p.getType().getSimpleName());

					item.put("name", p.getName());
					item.put("name", p.getName());

					P ppp = p.getAnnotation(P.class);
					if (ppp != null) {
						item.put("r", ppp.r());
						item.put("t", ppp.t());
					}
					ps.add(item);
				}
				jm.put("parms", ps);

				ms.add(jm);
			}
			retMap.put("methods", ms);
		}

		return retMap;
	}

	public String getJSCode() {

		StringBuffer sb = new StringBuffer();
		String ln = "\n";
		String ln2 = "\n\n";

		sb.append("\t\tNODE <").append(node).append(">  ").append(this.getClass().getName()).append(ln2);

		sb.append(ln).append(
				"\t##########################################################################################\t")
				.append(ln2);

		Iterator<Entry<String, Object[]>> iten = enumFields.entrySet().iterator();
		while (iten.hasNext()) {
			Entry<String, Object[]> entry = iten.next();
			String key = entry.getKey();
			Object[] value = entry.getValue();
			Field fff = (Field) value[0];
			try {
				Object[] objs = (Object[]) fff.get(this);
				sb.append("const ").append(fff.getType().getComponentType().getSimpleName()).append(" = {")
						.append(" // ").append(fff.getType().getComponentType().getName()).append(ln);
				for (int i = 0; i < objs.length; i++) {
					Object obj = objs[i];
					ENUMVALUE ev = (ENUMVALUE) obj;

					sb.append("\t").append(ev.toString()).append(":{v:\"").append(ev.v()).append("\",t:\"")
							.append(ev.txt()).append("\"}");
					sb.append(",").append(ln);
				}
				sb.append("};").append(ln2);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sb.append(ln).append(
				"\t##########################################################################################\t")
				.append(ln2);

		Iterator<Entry<String, Object[]>> it = methods.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object[]> entry = it.next();
			String key = entry.getKey();
			Object[] value = entry.getValue();
			// { m, des, ret, type, verify, count, parm, ann }
			int xx = 0;
			Method m = (Method) value[xx++];
			String des = (String) value[xx++];
			String ret = (String) value[xx++];
			byte type = (byte) value[xx++];
			boolean verify = (boolean) value[xx++];
			int count = (int) value[xx++];
			Parameter[] parm = (Parameter[]) value[xx++];

			sb.append(">>>>> ").append(key).append("\t<").append(des).append(">").append(ln);
			sb.append("\t\t\t---").append(ret).append("").append(ln2);

			// 示范
			sb.append("example:").append(ln2);
			sb.append("\tlet cnt = {").append(ln);
			if (count <= 0) {
			} else {
				for (int i = 0; i < count; i++) {
					Parameter p = parm[i];
					sb.append("\t\t").append(p.getName()).append(": ").append(p.getName()).append(", // ");

					sb.append(p.getType().getSimpleName()).append(" ");
					P ppp = p.getAnnotation(P.class);
					if (ppp != null) {
						if (ppp.r()) {
						} else {
							sb.append("<选填> ");
						}
						sb.append(ppp.t());
					}
					sb.append(ln);
				}
			}
			sb.append("\t};").append(ln);
			sb.append("\tutil.call('/").append(node).append("/").append(key).append("', cnt, callback);").append(ln);

			sb.append(ln).append(
					"\t------------------------------------------------------------------------------------------\t")
					.append(ln2);

		}

		return sb.toString();
	}

	public void getJSONDocs(StringBuffer sb) {
		String ln = "\n";
		String ln2 = "\n\n";

		Iterator<Entry<String, Object[]>> it = methods.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object[]> entry = it.next();
			String key = entry.getKey();
			Object[] value = entry.getValue();

			// { m, des, ret, type, verify, count, parm, ann }
			int xx = 0;
			Method m = (Method) value[xx++];
			String des = (String) value[xx++];
			String ret = (String) value[xx++];
			byte type = (byte) value[xx++];
			boolean verify = (boolean) value[xx++];
			int count = (int) value[xx++];
			Parameter[] parm = (Parameter[]) value[xx++];

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

			sb.append("\t").append(key).append("\t\t< ").append(temp).append(verify ? " -verify" : "").append(" >")
					.append(ln);
		}
	}

	private static JSONObject getReqContent(APIRequest req) throws ServerException {
		String content = req.c;
		if (StringUtils.isBlank(content)) {
			throw new ServerException(BaseRC.REQUEST_PARSE_ERROR, StringUtils.join("getRequestContent>", "is blank"));
		}
		try {
			return JSON.parseObject(content);
		} catch (ClassCastException cce) {
			throw new ServerException(BaseRC.REQUEST_PARSE_ERROR,
					StringUtils.join("getRequestContent>parse json>", cce.getMessage()));
		}
	}

	/**
	 * 获取对象，不可为空
	 */
	private static <T> T getObject(JSONObject jo, String key, Class<T> clazz) throws ServerException {
		try {
			return jo.getObject(key, clazz);
		} catch (ClassCastException cce) {
			throw new ServerException(BaseRC.REQUEST_PARSE_ERROR,
					StringUtils.join("getObjectNotNull>", key, ">", cce.getMessage()));
		}
	}

	/**
	 * 获取对象，可以为空
	 */
	private static <T> T getObjectCanNull(JSONObject jo, String key, Class<T> clazz) throws ServerException {
		try {
			return jo.getObject(key, clazz);
		} catch (ClassCastException cce) {
			return null;
		}
	}

	/**
	 * 获取JSON对象，不可为空
	 */
	private static JSONObject getJSONObject(JSONObject jo, String key) throws ServerException {
		return jo.getJSONObject(key);
	}

	/**
	 * 获取JSON对象，可以为空
	 */
	private static JSONObject getJSONObjectCanNull(JSONObject jo, String key) throws ServerException {
		try {
			return jo.getJSONObject(key);
		} catch (ClassCastException cce) {
			return null;
		}
	}

	/**
	 * 获取JSON数组，不可为空
	 */
	private static JSONArray getJSONArray(JSONObject jo, String key) throws ServerException {
		try {
			JSONArray array = jo.getJSONArray(key);
			if (null == array) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getArrayNotEmpty>", key, ">", "is null"));
			} else {
				return array;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getArrayNotEmpty>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取JSON数组，可以为空
	 */
	private static JSONArray getJSONArrayCanNull(JSONObject jo, String key) {
		try {
			JSONArray array = jo.getJSONArray(key);
			if (null == array) {
				return new JSONArray();
			} else {
				return array;
			}
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	/**
	 * 获取字符串，不可为空或blank
	 */
	private static String getString(JSONObject jo, String key) throws ServerException {
		String value = jo.getString(key);
		if (StringUtils.isBlank(value)) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getStringNotBlank>", key, ">", "is blank"));
		} else {
			return value;
		}
	}

	/**
	 * 获取字符串，可以为空
	 */
	private static String getStringCanNull(JSONObject jo, String key) {
		String value = jo.getString(key);
		if (StringUtils.isBlank(value)) {
			return null;
		} else {
			return value;
		}
	}

	/**
	 * 获取Double，不可为空
	 */
	private static Double getDouble(JSONObject jo, String key) throws ServerException {
		try {
			Double value = jo.getDouble(key);
			if (null == value) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getDoubleNotNull>", key, ">", "is null"));
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getDoubleNotNull>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取Double，可以为空
	 */
	private static Double getDoubleCanNull(JSONObject jo, String key) {
		return jo.getDouble(key);
	}

	/**
	 * 获取Long，不可为空
	 */
	private static Long getLong(JSONObject jo, String key) throws ServerException {
		try {
			Long value = jo.getLong(key);
			if (null == value) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getLongNotNull>", key, ">", "is null"));
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getLongNotNull>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取Long，可以为空
	 */
	private static Long getLongCanNull(JSONObject jo, String key) {
		return jo.getLong(key);
	}

	/**
	 * 获取Integer，不可为空
	 */
	private static Integer getInteger(JSONObject jo, String key) throws ServerException {
		try {
			Integer value = jo.getInteger(key);
			if (null == value) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getIntegerNotNull>", key, ">", "is null"));
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getIntegerNotNull>", key, ">", e.toString()));
		}
	}

	/**
	 * 获取Integer，可以为空
	 */
	private static Integer getIntegerCanNull(JSONObject jo, String key) {
		return jo.getInteger(key);
	}

	/**
	 * 获取Float，不可为空
	 */
	private static Float getFloat(JSONObject jo, String key) throws ServerException {
		try {
			Float value = jo.getFloat(key);
			if (null == value) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getFloatNotNull>", key, ">", "is null"));
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getFloatNotNull>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取Float，可以为空
	 */
	private static Float getFloatCanNull(JSONObject jo, String key) {
		return jo.getFloat(key);
	}

	/**
	 * 获取Byte，不可为空
	 */
	private static Byte getByte(JSONObject jo, String key) throws ServerException {
		try {
			Byte value = jo.getByte(key);
			if (null == value) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getByteNotNull>", key, ">", "is null"));
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getByteNotNull>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取Byte，可以为空
	 */
	private static Byte getByteCanNull(JSONObject jo, String key) {
		return jo.getByte(key);
	}

	/**
	 * 获取Boolean，不可为空
	 */
	private static Boolean getBoolean(JSONObject jo, String key) throws ServerException {
		try {
			Boolean value = jo.getBoolean(key);
			if (null == value) {
				throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
						StringUtils.join("getBooleanNotNull>", key, ">", "is null"));
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getBooleanNotNull>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取Boolean，可以为空
	 */
	private static Boolean getBooleanCanNull(JSONObject jo, String key) {
		return jo.getBoolean(key);
	}
}
