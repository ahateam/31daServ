package zyxhj.economy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.service.ServiceUtils;
import zyxhj.economy.domain.ORGRole;
import zyxhj.economy.service.AssetService;
import zyxhj.economy.service.ORGService;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
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

	private AssetService assetService;

	private ORGController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			orgService = ORGService.getInstance();

			assetService = AssetService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@ENUM
	public ORGRole.SHARE[] shareTypes = ORGRole.SHARE.values();

	@ENUM
	public ORGRole.DUTY[] dutyTypes = ORGRole.DUTY.values();

	@ENUM
	public ORGRole.VISOR[] visorTypes = ORGRole.VISOR.values();

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
	 * 
	 */
	@POSTAPI(path = "createORG", //
			des = "创建组织", //
			ret = "所创建的对象"//
	)
	public APIResponse createORG(//
			@P(t = "组织名称") String name, //
			@P(t = "组织机构代码") String code, //
			@P(t = "省") String province, //
			@P(t = "市") String city, //
			@P(t = "区") String district, //
			@P(t = "街道地址") String address, //
			@P(t = "组织机构证书图片地址", r = false) String imgOrg, //
			@P(t = "组织授权证书图片地址", r = false) String imgAuth, //
			@P(t = "总股份数") Integer shareAmount//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.createORG(conn, name, code, province, city, district, address, imgOrg, imgAuth, shareAmount);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "setORG", //
			des = "更新组织", //
			ret = "所更新的对象"//
	)
	public APIResponse setORG(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "省") String province, //
			@P(t = "市") String city, //
			@P(t = "区") String district, //
			@P(t = "街道地址") String address, //
			@P(t = "组织机构证书图片地址", r = false) String imgOrg, //
			@P(t = "组织授权证书图片地址", r = false) String imgAuth, //
			@P(t = "总股份数") Integer shareAmount//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.setORG(conn, orgId, province, city, district, address, imgOrg, imgAuth, shareAmount);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getORGs", //
			des = "获取全部组织列表", //
			ret = "组织对象列表"//
	)
	public APIResponse getORGs(//
			Integer count, //
			Integer offset) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGs(conn, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getORGById", //
			des = "获取组织对象", //
			ret = "组织对象"//
	)
	public APIResponse getORGById(//
			@P(t = "组织编号") Long orgId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(ServiceUtils.checkNull(orgService.getORGById(conn, orgId)));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getUserORGs", //
			des = "获取用户的组织列表", //
			ret = "组织对象列表"//
	)
	public APIResponse getUserORGs(//
			@P(t = "用户编号") Long userId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(ServiceUtils.checkNull(orgService.getUserORGs(conn, userId)));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "importUser", //
			des = "导入用户到某个组织" //
	)
	public APIResponse importUser(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "手机号") String mobile, //
			@P(t = "真实姓名") String realName, //
			@P(t = "身份证号") String idNumber, //
			@P(t = "股东类型,ORGRole.SHARE") Byte share, //
			@P(t = "股份数") Integer shareAmount, //
			@P(t = "选举权重") Integer weight, //
			@P(t = "董事会职务类型,ORGRole.DUTY") Byte duty, //
			@P(t = "监事会职务类型,ORGRole.VISOR") Byte visor//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.importUser(conn, orgId, mobile, realName, idNumber, share, shareAmount, weight, duty, visor);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "updateUser", //
			des = "更新用户信息", //
			ret = "更新影响记录的行数"//
	)
	public APIResponse updateUser(//
			@P(t = "用户编号") Long userId, //
			@P(t = "手机号", r = false) String mobile, //
			@P(t = "真实姓名", r = false) String realName, //
			@P(t = "密码", r = false) String pwd//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			int ret = orgService.updateUser(conn, userId, mobile, realName, pwd);
			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "removeORGUser", //
			des = "移除组织的用户" //
	)
	public APIResponse removeORGUser(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "用户编号") Long userId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			orgService.removeORGUser(conn, orgId, userId);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "updateORGUser", //
			des = "更新组织的用户", //
			ret = "更新影响记录的行数"//
	)
	public APIResponse updateORGUser(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "股东类型,ORGRole.SHARE") Byte share, //
			@P(t = "股份数") Integer shareAmount, //
			@P(t = "选举权重") Integer weight, //
			@P(t = "董事会职务类型,ORGRole.DUTY") Byte duty, //
			@P(t = "监事会职务类型,ORGRole.VISOR") Byte visor//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			int ret = orgService.updateORGUser(conn, orgId, userId, share, weight, duty, visor);
			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "loginByMobileAndPwd", //
			des = "手机号密码登录", //
			ret = "LoginBO对象，包含user，session等信息"//
	)
	public APIResponse loginByMobileAndPwd(//
			@P(t = "手机号") String mobile, //
			@P(t = "密码") String pwd//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse
					.getNewSuccessResp(ServiceUtils.checkNull(orgService.loginByMobileAndPwd(conn, mobile, pwd)));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "loginInORG", //
			des = "登录到组织", //
			ret = "ORGUserBo对象，包含user，session及org等信息"//
	)
	public APIResponse loginInORG(//
			@P(t = "用户编号") Long userId, //
			@P(t = "组织编号") Long orgId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(ServiceUtils.checkNull(orgService.loginInORG(conn, userId, orgId)));
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
	@POSTAPI(//
			path = "getORGDirectors", //
			des = "获取组织董事会成员列表", //
			ret = "ORGRole组织对象列表"//
	)
	public APIResponse getORGDirectors(//
			@P(t = "组织编号") Long orgId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGDirectors(conn, orgId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getORGShareholders", //
			des = "获取组织股东会成员列表", //
			ret = "ORGRole组织对象列表"//
	)
	public APIResponse getORGShareholders(//
			@P(t = "组织编号") Long orgId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGShareholders(conn, orgId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getORGSupervisors", //
			des = "获取组织监事会成员列表", //
			ret = "ORGRole组织对象列表"//
	)
	public APIResponse getORGSupervisors(//
			@P(t = "组织编号") Long orgId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(orgService.getORGSupervisors(conn, orgId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getORGRolesLikeIDNumber", //
			des = "根据组织编号和身份证号片段（生日），模糊查询", //
			ret = "ORGRole组织对象列表"//
	)
	public APIResponse getORGRolesLikeIDNumber(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "身份证编号（片段即可），模糊查询") String idNumber, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse
					.getNewSuccessResp(orgService.getORGRolesLikeIDNumber(conn, orgId, idNumber, count, offset));
		}
	}

	//////////////////////////// assets

	// /**
	// *
	// */
	// @POSTAPI(//
	// path = "createAsset", //
	// des = "创建资产", //
	// ret = "所创建的对象"//
	// )
	// public APIResponse createAsset(//
	// @P(t = "组织编号") Long orgId, //
	// @P(t = "标题") String title, //
	// @P(t = "备注") String remark, //
	// @P(t = "数据（JSON）") String data //
	// ) throws Exception {
	// try (DruidPooledConnection conn = (DruidPooledConnection)
	// dsRds.openConnection()) {
	// return APIResponse.getNewSuccessResp(assetService.createAsset(conn, orgId,
	// title, remark, data));
	// }
	// }
	//
	// /**
	// *
	// */
	// @POSTAPI(//
	// path = "editAsset", //
	// des = "编辑资产", //
	// ret = "受影响的记录数"//
	// )
	// public APIResponse editAsset(//
	// @P(t = "组织编号") Long assetId, //
	// @P(t = "标题") String title, //
	// @P(t = "备注") String remark, //
	// @P(t = "数据（JSON）") String data //
	// ) throws Exception {
	// try (DruidPooledConnection conn = (DruidPooledConnection)
	// dsRds.openConnection()) {
	// return APIResponse.getNewSuccessResp(assetService.editAsset(conn, assetId,
	// title, remark, data));
	// }
	// }

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delAsset", //
			des = "删除资产", //
			ret = "受影响的记录数"//
	)
	public APIResponse delAsset(//
			@P(t = "组织编号") Long assetId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(assetService.delAsset(conn, assetId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getAssets", //
			des = "获取组织的资产列表", //
			ret = "Asset对象列表"//
	)
	public APIResponse getAssets(//
			@P(t = "组织编号") Long orgId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(assetService.getAssets(conn, orgId, count, offset));
		}
	}

}
