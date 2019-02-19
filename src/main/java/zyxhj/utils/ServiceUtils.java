package zyxhj.utils;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public final class ServiceUtils {

	private static UserService userService;

	static {
		try {
			userService = Singleton.ins(UserService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO 测试代码，目前不验证
	 * 
	 */
	public static User userAuth(DruidPooledConnection conn, Long userId) throws Exception {
		User u = new User();
		u.id = userId;
		return u;
		// return userService.auth(conn, userId);
	}

	public static Object checkNull(Object obj) throws ServerException {
		if (null == obj) {
			throw new ServerException(BaseRC.SERVER_OBJECT_NULL);
		} else {
			return obj;
		}
	}

}
