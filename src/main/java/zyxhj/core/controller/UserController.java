package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.LoginBo;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.api.APIRequest;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.Param;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

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
	private UserService userService;

	private UserController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

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

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection();) {
			User user = userService.registByNameAndPwd(conn, name, pwd);
			// 如果成功注册，则写入Session后，返回LoginBo
			LoginBo loginBo = userService.login(conn, user);
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

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			// 如果成功登录，则写入Session后，返回LoginBo
			LoginBo loginBo = userService.loginByNameAndPwd(conn, name, pwd);
			// 返回登录业务对象
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

	/**
	 * 匿名登录
	 * 
	 * @return LoginBO 业务对象，包含用户session等相关
	 */
	@POSTAPI(path = "loginByAnonymous")
	public APIResponse loginByAnonymous(APIRequest req) throws Exception {
		// JSONObject c = Param.getReqContent(req);

		// 构造匿名user
		User anonymous = new User();
		anonymous.id = IDUtils.getSimpleId();
		anonymous.name = "游客";
		anonymous.nickname = "孙悟空到此一游";

		// 写入匿名用户Session后，返回LoginBo
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			LoginBo loginBo = userService.login(conn, anonymous);
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

}
