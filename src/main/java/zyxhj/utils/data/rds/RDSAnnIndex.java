package zyxhj.utils.data.rds;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RDS索引注解
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RDSAnnIndex {

	public static final String NORMAL = "INDEX";
	public static final String UNIQUE = "UNIQUE INDEX";// 唯一性

	// public static final String FULLTEXT = "FULLTEXT";// 唯一性
	// 目前RDS的全文索引尝试失败，将来还是依赖opensearch等工具做比较靠谱。目前使用select like先解决

	public String type() default NORMAL;
}