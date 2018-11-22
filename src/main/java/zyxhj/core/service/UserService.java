package zyxhj.core.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.core.domain.User;
import zyxhj.core.domain.UserSession;
import zyxhj.core.repository.UserRepository;
import zyxhj.core.repository.UserSessionRepository;
import zyxhj.org.cn.utils.CacheCenter;
import zyxhj.org.cn.utils.IDUtils;
import zyxhj.org.cn.utils.api.BaseRC;
import zyxhj.org.cn.utils.api.ServerException;
import zyxhj.org.cn.utils.data.ots.OTSAutoCloseableClient;

public class UserService {

	private static Logger log = LoggerFactory.getLogger(UserService.class);

	private static UserService ins;

	public static synchronized UserService getInstance() {
		if (null == ins) {
			ins = new UserService();
		}
		return ins;
	}

	private UserRepository userRepository;
	private UserSessionRepository userSessionRepository;

	private UserService() {
		try {
			userRepository = UserRepository.getInstance();
			userSessionRepository = UserSessionRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public UserSession putUserSession(OTSAutoCloseableClient client, Long userId, Byte userLevel, Date loginTime,
			String loginToken) throws Exception {

		UserSession ret = new UserSession();
		ret.userId = userId;
		ret.level = userLevel;
		ret.loginTime = loginTime;
		ret.loginToken = loginToken;

		// 先放入Session缓存，再放入存储
		CacheCenter.SESSION_CACHE.put(userId, ret);
		userSessionRepository.putObject(client, ret, true);
		return ret;
	}

	public UserSession getUserSession(OTSAutoCloseableClient client, Long userId) throws Exception {
		// 先从缓存中获取
		UserSession session = null;
		try {
			session = CacheCenter.SESSION_CACHE.get(userId);
		} catch (Exception e) {
			session = userSessionRepository.getByKey(client, userId);
		}
		return session;
	}

	public void clearUserSessionById(OTSAutoCloseableClient client, Long userId) throws Exception {
		CacheCenter.SESSION_CACHE.invalidate(userId);
		userSessionRepository.deleteByKey(client, userId);
	}

	/**
	 * 用户名密码注册
	 * 
	 * @param name
	 *            用户名（必填）
	 * @param pwd
	 *            密码（必填）
	 * 
	 * @return 刚注册的用户对象
	 */
	public User registByNameAndPwd(DruidPooledConnection conn, String name, String pwd) throws Exception {
		// 用户不存在
		User newUser = new User();
		newUser.id = IDUtils.getSimpleId();
		newUser.createDate = new Date();
		newUser.level = User.LEVEL_BASIC;
		newUser.name = name;

		newUser.pwd = pwd;// TODO 目前是明文，需要加料传输和存储

		// 创建用户
		userRepository.insert(conn, newUser);
		newUser.pwd = null;// 抹掉密码
		return newUser;
	}

	public User loginByNameAndPwd(DruidPooledConnection conn, String name, String pwd) throws Exception {
		// 判断用户是否存在
		User existUser = userRepository.getByKey(conn, "name", name);
		if (null == existUser) {
			// 用户不存在
			throw new ServerException(BaseRC.USER_NOT_EXIST);
		} else {
			// 用户已存在，匹配密码
			// TODO 目前是明文，需要加料然后匹配
			if (pwd.equals(existUser.pwd)) {
				return existUser;
			} else {
				// 密码错误
				throw new ServerException(BaseRC.USER_PWD_ERROR);
			}
		}
	}

	public User getUserById(DruidPooledConnection conn, Long userId) throws Exception {
		return userRepository.getByKey(conn, "id", userId);
	}

	public User getUserByName(DruidPooledConnection conn, String name) throws Exception {
		return userRepository.getByKey(conn, "name", name);
	}

	public User getUserByMobile(DruidPooledConnection conn, String mobile) throws Exception {
		return userRepository.getByKey(conn, "mobile", mobile);
	}

	public User getUserByEmail(DruidPooledConnection conn, String email) throws Exception {
		return userRepository.getByKey(conn, "email", email);
	}

	public User getUserByQQOpenId(DruidPooledConnection conn, String qqOpenId) throws Exception {
		return userRepository.getByKey(conn, "qq_open_id", qqOpenId);
	}

	public User getUserByWxOpenId(DruidPooledConnection conn, String wxOpenId) throws Exception {
		return userRepository.getByKey(conn, "wx_open_id", wxOpenId);
	}

	public User getUserByWbOpenId(DruidPooledConnection conn, String wbOpenId) throws Exception {
		return userRepository.getByKey(conn, "wb_open_id", wbOpenId);
	}

	public void setUserNickname(DruidPooledConnection conn, Long userId, String nickname) throws Exception {
		User forUpdate = new User();
		forUpdate.nickname = nickname;
		userRepository.updateByKey(conn, "id", userId, forUpdate, true);
	}

	public void setUserSignature(DruidPooledConnection conn, Long userId, String signature) throws Exception {
		User forUpdate = new User();
		forUpdate.signature = signature;
		userRepository.updateByKey(conn, "id", userId, forUpdate, true);
	}

	public int deleteUserById(DruidPooledConnection conn, Long userId) throws Exception {
		return userRepository.deleteByKey(conn, "id", userId);
	}

	public User auth(DruidPooledConnection conn, Long userId) throws Exception {
		// 先判断user是否存在
		User user = userRepository.getByKey(conn, "id", userId);
		if (null == user) {
			// user不存在
			throw new ServerException(BaseRC.USER_NOT_EXIST);
		} else {
			// 再判断user状态是否有效，TODO 目前status没有启用
			if (User.STATUS_NORMAL == user.status) {
				// 正常状态

				// 最后判断令牌授权等是否有效，TODO，目前没有设计

				// 鉴权必定查询，为方便后续使用，将查询到的user返回备用，避免将来查询
				return user;
			} else if (User.STATUS_PENDING == user.status) {
				// 应用正在等待审核
				throw new ServerException(BaseRC.USER_AUTH_PENDING);
			} else if (User.STATUS_STOPED == user.status) {
				// 应用已被停用
				throw new ServerException(BaseRC.USER_AUTH_STOPED);
			} else if (User.STATUS_DELETED == user.status) {
				// 应用已经删除
				throw new ServerException(BaseRC.USER_AUTH_DELETED);
			} else {
				// 未知的用户状态
				throw new ServerException(BaseRC.USER_AUTH_UNKNOWN_STATUS);
			}

		}
	}

	public JSONArray getUserTags(DruidPooledConnection conn, Long userId, String tagKey) throws Exception {
		return userRepository.getTags(conn, userId, tagKey);
	}

	public void addUserTags(DruidPooledConnection conn, Long userId, String tagKey, JSONArray tags) throws Exception {
		userRepository.addTags(conn, userId, tagKey, tags);
	}

	public void removeUserTags(DruidPooledConnection conn, Long userId, String tagKey, JSONArray tags)
			throws Exception {
		userRepository.removeTags(conn, userId, tagKey, tags);
	}
}
