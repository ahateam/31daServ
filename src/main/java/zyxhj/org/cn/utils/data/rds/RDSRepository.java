package zyxhj.org.cn.utils.data.rds;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import zyxhj.org.cn.utils.api.BaseRC;
import zyxhj.org.cn.utils.api.ServerException;

public abstract class RDSRepository<T> {

	private static final String BLANK = " ";

	private RDSObjectMapper mapper;

	private Class clazz;

	protected static PreparedStatement prepareStatement(Connection conn, String sql, Object[] params)
			throws ServerException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);

			if (null != params && params.length > 0) {
				int ind = 1;
				for (Object p : params) {
					if (p instanceof Boolean) {
						ps.setBoolean(ind++, (Boolean) p);
					} else if (p instanceof Short) {
						ps.setShort(ind++, (Short) p);
					} else if (p instanceof Integer) {
						ps.setInt(ind++, (Integer) p);
					} else if (p instanceof Long) {
						ps.setLong(ind++, (Long) p);
					} else if (p instanceof Float) {
						ps.setFloat(ind++, (Float) p);
					} else if (p instanceof Double) {
						ps.setDouble(ind++, (Double) p);
					} else if (p instanceof String) {
						ps.setString(ind++, (String) p);
					} else if (p instanceof Date) {
						ps.setDate(ind++, (Date) p);
					} else {
						ps.setObject(ind++, p);
					}
				}
			}
		} catch (SQLException e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, e.getMessage());
		}
		return ps;
	}

	private void checkCountAndOffset(Integer count, Integer offset) throws ServerException {
		if (null != count || null != offset) {
			if (count < 1 || count > 512) {
				throw new ServerException(BaseRC.REPOSITORY_COUNT_OFFSET_ERROR, "count < 1 or count > 512");
			}
			if (offset < 0) {
				throw new ServerException(BaseRC.REPOSITORY_COUNT_OFFSET_ERROR, "offset < 0");
			}
		}
	}

	protected RDSRepository(Class<T> clazz) {
		this.clazz = clazz;
		this.mapper = new RDSObjectMapper(clazz);
	}

	/**
	 * 模版方法，获取一个对象</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * 
	 * @return 返回查询的对象，如果查询不到，则返回null
	 */
	protected T get(DruidPooledConnection conn, String where, Object[] whereParams) throws ServerException {
		List<T> list = getList(conn, where, whereParams, 1, 0);
		if (list.size() <= 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	/**
	 * 模版方法，获取对象列表</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param count
	 *            查询的总数量
	 * @param offset
	 *            查询的起始位置，下标从零开始（0表示从第一个开始查询）
	 * 
	 * @return 返回查询的对象列表，如果查询不到，则返回空数组
	 */
	protected List<T> getList(DruidPooledConnection conn, String where, Object[] whereParams, Integer count,
			Integer offset) throws ServerException {

		checkCountAndOffset(count, offset);

		// 有oracle兼容性问题，不支持limit和offset
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(mapper.getTableName());
		if (StringUtils.isNotBlank(where)) {
			sql.append(BLANK).append(where);
		} else {
			// 查询操作 where可以为空
		}

		if (count != null) {
			sql.append(" LIMIT ").append(count);
		}
		if (offset != null) {
			sql.append(" OFFSET ").append(offset);
		}
		// System.out.println(sql.toString());
		PreparedStatement ps = prepareStatement(conn, sql.toString(), whereParams);
		try {
			ResultSet rs = ps.executeQuery();
			return mapper.deserialize(rs, clazz);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 模版方法，获取数量</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * 
	 * @return 返回数量
	 */
	protected int count(DruidPooledConnection conn, String where, Object[] whereParams) throws ServerException {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ").append(mapper.getTableName());
		if (StringUtils.isNotBlank(where)) {
			sql.append(BLANK).append(where);
		} else {
			// 查询操作 where可以为空
		}
		PreparedStatement ps = prepareStatement(conn, sql.toString(), whereParams);

		try {
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 模版方法，删除对象</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * 
	 * @return 返回影响的记录数
	 */
	protected int delete(DruidPooledConnection conn, String where, Object[] whereParams) throws ServerException {
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(mapper.getTableName());
		if (StringUtils.isNotBlank(where)) {
			sql.append(BLANK).append(where);
		} else {
			// 删除操作 where不能为空
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "where not null");
		}
		PreparedStatement ps = prepareStatement(conn, sql.toString(), whereParams);
		try {
			int count = ps.executeUpdate();
			if (count <= 0) {
				throw new ServerException(BaseRC.REPOSITORY_DELETE_ERROR, "nothing changed");
			} else {
				return count;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 模版方法，更新</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param set
	 *            SQL的SET从句字符串
	 * @param setParams
	 *            SET从句的参数
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * 
	 * @return 返回影响的记录数
	 */
	protected int update(DruidPooledConnection conn, String set, Object[] setParams, String where, Object[] whereParams)
			throws ServerException {
		StringBuilder sql = new StringBuilder("UPDATE ").append(mapper.getTableName());
		if (StringUtils.isNotBlank(set)) {
			sql.append(BLANK).append(set);
		} else {
			// 更新操作 set不能为空
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "set not null");
		}
		if (StringUtils.isNotBlank(where)) {
			sql.append(BLANK).append(where);
		} else {
			// 更新操作 where不能为空
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "where not null");
		}

		int totalCount = setParams.length + whereParams.length;
		Object[] total = new Object[totalCount];
		System.arraycopy(setParams, 0, total, 0, setParams.length);
		System.arraycopy(whereParams, 0, total, setParams.length, whereParams.length);
		System.out.println(sql);
		PreparedStatement ps = prepareStatement(conn, sql.toString(), total);
		int count = 0;
		try {
			count = ps.executeUpdate();
			return count;
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			if (count <= 0) {
				throw new ServerException(BaseRC.REPOSITORY_UPDATE_ERROR, "nothing changed");
			}
			try {
				ps.close();
			} catch (Exception e) {
			}
		}

	}

	/**
	 * 模版方法，更新对象</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param where
	 *            SQL的WHERE从句字符串
	 * @param whereParams
	 *            WHERE从句的参数
	 * @param skipNull
	 *            是否跳过空字段
	 * 
	 * @return 返回影响的记录数
	 */
	protected int update(DruidPooledConnection conn, String where, Object[] whereParams, T t, boolean skipNull)
			throws ServerException {
		StringBuilder set = new StringBuilder("SET ");
		Map<String, Object> map;
		List<Object> values = new ArrayList<>();
		try {
			map = mapper.serialize(t);

			map.forEach((k, v) -> {
				// 跳过id列，不参与更新
				if (!mapper.getFieldMapperByAlias(k).isPrimaryKey) {
					// 如果为null，并且skipNull为true，则不参与更新
					if (skipNull) {
						if (null != v) {
							set.append(k).append("=?,");
							values.add(v);
						}
					} else {
						set.append(k).append("=?,");
						values.add(v);
					}
				}
			});
			set.deleteCharAt(set.length() - 1);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR);
		}

		return update(conn, set.toString(), values.toArray(), where, whereParams);
	}

	protected JSONArray getTags(DruidPooledConnection conn, String tagColumnName, Long objId, String tagKey)
			throws ServerException {
		// SELECT tags->'$.k3' FROM tb_content WHERE id=?
		StringBuffer sb = new StringBuffer();
		sb.append(tagColumnName).append("->'$.").append(tagKey).append("'");
		String result = this.getColumnString(conn, sb.toString(), "WHERE id=?", new Object[] { objId });
		JSONArray tags = JSON.parseArray(result);
		return tags;
	}

	/**
	 * 由于MYSQL的JSON支持，数组操作的api有些简陋，因此仍然选择读取出来再整体覆盖的办法</br>
	 * 但是，查询和检索仍然能够得到不小的提高
	 */
	protected void addTags(DruidPooledConnection conn, String tagColumnName, Long objId, String tagKey, JSONArray tags)
			throws ServerException {
		// UPDATE `tb_content` SET tags=JSON_SET(tags, '$.k2',JSON_ARRAY("t4","t5"))
		// WHERE id=386953055161814;

		if (null == tags || tags.isEmpty()) {
			// 目标标签为空，直接不修改，退出
			return;
		} else {
			JSONArray oldTags = getTags(conn, tagColumnName, objId, tagKey);
			JSONArray newTags = mergeTags(oldTags, tags);

			StringBuffer sb = new StringBuffer();
			sb.append("SET ").append(tagColumnName).append("=JSON_SET(").append(tagColumnName).append(",'$.")
					.append(tagKey);
			sb.append("',JSON_ARRAY(");
			for (Object tag : newTags) {
				sb.append("\"").append(tag).append("\",");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("))");
			this.update(conn, sb.toString(), new Object[] {}, "WHERE id=?", new Object[] { objId });
		}
	}

	/**
	 * 由于MYSQL的JSON支持，数组操作的api有些简陋，因此仍然选择读取出来再整体覆盖的办法</br>
	 * 但是，查询和检索仍然能够得到不小的提高
	 */
	protected void removeTags(DruidPooledConnection conn, String tagColumnName, Long objId, String tagKey,
			JSONArray tags) throws ServerException {
		// SQL语句跟add相同，都是设置
		// UPDATE `tb_content` SET tags=JSON_SET(tags, '$.k2',JSON_ARRAY("t4","t5"))
		// WHERE id=386953055161814;

		if (null == tags || tags.isEmpty()) {
			// 目标标签为空，直接不修改，退出
			return;
		} else {
			JSONArray oldTags = getTags(conn, tagColumnName, objId, tagKey);
			if (null == oldTags || oldTags.isEmpty()) {
				// 没有旧标签，无需移除
				return;
			} else {
				JSONArray newTags = removeTags(oldTags, tags);

				StringBuffer sb = new StringBuffer();
				sb.append("SET ").append(tagColumnName).append("=JSON_SET(").append(tagColumnName).append(",'$.")
						.append(tagKey);
				sb.append("',JSON_ARRAY(");
				for (Object tag : newTags) {
					sb.append("\"").append(tag).append("\",");
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append("))");
				this.update(conn, sb.toString(), new Object[] {}, "WHERE id=?", new Object[] { objId });
			}
		}
	}

	private static JSONArray mergeTags(JSONArray old, JSONArray now) {
		if (null == old || old.isEmpty()) {
			// old为空，用now覆盖
			return now;
		} else {
			JSONArray ret = new JSONArray();
			for (Object to : old) {
				ret.add(to);
			}
			// 开始循环逐个添加，如果存在就不添加
			for (Object tn : now) {
				boolean exist = false;
				for (Object to : old) {
					if (StringUtils.compareIgnoreCase(to.toString(), tn.toString()) == 0) {
						exist = true;
						break;
					} else {
						continue;
					}
				}

				if (exist) {
					// 已经存在，不添加
					continue;
				} else {
					ret.add(tn);
				}
			}
			return ret;
		}
	}

	private static JSONArray removeTags(JSONArray old, JSONArray now) {
		JSONArray ret = new JSONArray();
		// 开始循环逐个添加，如果存在就不添加
		for (Object to : old) {
			boolean exist = false;
			for (Object tn : now) {
				if (StringUtils.compareIgnoreCase(to.toString(), tn.toString()) == 0) {
					exist = true;
					break;
				} else {
					continue;
				}
			}

			if (exist) {
				// 已经存在，需要移除，不添加
				continue;
			} else {
				ret.add(to);
			}
		}
		return ret;
	}

	/**
	 * 模版方法，插入对象（如果字段为空则不插入该字段）</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param T
	 *            要插入的对象（泛型）
	 */
	public void insert(DruidPooledConnection conn, T t) throws ServerException {
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(mapper.getTableName());
		Map<String, Object> map;
		List<Object> values = new ArrayList<>();
		try {
			map = mapper.serialize(t);

			StringBuilder k = new StringBuilder(" (");
			StringBuilder v = new StringBuilder(" (");

			map.forEach((key, value) -> {
				if (null != value) {
					k.append(key).append(",");
					v.append("?").append(",");
					values.add(value);
				}
			});
			k.deleteCharAt(k.length() - 1);
			k.append(") ");
			v.deleteCharAt(v.length() - 1);
			v.append(") ");
			sql.append(k).append("VALUES").append(v);
			// System.err.println(sql);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, e.getMessage());
		}

		PreparedStatement ps = prepareStatement(conn, sql.toString(), values.toArray());
		try {
			int count = ps.executeUpdate();
			if (count != 1) {
				throw new ServerException(BaseRC.REPOSITORY_INSERT_ERROR);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 模版方法，批量插入对象列表</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param list
	 *            要插入的对象列表（泛型）
	 * @return 插入的记录数
	 */
	public int insertList(DruidPooledConnection conn, List<T> list) throws ServerException {
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(mapper.getTableName());
		Map<String, Object> map = null;
		List<Object> values = new ArrayList<>();

		try {
			StringBuilder k = new StringBuilder(" (");
			StringBuilder v = new StringBuilder(" (");
			for (int i = 0; i < list.size(); i++) {
				map = mapper.serialize(list.get(i));
				if (i >= 1) {
					v.append(",(");
				}

				if (i == 0) {
					map.forEach((key, value) -> {
						k.append(key + ",");
						v.append("?,");
						values.add(value);
					});
				} else {
					map.forEach((key, value) -> {
						v.append("?,");
						values.add(value);
					});
				}

				if (i == 0) {
					k.deleteCharAt(k.length() - 1);
					k.append(") ");
				}
				v.deleteCharAt(v.length() - 1);
				v.append(")");
			}

			sql.append(k).append("VALUES").append(v);

		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, e.getMessage());
		}

		PreparedStatement ps = prepareStatement(conn, sql.toString(), values.toArray());
		try {
			int count = ps.executeUpdate();
			if (count <= 0) {
				throw new ServerException(BaseRC.REPOSITORY_INSERT_ERROR, "nothing changed");
			} else {
				return count;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	};

	/**
	 * 模版方法，根据某个唯一键值获取一个对象</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @return 返回查询的对象，如果查询不到，则返回null
	 */
	public T getByKey(DruidPooledConnection conn, String key, Object value) throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(key).append("=?");
		return this.get(conn, sb.toString(), new Object[] { value });
	}

	public List<T> getListByKey(DruidPooledConnection conn, String key, Object value, int count, int offset)
			throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(key).append("=?");
		return this.getList(conn, sb.toString(), new Object[] { value }, count, offset);
	}

	public T getByKeys(DruidPooledConnection conn, String[] keys, Object[] values) throws ServerException {
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(keys[0]).append("=?");
		if (keys.length >= 2) {
			for (int i = 1; i < keys.length; i++) {
				sb.append(" and ");
				sb.append(keys[i]).append("=?");
			}
		}
		return this.get(conn, sb.toString(), values);
	}

	public List<T> getListByKeys(DruidPooledConnection conn, String[] keys, Object[] values, int count, int offset)
			throws ServerException {
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(keys[0]).append("=?");
		if (keys.length >= 2) {
			for (int i = 1; i < keys.length; i++) {
				sb.append(" and ");
				sb.append(keys[i]).append("=?");
			}
		}
		return this.getList(conn, sb.toString(), values, count, offset);
	}

	/**
	 * 模版方法，根据某个唯一键值删除一个对象</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @return 返回影响的记录数
	 */
	public int deleteByKey(DruidPooledConnection conn, String key, Object value) throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(key).append("=?");
		return delete(conn, sb.toString(), new Object[] { value });
	}

	public int deleteByKeys(DruidPooledConnection conn, String[] keys, Object[] values) throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		sb.append(keys[0]).append("=?");
		if (keys.length >= 2) {
			for (int i = 1; i < keys.length; i++) {
				sb.append(" and ");
				sb.append(keys[i]).append("=?");
			}
		}
		return delete(conn, sb.toString(), values);
	}

	/**
	 * 模版方法，根据某个唯一键值更新一个对象</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @param t
	 *            要更新的对象（最好在对象中将key所对应的值抹掉）
	 * @return 返回影响的记录数
	 */
	public int updateByKey(DruidPooledConnection conn, String key, Object value, T t, boolean skipNull)
			throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(key).append("=?");
		return this.update(conn, sb.toString(), new Object[] { value }, t, skipNull);
	}

	public int updateByKeys(DruidPooledConnection conn, String[] keys, Object[] values, T t, boolean skipNull)
			throws ServerException {
		if (keys.length != values.length) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_PREPARE_ERROR, "keys length not equal values length");
		}
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(keys[0]).append("=?");
		if (keys.length >= 2) {
			for (int i = 1; i < keys.length; i++) {
				sb.append(" and ");
				sb.append(keys[i]).append("=?");
			}
		}
		return this.update(conn, sb.toString(), values, t, skipNull);
	}

	/**
	 * 模版方法，根据某个唯一键值获取对象数组</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @return 返回查询的对象数组，如果查询不到，则数组长度为0
	 */
	public List<T> getListByKey(DruidPooledConnection conn, String key, Object value, Integer count, Integer offset)
			throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(key).append("=?");
		return this.getList(conn, sb.toString(), new Object[] { value }, count, offset);
	}

	/**
	 * 模版方法，根据某个唯一键值的某些值，获取这些值所对应的对象数组</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param values
	 *            值数组（字符串，在外部转换好再放进来，防止隐式转换出问题）
	 * @return 查询的对象列表
	 * @throws ServerException
	 */
	public List<T> getListByKeyInValues(DruidPooledConnection conn, String key, String[] values)
			throws ServerException {
		// 有oracle兼容性问题，不支持limit和offset
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(mapper.getTableName());
		sql.append(" WHERE ");
		sql.append(key).append(" IN (");
		List<Object> inValues = new ArrayList<>();
		StringBuffer ordersb = new StringBuffer(" ORDER BY FIND_IN_SET(");
		ordersb.append(key).append(",'");
		for (String id : values) {
			sql.append("?").append(",");
			ordersb.append(id).append(",");
			inValues.add(id);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
		ordersb.deleteCharAt(ordersb.length() - 1);
		ordersb.append("')");
		sql.append(ordersb);

		PreparedStatement ps = prepareStatement(conn, sql.toString(), values);
		try {
			ResultSet rs = ps.executeQuery();
			return mapper.deserialize(rs, clazz);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * TODO 这个方法有些不规范，但没有更好的办法
	 * 
	 * 模版方法，根据某个唯一键值的某些值，获取这些值所对应的对象数组</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param values
	 *            值数组（字符串，在外部转换好再放进来，防止隐式转换出问题）
	 * @return 查询的对象列表
	 * @throws ServerException
	 */
	public List<T> getListWhereKeyInValues(DruidPooledConnection conn, String where, String key, String[] values,
			String... whereParams) throws ServerException {
		// 有oracle兼容性问题，不支持limit和offset
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(mapper.getTableName());
		sql.append(" ").append(where).append(" AND ");
		sql.append(key).append(" IN (");
		List<Object> inValues = new ArrayList<>();
		StringBuffer ordersb = new StringBuffer(" ORDER BY FIND_IN_SET(");
		ordersb.append(key).append(",'");
		for (String id : values) {
			sql.append("?").append(",");
			ordersb.append(id).append(",");
			inValues.add(id);
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") ");
		ordersb.deleteCharAt(ordersb.length() - 1);
		ordersb.append("')");
		sql.append(ordersb);
		String[] newValues = new String[values.length + whereParams.length];
		System.arraycopy(whereParams, 0, newValues, 0, whereParams.length);
		System.arraycopy(values, 0, newValues, whereParams.length, values.length);
		PreparedStatement ps = prepareStatement(conn, sql.toString(), newValues);
		try {
			ResultSet rs = ps.executeQuery();
			return mapper.deserialize(rs, clazz);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 模版方法，不带任何条件的获取表中的记录数</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @return 查询出来的记录数
	 */
	public int count(DruidPooledConnection conn) throws ServerException {
		return count(conn, null, null);
	}

	/**
	 * 模版方法，根据某个唯一键值获取符合这一条件的记录数</br>
	 * 
	 * @param conn
	 *            连接对象
	 * @param key
	 *            列名
	 * @param value
	 *            值
	 * @return 返记录数
	 */
	public int countByKey(DruidPooledConnection conn, String key, Object value) throws ServerException {
		StringBuffer sb = new StringBuffer("WHERE ");
		sb.append(key).append("=?");
		return count(conn, sb.toString(), new Object[] { value });
	}

	/**
	 * 返回某列得字符串值
	 */
	protected String getColumnString(DruidPooledConnection conn, String columnName, String where, Object[] whereParams)
			throws ServerException {
		StringBuilder sql = new StringBuilder("SELECT ").append(columnName).append(" FROM ")
				.append(mapper.getTableName());
		if (StringUtils.isNotBlank(where)) {
			sql.append(BLANK).append(where);
		} else {
			// 查询操作 where可以为空
		}
		// System.out.println(sql);
		PreparedStatement ps = prepareStatement(conn, sql.toString(), whereParams);
		try {
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString(columnName);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 方便跨对象操作的原生SQL模版方法，无特殊情况请避免使用</br>
	 */
	protected <X> List<X> nativeGetList(DruidPooledConnection conn, RDSRepository<X> repository, String sql,
			Object[] whereParams) throws ServerException {
		PreparedStatement ps = prepareStatement(conn, sql, whereParams);
		try {
			ResultSet rs = ps.executeQuery();
			return repository.mapper.deserialize(rs, repository.clazz);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_SQL_EXECUTE_ERROR, e.getMessage());
		} finally {
			try {
				ps.close();
			} catch (Exception e) {
			}
		}
	}

}
