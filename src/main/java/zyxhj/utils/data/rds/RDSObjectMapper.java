package zyxhj.utils.data.rds;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

/**
 * RDS对象映射器
 */
public class RDSObjectMapper {

	private String tableName;

	private Map<String, RDSFieldMapper> fieldMapperMap = new HashMap<>();

	public RDSObjectMapper(Class<?> clazz) {
		RDSAnnEntity annEntity = clazz.getAnnotation(RDSAnnEntity.class);
		this.tableName = annEntity.alias();

		Field[] classFields = clazz.getFields();
		for (Field cf : classFields) {
			if (!Modifier.isStatic(cf.getModifiers())) {
				String fieldName = cf.getName();

				RDSAnnID annId = cf.getAnnotation(RDSAnnID.class);
				RDSAnnField annField = cf.getAnnotation(RDSAnnField.class);
				String fieldAlias = annField.alias();

				if (StringUtils.isBlank(fieldAlias)) {
					// 如果不存在别名，则默认按非驼峰的全小写，下划线分割单词的命名规则
					fieldAlias = RDSUtils.underscoreName(fieldName);
				}

				RDSFieldMapper mapper = null;
				if (null != annId) {
					// ID列
					mapper = new RDSFieldMapper(fieldName, fieldAlias, cf, true);
				} else {
					// 普通列
					mapper = new RDSFieldMapper(fieldName, fieldAlias, cf, false);
				}

				fieldMapperMap.put(mapper.alias, mapper);
			}
		}
	}

	public String getTableName() {
		return tableName;
	}

	public RDSFieldMapper getFieldMapperByAlias(String alias) {
		return fieldMapperMap.get(alias);
	}

	public <T> List<T> deserialize(ResultSet rs, Class<T> clazz) throws Exception {
		List<T> ret = new ArrayList<>();
		// ResultSet不是标准set，所以不能用stream接口
		while (rs.next()) {
			T t = clazz.newInstance();

			Iterator<Entry<String, RDSFieldMapper>> it = fieldMapperMap.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, RDSFieldMapper> entry = it.next();
				RDSFieldMapper mapper = entry.getValue();
				mapper.setFieldValue(t, rs);
			}
			ret.add(t);
		}
		return ret;
	}

	public Map<String, Object> serialize(Object t) throws Exception {
		Map<String, Object> ret = new HashMap<>();

		Iterator<Entry<String, RDSFieldMapper>> it = fieldMapperMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, RDSFieldMapper> entry = it.next();
			RDSFieldMapper mapper = entry.getValue();
			Object value = mapper.getFieldValue(t);
			ret.put(mapper.alias, value);
		}
		return ret;
	}
}
