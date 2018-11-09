package zyxhj.org.cn.utils.data.ots;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.Row;

import zyxhj.org.cn.utils.data.ots.OTSAnnID.KeyType;

public class OTSObjectMapper<T> {

	private Class<T> clazz;

	private String tableName;

	/**
	 * 主键列表，OTS有4个主键，第一个也是分片键</br>
	 * 构造时，已经严格按顺序排列，可直接取用
	 */
	private List<OTSFieldMapper<T>> primaryKeyList = new ArrayList<>();

	/**
	 * 字段列表，可能顺序是乱的
	 */
	private List<OTSFieldMapper<T>> columnList = new ArrayList<>();

	/**
	 * OTS第一个是主键（分片键），同时还允许有3个副键（索引键）。<br>
	 * OTS的4个索引，左闭右开。。。<br>
	 * 
	 * @param primaryKey
	 *            主键（分片键）
	 * @param otherKeys
	 *            副键（其它索引键）
	 */
	public OTSObjectMapper(Class<T> clazz) {
		this.clazz = clazz;

		OTSAnnEntity annEntity = clazz.getAnnotation(OTSAnnEntity.class);
		this.tableName = annEntity.alias();

		Object[] pks = new Object[4];
		int pkCount = 0;

		Field[] classFields = clazz.getFields();
		for (Field cf : classFields) {
			if (!Modifier.isStatic(cf.getModifiers())) {
				String fieldName = cf.getName();

				OTSAnnID annId = cf.getAnnotation(OTSAnnID.class);
				OTSAnnField annField = cf.getAnnotation(OTSAnnField.class);
				String fieldAlias = annField.alias();

				if (StringUtils.isBlank(fieldAlias)) {
					// 如果不存在别名，则默认按Java的驼峰命名规则
					fieldAlias = fieldName;
				}

				OTSFieldMapper<T> mapper = null;
				if (null != annId) {
					// ID列，按顺序填入数组中
					KeyType kt = annId.keyType();
					mapper = new OTSFieldMapper<T>(fieldName, fieldAlias, cf, kt);

					if (kt == OTSAnnID.KeyType.PARTITION_KEY) {
						pks[0] = mapper;
						pkCount = 1;
					} else if (kt == OTSAnnID.KeyType.PRIMARY_KEY_1) {
						pks[1] = mapper;
						pkCount = 2;
					} else if (kt == OTSAnnID.KeyType.PRIMARY_KEY_2) {
						pks[2] = mapper;
						pkCount = 3;
					} else if (kt == OTSAnnID.KeyType.PRIMARY_KEY_3) {
						pks[3] = mapper;
						pkCount = 4;
					}
				} else {
					// 普通列
					mapper = new OTSFieldMapper<T>(fieldName, fieldAlias, cf, null);
					columnList.add(mapper);
				}
			}
		}

		for (int i = 0; i < pkCount; i++) {
			primaryKeyList.add((OTSFieldMapper<T>) pks[i]);
		}
	}

	public String getTableName() {
		return tableName;
	}

	public List<T> deserialize(List<Row> rows) throws Exception {
		List<T> ret = new ArrayList<>();
		for (Row row : rows) {
			T t = deserialize(row);
			ret.add(t);
		}
		return ret;
	}

	public T deserialize(Row row) throws Exception {
		T t = clazz.newInstance();
		for (OTSFieldMapper<T> fieldMapper : primaryKeyList) {
			fieldMapper.setFieldValue(t, row);
		}
		for (OTSFieldMapper<T> fieldMapper : columnList) {
			fieldMapper.setFieldValue(t, row);
		}
		return t;
	}

	/**
	 * 从对象实例构造PrimaryKey</br>
	 * 
	 * @throws Exception
	 */
	public PrimaryKey getPrimaryKeyFromObject(T t) throws Exception {
		PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();

		// 因为在构造时，已经将PrimaryKey按顺序排好了，所以这里可以直接按数组里的顺序去取。
		for (OTSFieldMapper<T> pk : primaryKeyList) {
			primaryKeyBuilder.addPrimaryKeyColumn(pk.alias, (PrimaryKeyValue) pk.getFieldValueFromObject(t));
		}
		return primaryKeyBuilder.build();
	}

	/**
	 * 从对象实例构造Column列表
	 */
	public List<Column> getColumnListFromObject(T t) throws Exception {
		List<Column> ret = new ArrayList<>();
		for (OTSFieldMapper<T> c : columnList) {
			String cn = c.alias;
			ColumnValue cv = (ColumnValue) c.getFieldValueFromObject(t);
			ret.add(new Column(cn, cv));
		}
		return ret;
	}

	/**
	 * 从参数数组构造PrimaryKey，4个主键，第一个是分片键，严格按顺序排列</br>
	 * 需要按顺序添加PrimaryKey
	 */
	public PrimaryKey getPrimaryKeyFromParams(Object... params) throws Exception {
		PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
		// 因为在构造时，已经将PrimaryKey按顺序排好了，所以这里可以直接按数组里的顺序去取。
		for (int i = 0; i < primaryKeyList.size(); i++) {
			OTSFieldMapper<T> pk = primaryKeyList.get(i);
			primaryKeyBuilder.addPrimaryKeyColumn(pk.alias, (PrimaryKeyValue) pk.getFieldValueFromParam(params[i]));
		}
		return primaryKeyBuilder.build();
	}

}
