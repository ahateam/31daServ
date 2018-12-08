package zyxhj.utils.data.rds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.utils.data.DataSource;

public class RDSUtils {

	/**
	 * mapper缓存
	 */
	private static HashMap<String, RDSObjectMapper> objectMapperMap = new HashMap<>();

	public static void entity2map(Class clazz) {

		String className = clazz.getName();
		System.out.println(className);
		RDSObjectMapper om = objectMapperMap.get(className);
		if (null == om) {
			// RDSObjectMapper om = new RDSObjectMapper(clazz);
			// objectMapperMap.put(className, om);
		}

	}

	public static void map2entity() {

	}

	public static void createTableByEntity(DataSource ds, Class entityClass) {
		try {
			createTable(ds, entityClass);
			createIndex(ds, entityClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void dropTableByEntity(DataSource ds, Class entityClass) {
		try {
			dropTable(ds, entityClass);
			// 表没了，索引自然就没了
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void dropTable(DataSource ds, Class entityClass) throws Exception {
		RDSAnnEntity annEntity = (RDSAnnEntity) entityClass.getAnnotation(RDSAnnEntity.class);

		if (null == annEntity) {
			// 没有注解，建表错误
			System.out.println("类缺少Entity注解");
			return;
		} else {
			String tableName = annEntity.alias();
			StringBuffer sql = new StringBuffer();
			sql.append("DROP TABLE IF EXISTS `").append(tableName).append("`;\n");

			System.out.println(sql.toString());

			try (DruidPooledConnection conn = (DruidPooledConnection) ds.openConnection()) {
				Statement stmt = conn.createStatement();

				stmt.executeUpdate(sql.toString());
				System.out.println("--------DROP TABLE SUCCESS");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void createTable(DataSource ds, Class entityClass) throws Exception {

		RDSAnnEntity annEntity = (RDSAnnEntity) entityClass.getAnnotation(RDSAnnEntity.class);

		if (null == annEntity) {
			// 没有注解，建表错误
			System.out.println("类缺少Entity注解");
			return;
		} else {
			String tableName = annEntity.alias();

			StringBuffer sql = new StringBuffer();

			sql.append("CREATE TABLE `").append(tableName).append("` (\n");

			Field[] classFields = entityClass.getFields();

			List<String> primaryKeys = new ArrayList<>();
			List<String> fullTextIndexs = new ArrayList<>();

			for (Field classField : classFields) {
				if (!Modifier.isStatic(classField.getModifiers())) {
					RDSAnnID annId = classField.getAnnotation(RDSAnnID.class);

					RDSAnnIndex annIndex = classField.getAnnotation(RDSAnnIndex.class);

					zyxhj.utils.data.rds.RDSAnnField annField = classField
							.getAnnotation(zyxhj.utils.data.rds.RDSAnnField.class);
					String fieldName = classField.getName();
					String fieldAlias = annField.alias();
					if (StringUtils.isBlank(fieldAlias)) {
						// 如果数据库字段别名为空，则表示按数据库规则去驼峰，加下划线，全小写命名规则
						fieldAlias = underscoreName(fieldName);
					}
					String fieldColumn = annField.column();

					sql.append("`").append(fieldAlias).append("` ").append(fieldColumn);

					if (null != annId) {
						// ID列，添加到数组中，最后再做PrimaryKey设置
						primaryKeys.add(fieldAlias);

						sql.append(" NOT NULL");
					}

					// if (null != annIndex && annIndex.type().equals(RDSAnnIndex.FULLTEXT)) {
					// // 全文索引
					// fullTextIndexs.add(fieldAlias);
					// }

					sql.append(",\n");
				}
			}

			if (!primaryKeys.isEmpty()) {
				// 处理主键
				// PRIMARY_KEY(column1,column2,..)
				sql.append("PRIMARY KEY (");
				for (String pk : primaryKeys) {
					sql.append('`').append(pk).append("`,");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(")");
			}

			if (!fullTextIndexs.isEmpty()) {
				// 处理全文索引
				// FULLTEXT (column1,column2,..)
				sql.append(",\n");
				sql.append("FULLTEXT (");
				for (String fti : fullTextIndexs) {
					sql.append('`').append(fti).append("`,");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(")");
			}

			sql.append("\n");
			sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n");

			System.out.println(sql.toString());

			try (DruidPooledConnection conn = (DruidPooledConnection) ds.openConnection()) {
				Statement stmt = conn.createStatement();

				stmt.executeUpdate(sql.toString());
				System.out.println("--------CREATE TABLE SUCCESS");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void createIndex(DataSource ds, Class entityClass) throws Exception {
		RDSAnnEntity annEntity = (RDSAnnEntity) entityClass.getAnnotation(RDSAnnEntity.class);

		if (null == annEntity) {
			// 没有注解，建表错误
			System.out.println("类缺少Entity注解");
			return;
		} else {
			Field[] classFields = entityClass.getFields();
			for (Field classField : classFields) {
				if (!Modifier.isStatic(classField.getModifiers())) {

					RDSAnnID annId = classField.getAnnotation(RDSAnnID.class);
					if (null != annId) {
						// ID列自建索引，我们不管，跳过
						continue;
					} else {
						// 不是ID列
						RDSAnnIndex annIndex = classField.getAnnotation(RDSAnnIndex.class);

						// 全文索引在建表时已经创建，因此不在此处再调用命令创建FULLTEXT索引
						if (null != annIndex
						// && !annIndex.type().equals(RDSAnnIndex.FULLTEXT)
						) {
							// 有索引注解，开始建索引
							String tableName = annEntity.alias();
							String fieldName = underscoreName(classField.getName());

							// 索引名，表名_列名
							String indexName = StringUtils.join(tableName, "_", fieldName);

							String indexType = annIndex.type();
							StringBuffer sql = new StringBuffer();
							sql.append("CREATE ").append(indexType).append(" `");
							sql.append(indexName).append("` ON ").append(tableName).append("(").append(fieldName)
									.append(")");

							System.out.println(sql);

							try (DruidPooledConnection conn = (DruidPooledConnection) ds.openConnection()) {
								Statement stmt = conn.createStatement();

								stmt.executeUpdate(sql.toString());
								System.out.println("--------CREATE INDEX SUCCESS");
							} catch (Exception e) {
								e.printStackTrace();
							}

						} else {
							// 没有索引注解，跳过
							continue;
						}
					}
				}
			}

		}
	}

	public static String underscoreName(String name) {
		StringBuilder result = new StringBuilder();
		if (name != null && name.length() > 0) {
			// 将第一个字符处理成大写
			result.append(name.substring(0, 1).toLowerCase());
			// 循环处理其余字符
			for (int i = 1; i < name.length(); i++) {
				String s = name.substring(i, i + 1);
				// 在大写字母前添加下划线
				if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
					result.append("_");
				}
				// 其他字符直接转成大写
				result.append(s.toLowerCase());
			}
		}
		return result.toString();
	}

	public static String camelName(String name) {
		StringBuilder result = new StringBuilder();
		// 快速检查
		if (name == null || name.isEmpty()) {
			// 没必要转换
			return "";
		} else if (!name.contains("_")) {
			// 不含下划线，仅将首字母小写
			return name.substring(0, 1).toLowerCase() + name.substring(1);
		}
		// 用下划线将原始字符串分割
		String camels[] = name.split("_");
		for (String camel : camels) {
			// 跳过原始字符串中开头、结尾的下换线或双重下划线
			if (camel.isEmpty()) {
				continue;
			}
			// 处理真正的驼峰片段
			if (result.length() == 0) {
				// 第一个驼峰片段，全部字母都小写
				result.append(camel.toLowerCase());
			} else {
				// 其他的驼峰片段，首字母大写
				result.append(camel.substring(0, 1).toUpperCase());
				result.append(camel.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	public static List<Object> getDomain(Object obj) throws Exception {
		// 存储对象对应变量名
		List<String> key = new ArrayList<String>();
		// 存储对象对应变量值
		List<Object> vals = new ArrayList<Object>();
		// 存储最终返回变量名与变量值数组
		List<Object> all = new ArrayList<Object>();
		// 获取对象变量
		Field[] fields = obj.getClass().getDeclaredFields();
		// 循环判断并获取对象变量名与变量值
		for (Field f : fields) {
			if (f.getGenericType().toString().equals("class java.lang.String")) {
				if (f.get(obj) != null && !f.get(obj).equals("")) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((String) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Long")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Long) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Integer")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Integer) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Double")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Double) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.util.Date")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Date) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Byte")) {
				if (f.get(obj) != null && !f.get(obj).equals("")) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Byte) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Short")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Short) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Float")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Float) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Boolean")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Boolean) f.get(obj));
				}
			} else if (f.getGenericType().toString().equals("class java.lang.Boolean")) {
				if (f.get(obj) != null) {
					key.add(RDSUtils.underscoreName(f.getName()));
					vals.add((Boolean) f.get(obj));
				}
			}
		}
		// 变量名集合转换数组
		String[] keys = new String[key.size()];
		key.toArray(keys);
		// 变量值集合转换数组
		Object[] values = new Object[vals.size()];
		vals.toArray(values);
		all.add(keys);
		all.add(values);
		return all;
	}

}
