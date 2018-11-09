package zyxhj.org.cn.core.repository;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.org.cn.core.domain.User;
import zyxhj.org.cn.utils.api.ServerException;
import zyxhj.org.cn.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRepository extends RDSRepository<User> {

	private static UserRepository ins;

	public static synchronized UserRepository getInstance() {
		if (null == ins) {
			ins = new UserRepository();
		}
		return ins;
	}

	private UserRepository() {
		super(User.class);
	}

	public JSONArray getTags(DruidPooledConnection conn, Long userId, String tagKey) throws ServerException {
		return this.getTags(conn, "tags", userId, tagKey);
	}

	public void addTags(DruidPooledConnection conn, Long userId, String tagKey, JSONArray tags) throws ServerException {
		this.addTags(conn, "tags", userId, tagKey, tags);
	}

	public void removeTags(DruidPooledConnection conn, Long userId, String tagKey, JSONArray tags)
			throws ServerException {
		this.removeTags(conn, "tags", userId, tagKey, tags);
	}
}
