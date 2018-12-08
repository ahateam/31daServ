package zyxhj.utils.data.rds;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RDS字段注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RDSAnnField {

	public static final String BYTE = "TINYINT";
	public static final String SHORT = "SMALLINT";
	public static final String INTEGER = "INTEGER";
	public static final String LONG = "BIGINT";
	public static final String DOUBLE = "DOUBLE";
	public static final String FLOAT = "REAL";
	public static final String TIME = "TIMESTAMP";
	public static final String BOOLEAN = "BIT";
	public static final String LONGTEXT = "LONGTEXT";
	public static final String VARCHAR = "VARCHAR";

	public static final String ID = "BIGINT(20)";
	public static final String TEXT = "VARCHAR(10240)";// 10240汉字
	public static final String SHORT_TEXT = "VARCHAR(1024)";// 1024汉字

	public static final String TEXT_TITLE = "VARCHAR(128)";// 标题文字
	public static final String TEXT_NAME = "VARCHAR(64)";// 名称文字
	public static final String TEXT_PWD = "VARCHAR(64)";// 密码文字

	public static final String JSON = "JSON";

	/**
	 * 数据库字段别名</br>
	 * 数据库字段命名规则：小写，下划线分割</br>
	 * 默认值为空，表示字段名与数据库中的列名一致
	 */
	public String alias() default "";

	/**
	 * 字段类型描述，SQL语法</br>
	 * 如：VARCHAR(255)
	 */
	public String column();

}