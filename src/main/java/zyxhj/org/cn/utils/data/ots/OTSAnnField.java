package zyxhj.org.cn.utils.data.ots;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OTS字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OTSAnnField {

	public enum ColumnType {
		BINARY, STRING, INTEGER, DOUBLE, BOOLEAN
	}

	/**
	 * OTS字段别名</br>
	 * OTS字段命名规则：跟Java相同，驼峰</br>
	 * 默认值为空，表示字段名与Java中的字段名一致
	 */
	public String alias() default "";

	public ColumnType column();

}