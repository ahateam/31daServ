package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.LoginBo;
import zyxhj.core.domain.User;
import zyxhj.core.service.UserService;
import zyxhj.utils.IDUtils;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class UserController extends Controller {

	private static Logger log = LoggerFactory.getLogger(UserController.class);

	private DataSource dsRds;
	private UserService userService;

	public UserController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			userService = Singleton.ins(UserService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getUserById", //
			des = "通过编号获取用户对象", //
			ret = "编号对应的用户对象"//
	)
	public APIResponse getUserById(//
			@P(t = "用户编号") Long userId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = userService.getUserById(conn, userId);
			ServiceUtils.checkNull(user);
			user.pwd = null;// 置空密码
			return APIResponse.getNewSuccessResp(user);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "registByNameAndPwd", //
			des = "用户名密码注册", //
			ret = "LoginBO对象，包含user，session等信息"//
	)
	public APIResponse registByNameAndPwd(//
			@P(t = "用户名") String name, //
			@P(t = "密码") String pwd//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection();) {
			User user = userService.registByNameAndPwd(conn, name, pwd);
			// 如果成功注册，则写入Session后，返回LoginBo
			LoginBo loginBo = userService.login(conn, user);
			// 返回登录业务对象
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "loginByNameAndPwd", //
			des = "用户名密码登录", //
			ret = "LoginBO对象，包含user，session等信息"//
	)
	public APIResponse loginByNameAndPwd(//
			@P(t = "用户名") String name, //
			@P(t = "密码") String pwd//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			// 如果成功登录，则写入Session后，返回LoginBo
			LoginBo loginBo = userService.loginByNameAndPwd(conn, name, pwd);
			// 返回登录业务对象
			return APIResponse.getNewSuccessResp(loginBo);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "loginByAnonymous", //
			des = "匿名登录", //
			ret = "LoginBO对象，包含user，session等信息"//
	)
	public APIResponse loginByAnonymous() throws Exception {

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
