package zyxhj.org.cn.utils.data.ots;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;

import zyxhj.org.cn.utils.data.ots.OTSAnnID.KeyType;

public class OTSFieldMapper<T> {

	/**
	 * 字段名（java对象中的名称）
	 */
	protected String name;

	/**
	 * 字段别名（数据库中的名称）
	 */
	protected String alias;

	/**
	 * 字段对象，用于反射操作该字段
	 */
	protected Field field;

	/**
	 * 类对象，用户反射操作该对象
	 */
	protected Class<?> javaType;

	/**
	 * 主键类型，如果为空则表示不是主键
	 */
	protected KeyType primaryKeyType;

	public OTSFieldMapper(String name, String alias, Field field, KeyType primaryKeyType) {
		this.name = name;
		this.alias = alias;
		this.field = field;
		this.javaType = field.getType();
		this.primaryKeyType = primaryKeyType;
	}

	public Object getFieldValueFromParam(Object param) throws Exception {

		if (null != primaryKeyType) {
			if (javaType.equals(Long.class)) {
				return PrimaryKeyValue.fromLong((Long) param);
			} else if (javaType.equals(String.class)) {
				return PrimaryKeyValue.fromString((String) param);
			} else if (javaType.equals(Integer.class)) {
				// ots实际上用的是Integer
				return PrimaryKeyValue.fromLong((Integer) param);
			} else {
				throw new Exception(StringUtils.join("unknown ots type:", javaType));
			}
		} else {
			if (javaType.equals(Boolean.class)) {
				return ColumnValue.fromBoolean((Boolean) param);
			} else if (javaType.equals(Long.class)) {
				return ColumnValue.fromLong((Long) param);
			} else if (javaType.equals(Double.class)) {
				return ColumnValue.fromDouble((Double) param);
			} else if (javaType.equals(String.class)) {
				return ColumnValue.fromString((String) param);
			} else if (javaType.equals(Integer.class)) {
				return ColumnValue.fromLong((Integer) param);
			} else {
				throw new Exception(StringUtils.join("unknown ots type:", javaType));
			}
		}
	}

	public Object getFieldValueFromObject(Object obj) throws Exception {
		Object t = field.get(obj);
		if (null == t) {
			return null;
		}
		if (null != primaryKeyType) {
			if (javaType.equals(Long.class)) {
				return PrimaryKeyValue.fromLong((Long) t);
			} else if (javaType.equals(String.class)) {
				return PrimaryKeyValue.fromString((String) t);
			} else if (javaType.equals(Integer.class)) {
				// ots实际上用的是Integer
				return PrimaryKeyValue.fromLong((Integer) t);
			} else {
				throw new Exception(StringUtils.join("unknown ots type:", javaType));
			}
		} else {
			if (javaType.equals(Boolean.class)) {
				return ColumnValue.fromBoolean((Boolean) t);
			} else if (javaType.equals(Long.class)) {
				return ColumnValue.fromLong((Long) t);
			} else if (javaType.equals(Double.class)) {
				return ColumnValue.fromDouble((Double) t);
			} else if (javaType.equals(String.class)) {
				return ColumnValue.fromString((String) t);
			} else if (javaType.equals(Integer.class)) {
				return ColumnValue.fromLong((Integer) t);
			} else {
				throw new Exception(StringUtils.join("unknown ots type:", javaType));
			}
		}
	}

	public void setFieldValue(Object obj, Row row) throws Exception {
		if (null != primaryKeyType) {
			// 如果是主键
			PrimaryKey pk = row.getPrimaryKey();
			PrimaryKeyValue pkv = pk.getPrimaryKeyColumn(alias).getValue();
			if (null != pkv) {
				if (javaType.equals(Long.class)) {
					field.set(obj, pkv.asLong());
				} else if (javaType.equals(String.class)) {
					field.set(obj, pkv.asString());
				} else if (javaType.equals(Integer.class)) {
					field.set(obj, (int) pkv.asLong());
				} else {
					throw new Exception(StringUtils.join("ots unsupported data type:", javaType.toString()));
				}
			}
		} else {
			// 不是主键，只是普通列
			ColumnValue cv = row.getLatestColumn(alias).getValue();
			if (null != cv) {
				if (javaType.equals(Boolean.class)) {
					field.set(obj, cv.asBoolean());
				} else if (javaType.equals(Long.class)) {
					field.set(obj, cv.asLong());
				} else if (javaType.equals(Double.class)) {
					field.set(obj, cv.asDouble());
				} else if (javaType.equals(String.class)) {
					field.set(obj, cv.asString());
				} else if (javaType.equals(Integer.class)) {
					field.set(obj, (int) cv.asLong());
				} else {
					throw new Exception(StringUtils.join("ots unsupported data type:", javaType.toString()));
				}
			}
		}
	}

}
