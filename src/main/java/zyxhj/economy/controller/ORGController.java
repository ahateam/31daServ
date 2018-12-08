package zyxhj.economy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.economy.service.ORGService;
import zyxhj.utils.api.APIRequest;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.Param;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class ORGController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ORGController.class);

	private static ORGController ins;

	public static synchronized ORGController getInstance(String node) {
		if (null == ins) {
			ins = new ORGController(node);
		}
		return ins;
	}

	private DataSource dsRds;
	private ORGService orgService;

	private ORGController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			orgService = ORGService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	// /**
	// * 组织管理员注册
	// *
	// * @param mobile
	// * 用户名（必填）
	// * @param name
	// * 密码（必填）
	// * @param idNumber
	// * 密码（必填）
	// *
	// * @return LoginBO 业务对象，包含用户session等相关
	// */
	// @POSTAPI(path = "registORGAdmin")
	// public APIResponse registORGAdmin(APIRequest req) throws Exception {
	// JSONObject c = Param.getReqContent(req);
	//
	// String mobile = Param.getString(c, "mobile");
	// String name = Param.getString(c, "name");
	// String idNumber = Param.getString(c, "idNumber");
	//
	// try (DruidPooledConnection conn = (DruidPooledConnection)
	// dsRds.openConnection();) {
	// User user = orgService.registORGAdmin(conn, mobile, name, idNumber);
	// // 如果成功注册，则写入Session后，返回LoginBo
	// LoginBo loginBo = userService.login(conn, user);
	// // 返回登录业务对象
	// return APIResponse.getNewSuccessResp(loginBo);
	// }
	// }
	//
	// /**
	// * 手机号密码登录
	// *
	// * @param mobile
	// * 手机号
	// * @param pwd
	// * 密码（目前明文传递，将来需要加密传递 ）
	// * @return LoginBO 业务对象，包含用户session等相关
	// */
	// @POSTAPI(path = "loginORGAdmin")
	// public APIResponse loginORGAdmin(APIRequest req) throws Exception {
	// JSONObject c = Param.getReqContent(req);
	//
	// String mobile = Param.getString(c, "mobile");
	// String pwd = Param.getString(c, "pwd");
	//
	// try (DruidPooledConnection conn = (DruidPooledConnection)
	// dsRds.openConnection()) {
	// // 如果成功登录，则写入Session后，返回LoginBo
	// LoginBo loginBo = orgService.loginORGAdmin(conn, mobile, pwd);
	// // 返回登录业务对象
	// return APIResponse.getNewSuccessResp(loginBo);
	// }
	// }

	/**
	 * 创建组织
	 * 
	 * @param name
	 *            组织名称（必填）
	 * @param code
	 *            组织机构代码（必填）
	 * @param province
	 *            省（必填）
	 * @param city
	 *            市（必填）
	 * @param district
	 *            区（必填）
	 * @param address
	 *            地址（必填）
	 * @param imgOrg
	 *            组织机构证书
	 * @param imgAuth
	 *            组织授权证书
	 * @param shareAmount
	 *            总股份数
	 * @return 成功创建的组织对象
	 */
	@POSTAPI(path = "createORG")
	public APIResponse createORG(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		String name = Param.getString(c, "name");
		String code = Param.getString(c, "code");

		String province = Param.getString(c, "province");
		String city = Param.getString(c, "city");
		String district = Param.getString(c, "district");
		String address = Param.getString(c, "address");
		String imgOrg = Param.getStringDFLT(c, "imgOrg", null);
		String imgAuth = Param.getStringDFLT(c, "imgAuth", null);

		Integer shareAmount = Param.getInteger(c, "shareAmount");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.createORG(conn, name, code, province, city, district, address, imgOrg, imgAuth, shareAmount);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 更新组织信息
	 * 
	 * @param orgId
	 *            组织编号（必填）
	 * @param province
	 *            省（必填）
	 * @param city
	 *            市（必填）
	 * @param district
	 *            区（必填）
	 * @param address
	 *            地址（必填）
	 * @param imgOrg
	 *            组织机构证书
	 * @param imgAuth
	 *            组织授权证书
	 * @param shareAmount
	 *            总股份数
	 * @return 成功更新的组织对象
	 */
	@POSTAPI(path = "setORG")
	public APIResponse setORG(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");

		String province = Param.getStringDFLT(c, "province", null);
		String city = Param.getStringDFLT(c, "city", null);
		String district = Param.getStringDFLT(c, "district", null);
		String address = Param.getStringDFLT(c, "address", null);
		String imgOrg = Param.getStringDFLT(c, "imgOrg", null);
		String imgAuth = Param.getStringDFLT(c, "imgAuth", null);

		Integer shareAmount = Param.getIntegerDFLT(c, "shareAmount", null);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.setORG(conn, orgId, province, city, district, address, imgOrg, imgAuth, shareAmount);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 获取组织列表
	 * 
	 * @param count
	 *            数量（用于分页）
	 * @param offset
	 *            起始位置（从零开始，用于分页）
	 * @return 返回组织列表
	 */
	@POSTAPI(path = "getORGs")
	public APIResponse getORGs(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Integer count = Param.getInteger(c, "count");
		Integer offset = Param.getInteger(c, "offset");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGs(conn, count, offset));
		}
	}

	/**
	 * 获取组织详细信息
	 * 
	 * @param orgId
	 *            组织编号（必填）
	 * @return 组织对象
	 */
	@POSTAPI(path = "getORGById")
	public APIResponse getORGById(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(Param.checkNull(orgService.getORGById(conn, orgId)));
		}
	}

	/**
	 * 获取用户的组织列表
	 * 
	 * @param userId
	 *            用户编号
	 * @return 返回组织列表
	 */
	@POSTAPI(path = "getUserORGs")
	public APIResponse getUserORGs(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long userId = Param.getLong(c, "userId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(Param.checkNull(orgService.getUserORGs(conn, userId)));
		}
	}

	/**
	 * 导入用户
	 * 
	 * @param orgId
	 *            组织编号
	 * @param mobile
	 *            手机号
	 * @param realName
	 *            用户真实姓名
	 * @param idNumber
	 *            身份证号
	 * @param share
	 *            股东身份类型
	 * @param shareAmount
	 *            股份数
	 * @param weight
	 *            股东权重
	 * @param duty
	 *            董事会职务类型
	 * @param visor
	 *            监事会职务类型
	 * 
	 */
	@POSTAPI(path = "importUser")
	public APIResponse importUser(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		String mobile = Param.getString(c, "mobile");
		String realName = Param.getString(c, "realName");
		String idNumber = Param.getString(c, "idNumber");

		Byte share = Param.getByte(c, "share");
		Integer shareAmount = Param.getInteger(c, "shareAmount");
		Integer weight = Param.getInteger(c, "weight");
		Byte duty = Param.getByte(c, "duty");
		Byte visor = Param.getByte(c, "visor");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.importUser(conn, orgId, mobile, realName, idNumber, share, shareAmount, weight, duty, visor);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 修改用户信息，身份证信息不能修改
	 * 
	 * @param userId
	 *            用户编号
	 * @param mobile
	 *            手机号（非必填）
	 * @param realName
	 *            用户真实姓名（非必填）
	 * @param pwd
	 *            密码（非必填）
	 */
	@POSTAPI(path = "updateUser")
	public APIResponse updateUser(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long userId = Param.getLong(c, "userId");

		String mobile = Param.getStringDFLT(c, "mobile", null);
		String realName = Param.getStringDFLT(c, "realName", null);
		String pwd = Param.getStringDFLT(c, "pwd", null);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.updateUser(conn, userId, mobile, realName, pwd);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 移除组织用户，不会影响user表
	 * 
	 * @param orgId
	 *            组织编号
	 * @param userId
	 *            用户编号
	 * 
	 */
	@POSTAPI(path = "removeORGUser")
	public APIResponse removeORGUser(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		Long userId = Param.getLong(c, "userId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.removeORGUser(conn, orgId, userId);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 更新组织用户，不会影响user表
	 * 
	 * @param orgId
	 *            组织编号
	 * @param userId
	 *            用户编号
	 * @param share
	 *            股东身份类型（非必填）
	 * @param weight
	 *            股东权重（非必填）
	 * @param duty
	 *            董事会职务类型（非必填）
	 * @param visor
	 *            监事会职务类型（非必填）
	 * 
	 */
	@POSTAPI(path = "updateORGUser")
	public APIResponse updateORGUser(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		Long userId = Param.getLong(c, "userId");

		Byte share = Param.getByteDFLT(c, "share", null);
		Integer weight = Param.getIntegerDFLT(c, "weight", null);
		Byte duty = Param.getByteDFLT(c, "duty", null);
		Byte visor = Param.getByteDFLT(c, "visor", null);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.updateORGUser(conn, orgId, userId, share, weight, duty, visor);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 手机号密码登录
	 * 
	 * @param mobile
	 *            电话号码
	 * @param pwd
	 *            密码
	 * @param 登录业务对象
	 */
	@POSTAPI(path = "loginByMobileAndPwd")
	public APIResponse loginByMobileAndPwd(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		String mobile = Param.getString(c, "mobile");
		String pwd = Param.getString(c, "pwd");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(Param.checkNull(orgService.loginByMobileAndPwd(conn, mobile, pwd)));
		}
	}

	/**
	 * 二次登录，进入对应组织
	 * 
	 * @param userId
	 *            用户编号
	 * @param orgId
	 *            组织编号
	 * @return 组织登录业务对象
	 */
	@POSTAPI(path = "loginInORG")
	public APIResponse loginInORG(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long userId = Param.getLong(c, "userId");
		Long orgId = Param.getLong(c, "orgId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(Param.checkNull(orgService.loginInORG(conn, userId, orgId)));
		}
	}

	/**
	 * 获取组织董事会成员列表
	 * 
	 * @param orgId
	 *            组织编号
	 * @param count
	 *            数量（用于分页）
	 * @param offset
	 *            起始位置（从零开始，用于分页）
	 * @return 董事会成员列表
	 */
	@POSTAPI(path = "getORGDirectors")
	public APIResponse getORGDirectors(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		Integer count = Param.getInteger(c, "count");
		Integer offset = Param.getInteger(c, "offset");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGDirectors(conn, orgId, count, offset));
		}
	}

	/**
	 * 获取组织股东成员列表
	 * 
	 * @param orgId
	 *            组织编号
	 * @param count
	 *            数量（用于分页）
	 * @param offset
	 *            起始位置（从零开始，用于分页）
	 * @return 股东成员列表
	 */
	@POSTAPI(path = "getORGShareholders")
	public APIResponse getORGShareholders(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		Integer count = Param.getInteger(c, "count");
		Integer offset = Param.getInteger(c, "offset");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGShareholders(conn, orgId, count, offset));
		}
	}

	/**
	 * 获取组织监事会成员列表
	 * 
	 * @param orgId
	 *            组织编号
	 * @param count
	 *            数量（用于分页）
	 * @param offset
	 *            起始位置（从零开始，用于分页）
	 * @return 监事会成员列表
	 */
	@POSTAPI(path = "getORGSupervisors")
	public APIResponse getORGSupervisors(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		Integer count = Param.getInteger(c, "count");
		Integer offset = Param.getInteger(c, "offset");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGSupervisors(conn, orgId, count, offset));
		}
	}

}
