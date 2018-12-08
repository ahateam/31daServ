package zyxhj.utils.data.rds;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RDS实体类注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RDSAnnEntity {

	/**
	 * 表别名，用于映射数据库的字段名</br>
	 * RDS是按数据库规则：小写，下划线分割（加tb前缀，如：tb_user）</br>
	 */
	public String alias();
}