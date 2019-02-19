package zyxhj.core.repository;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.core.domain.User;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRepository extends RDSRepository<User> {

	public UserRepository() {
		super(User.class);
	}

	public JSONArray getUserTags(DruidPooledConnection conn, Long userId, String tagKey) throws ServerException {
		return this.getTags(conn, "tags", tagKey, "WHERE id=?", new Object[] { userId });
	}

}
