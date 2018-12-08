package zyxhj.utils;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;

public class ServiceUtils {

	private static UserService userService;

	static {
		userService = UserService.getInstance();
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

}
