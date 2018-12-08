package zyxhj.utils.data.rds;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Date;

/**
 * RDS字段映射器
 */
public class RDSFieldMapper {

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
	 * 是否主键
	 */
	protected boolean isPrimaryKey;

	protected RDSFieldMapper(String fieldName, String fieldAlias, Field field, boolean isPrimaryKey) {
		this.name = fieldName;
		this.alias = fieldAlias;
		this.field = field;
		this.javaType = field.getType();
		this.isPrimaryKey = isPrimaryKey;
	}

	protected Object getFieldValue(Object obj) throws Exception {
		return field.get(obj);
	}

	protected void setFieldValue(Object obj, ResultSet rs) throws Exception {
		if (javaType.equals(Boolean.class)) {
			field.set(obj, rs.getBoolean(alias));
		} else if (javaType.equals(Byte.class)) {
			field.set(obj, rs.getByte(alias));
		} else if (javaType.equals(Short.class)) {
			field.set(obj, rs.getShort(alias));
		} else if (javaType.equals(Integer.class)) {
			field.set(obj, rs.getInt(alias));
		} else if (javaType.equals(Long.class)) {
			field.set(obj, rs.getLong(alias));
		} else if (javaType.equals(Float.class)) {
			field.set(obj, rs.getFloat(alias));
		} else if (javaType.equals(Double.class)) {
			field.set(obj, rs.getDouble(alias));
		} else if (javaType.equals(String.class)) {
			field.set(obj, rs.getString(alias));
		} else if (javaType.equals(Date.class)) {
			// RecordSet中，getDate只获取日期部分，getTime只获取时间部分
			field.set(obj, rs.getTimestamp(alias));
		} else {
			field.set(obj, rs.getObject(alias));
		}
	}

}
