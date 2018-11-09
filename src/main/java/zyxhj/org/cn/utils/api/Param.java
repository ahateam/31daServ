package zyxhj.org.cn.utils.api;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Param {

	public static JSONObject getReqContent(APIRequest req) throws ServerException {
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

	public static Object checkNull(Object obj) throws ServerException {
		if (null == obj) {
			throw new ServerException(BaseRC.SERVER_OBJECT_NULL);
		} else {
			return obj;
		}
	}

	/**
	 * 获取Long型SimpleID参数（JS端ID用字符串，服务端ID用Long型）
	 */
	public static Long getSimpleId(JSONObject jo, String key) throws ServerException {
		try {
			Long value = Long.parseLong(jo.getString(key));
			return value;
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getSimpleId>", key, ">", e.getMessage()));
		}
	}

	/**
	 * 获取对象
	 */
	public static <T> T getObject(JSONObject jo, String key, Class<T> clazz) throws ServerException {
		try {
			return jo.getObject(key, clazz);
		} catch (ClassCastException cce) {
			throw new ServerException(BaseRC.REQUEST_PARSE_ERROR,
					StringUtils.join("getObjectNotNull>", key, ">", cce.getMessage()));
		}
	}

	/**
	 * 获取对象，可以为空，有问题则返回null
	 */
	public static <T> T getObjectCanNull(JSONObject jo, String key, Class<T> clazz) throws ServerException {
		try {
			return jo.getObject(key, clazz);
		} catch (ClassCastException cce) {
			return null;
		}
	}

	/**
	 * 获取JSON数组参数，不可为空，否则抛出异常
	 */
	public static JSONArray getArray(JSONObject jo, String key) throws ServerException {
		try {
			JSONArray array = jo.getJSONArray(key);
			if (null == array || array.size() <= 0) {
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
	 * 获取JSON数组参数，如果或者有问题则返回空数组
	 */
	public static JSONArray getArrayCanEmpty(JSONObject jo, String key) {
		try {
			JSONArray array = jo.getJSONArray(key);
			if (null == array || array.size() <= 0) {
				return new JSONArray();
			} else {
				return array;
			}
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	/**
	 * 获取字符串参数，不可为空或blank，否则抛出异常
	 */
	public static String getString(JSONObject jo, String key) throws ServerException {
		String value = jo.getString(key);
		if (StringUtils.isBlank(value)) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getStringNotBlank>", key, ">", "is blank"));
		} else {
			return value;
		}
	}

	/**
	 * 获取字符串参数，可以为空，空则返回默认值defaultValue
	 */
	public static String getStringDFLT(JSONObject jo, String key, String defaultValue) {
		String value = jo.getString(key);
		if (StringUtils.isBlank(value)) {
			return defaultValue;
		} else {
			return value;
		}
	}

	/**
	 * 获取Double参数，不可为空，否则抛出异常
	 */
	public static Double getDouble(JSONObject jo, String key) throws ServerException {
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
	 * 获取Double参数，如果空则使用默认值
	 */
	public static Double getDoubleDFLT(JSONObject jo, String key, Double d) {
		try {
			Double value = jo.getDouble(key);
			if (null == value) {
				if (null == d) {
					return null;
				} else {
					return new Double(d);// 重新构造，避免引用
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			return new Double(d);// 重新构造，避免引用
		}
	}

	/**
	 * 获取Long参数，不可为空，否则抛出异常
	 */
	public static Long getLong(JSONObject jo, String key) throws ServerException {
		Long value;
		try {
			value = jo.getLong(key);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getLongNotNull>", key, ">", e.getMessage()));
		}
		if (null == value) {
			throw new ServerException(BaseRC.REQUEST_PARAMS_ERROR,
					StringUtils.join("getLongNotNull>", key, ">", "is null"));
		} else {
			return value;
		}
	}

	/**
	 * 获取Long参数，如果空则使用默认值
	 */
	public static Long getLongDFLT(JSONObject jo, String key, Long d) {
		try {
			Long value = jo.getLong(key);
			if (null == value) {
				if (null == d) {
					return null;
				} else {
					return new Long(d);// 重新构造，避免引用
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			return new Long(d);// 重新构造，避免引用
		}
	}

	/**
	 * 获取Integer参数，不可为空，否则抛出异常
	 */
	public static Integer getInteger(JSONObject jo, String key) throws ServerException {
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
	 * 获取Integer参数，如果空则使用默认值
	 */
	public static Integer getIntegerDFLT(JSONObject jo, String key, Integer d) {
		try {
			Integer value = jo.getInteger(key);
			if (null == value) {
				if (null == d) {
					return null;
				} else {
					return new Integer(d);// 重新构造，避免引用
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			return new Integer(d);// 重新构造，避免引用
		}
	}

	/**
	 * 获取Float参数，不可为空，否则抛出异常
	 */
	public static Float getFloat(JSONObject jo, String key) throws ServerException {
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
	 * 获取Float参数，如果空则使用默认值
	 */
	public static Float getFloatDFLT(JSONObject jo, String key, Float d) {
		try {
			Float value = jo.getFloat(key);
			if (null == value) {
				if (null == d) {
					return null;
				} else {
					return new Float(d);// 重新构造，避免引用
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			return new Float(d);// 重新构造，避免引用
		}
	}

	/**
	 * 获取Byte参数，不可为空，否则抛出异常
	 */
	public static Byte getByte(JSONObject jo, String key) throws ServerException {
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
	 * 获取Byte参数，如果空则使用默认值
	 */
	public static Byte getByteDFLT(JSONObject jo, String key, Byte d) {
		try {
			Byte value = jo.getByte(key);
			if (null == value) {
				if (null == d) {
					return null;
				} else {
					return new Byte(d);// 重新构造，避免引用
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			return new Byte(d);// 重新构造，避免引用
		}
	}

	/**
	 * 获取Boolean参数，不可为空，否则抛出异常
	 */
	public static Boolean getBoolean(JSONObject jo, String key) throws ServerException {
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
	 * 获取Boolean参数，如果空则使用默认值
	 */
	public static Boolean getBooleanDFLT(JSONObject jo, String key, Boolean d) {
		try {
			Boolean value = jo.getBoolean(key);
			if (null == value) {
				if (null == d) {
					return null;
				} else {
					return new Boolean(d);// 重新构造，避免引用
				}
			} else {
				return value;
			}
		} catch (Exception e) {
			return new Boolean(d);// 重新构造，避免引用
		}
	}

}
