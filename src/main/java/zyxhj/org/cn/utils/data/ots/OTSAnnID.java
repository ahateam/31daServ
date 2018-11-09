package zyxhj.org.cn.utils.data.ots;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * OTS字段，主键列注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OTSAnnID {

	public enum KeyType {
		PARTITION_KEY, PRIMARY_KEY_1, PRIMARY_KEY_2, PRIMARY_KEY_3
	}

	/**
	 * 主键类型（分片键，1，2，3主键）
	 */
	public KeyType keyType();

}