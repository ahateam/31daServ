package zyxhj.core.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.LoginBo;
import zyxhj.core.domain.User;
import zyxhj.core.domain.UserSession;
import zyxhj.core.service.UserService;
import zyxhj.org.cn.utils.IDUtils;
import zyxhj.org.cn.utils.api.APIRequest;
import zyxhj.org.cn.utils.api.APIResponse;
import zyxhj.org.cn.utils.api.Controller;
import zyxhj.org.cn.utils.api.Param;
import zyxhj.org.cn.utils.data.DataSource;
import zyxhj.org.cn.utils.data.DataSourceUtils;
import zyxhj.org.cn.utils.data.ots.OTSAutoCloseableClient;

public class UserController extends Controller {

	private static Logger log = LoggerFactory.getLogger(UserController.class);

	private static UserController ins;

	public static synchronized UserController getInstance(String node) {
		if (null == ins) {
			ins = new UserController(node);
		}
		return ins;
	}

	private DataSource dsRds;
	private DataSource dsOts;
	private UserService userService;

	private UserController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");
			dsOts = DataSourceUtils.getDataSource("otsDefault");

			userService = UserService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 通过用户编号获取用户信息
	 * 
	 * @param userId
	 *            用户编号
	 * @return 用户对象（部分信息应该抹掉，或者不查询出来，例如pwd，现在偷懒简单做的）
	 */
	@POSTAPI(path = "getUserById")
	public APIResponse getUserById(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long userId = Param.getLong(c, "userId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = userService.getUserById(conn, userId);
			Param.checkNull(user);
			user.pwd = null;// 置空密码
			return APIResponse.getNewSuccessResp(user);
		}
	}

	private LoginBo login(DruidPooledConnection conn, OTSAutoCloseableClient client, User user) throws Exception {
		Date loginTime = new Date();
		UserSession userSession = userService.putUserSession(client, user.id, user.level, loginTime,
				IDUtils.getHexSimpleId());

		LoginBo ret = new LoginBo();
		ret.id = user.id;
		ret.level = user.level;
		ret.name = user.name;
		ret.nickname = user.nickname;
		ret.signature = user.signature;

		ret.loginTime = userSession.loginTime;
		ret.loginToken = userSession.loginToken;

		return ret;
	}

	/**
	 * 用户名密码注册
	 * 
	 * @param name
	 *            用户名（必填）
	 * @param pwd
	 *            密码（必填）
	 * 
	 * @return LoginBO 业务对象，包含用户session等相关
	 */
	@POSTAPI(path = "registByNameAndPwd")
	public APIResponse registByNameAndPwd(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		String name = Param.getString(c, "name");
		String pwd = Param.getString(c, "pwd");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection();
				OTSAutoCloseableClient client = (OTSAutoCloseableClient) dsOts.openConnection()) {
			User user = userService.registByNameAndPwd(conn, name, pwd);
			// 如果成功注册，则写入Session后，返回LoginBo
			LoginBo loginBo = login(conn, client, user);
			// 返回登录业务对象
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

	/**
	 * 用户名密码登录
	 * 
	 * @param name
	 *            用户名
	 * @param pwd
	 *            密码（目前明文传递，将来需要加密传递 ）
	 * @return LoginBO 业务对象，包含用户session等相关
	 */
	@POSTAPI(path = "loginByNameAndPwd")
	public APIResponse loginByNameAndPwd(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		String name = Param.getString(c, "name");
		String pwd = Param.getString(c, "pwd");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection();
				OTSAutoCloseableClient client = (OTSAutoCloseableClient) dsOts.openConnection()) {
			User user = userService.loginByNameAndPwd(conn, name, pwd);
			// 如果成功登录，则写入Session后，返回LoginBo
			LoginBo loginBo = login(conn, client, user);
			// 返回登录业务对象
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

	/**
	 * 用户名密码登录
	 * 
	 * @return LoginBO 业务对象，包含用户session等相关
	 */
	@POSTAPI(path = "loginByAnonymous")
	public APIResponse loginByAnonymous(APIRequest req) throws Exception {
		// JSONObject c = Param.getReqContent(req);

		// 构造匿名user
		User anonymous = new User();
		anonymous.id = IDUtils.getSimpleId();
		anonymous.level = User.LEVEL_ANONYMOUS;
		anonymous.name = "anonymous";
		anonymous.nickname = "";

		// 写入匿名用户Session后，返回LoginBo
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection();
				OTSAutoCloseableClient client = (OTSAutoCloseableClient) dsOts.openConnection()) {
			LoginBo loginBo = login(conn, client, anonymous);
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

}
