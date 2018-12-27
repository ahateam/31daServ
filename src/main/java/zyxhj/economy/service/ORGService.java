package zyxhj.economy.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.LoginBo;
import zyxhj.core.domain.User;
import zyxhj.core.domain.UserSession;
import zyxhj.core.repository.UserRepository;
import zyxhj.economy.domain.ORG;
import zyxhj.economy.domain.ORGRole;
import zyxhj.economy.domain.ORGUserBo;
import zyxhj.economy.repository.ORGRepository;
import zyxhj.economy.repository.ORGRoleRepository;
import zyxhj.utils.CacheCenter;
import zyxhj.utils.ExcelUtils;
import zyxhj.utils.IDUtils;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class ORGService {

	private static Logger log = LoggerFactory.getLogger(ORGService.class);

	private static ORGService ins;

	public static synchronized ORGService getInstance() {
		if (null == ins) {
			ins = new ORGService();
		}
		return ins;
	}

	private ORGRepository orgRepository;
	private ORGRoleRepository orgRoleRepository;
	private UserRepository userRepository;

	private ORGService() {
		try {
			orgRepository = ORGRepository.getInstance();
			orgRoleRepository = ORGRoleRepository.getInstance();
			userRepository = UserRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 从UserService拷贝过来的
	 */
	public UserSession putUserSession(Long userId, Date loginTime, String loginToken) throws Exception {

		UserSession ret = new UserSession();
		ret.userId = userId;
		ret.loginTime = loginTime;
		ret.loginToken = loginToken;

		// 先放入Session缓存，再放入存储
		CacheCenter.SESSION_CACHE.put(userId, ret);
		return ret;
	}

	/**
	 * 从UserService拷贝过来的
	 */
	private LoginBo login(DruidPooledConnection conn, User user) throws Exception {
		Date loginTime = new Date();
		UserSession userSession = putUserSession(user.id, loginTime, IDUtils.getHexSimpleId());

		LoginBo ret = new LoginBo();
		ret.id = user.id;
		ret.name = user.name;
		ret.realName = user.realName;
		ret.nickname = user.nickname;
		ret.signature = user.signature;

		ret.idNumber = user.idNumber;
		ret.mobile = user.mobile;

		ret.roles = user.roles;

		ret.loginTime = userSession.loginTime;
		ret.loginToken = userSession.loginToken;

		return ret;
	}

	/**
	 * 从UserService拷贝过来的，然后简单修改，增加了组织信息
	 */
	private ORGUserBo login2(DruidPooledConnection conn, User user, ORGRole role) throws Exception {
		Date loginTime = new Date();
		UserSession userSession = putUserSession(user.id, loginTime, IDUtils.getHexSimpleId());

		ORGUserBo ret = new ORGUserBo();

		ret.id = user.id;
		ret.name = user.name;
		ret.nickname = user.nickname;
		ret.signature = user.signature;
		ret.mobile = user.mobile;

		ret.roles = user.roles;

		ret.loginTime = userSession.loginTime;
		ret.loginToken = userSession.loginToken;

		ret.orgId = role.orgId;
		ret.realName = role.realName;
		ret.idNumber = role.idNumber;
		ret.share = role.share;
		ret.shareAmount = role.shareAmount;
		ret.weight = role.weight;
		ret.duty = role.duty;
		ret.visor = role.visor;

		return ret;
	}

	/**
	 * 创建组织，按组织机构代码证排重
	 */
	public ORG createORG(DruidPooledConnection conn, String name, String code, String province, String city,
			String district, String address, String imgOrg, String imgAuth, Integer shareAmount) throws Exception {
		ORG eistORG = orgRepository.getByKey(conn, "code", code);
		if (null == eistORG) {
			// 组织不存在
			ORG newORG = new ORG();

			newORG.id = IDUtils.getSimpleId();
			newORG.createTime = new Date();
			newORG.name = name;
			newORG.code = code;
			newORG.province = province;
			newORG.city = city;
			newORG.district = district;
			newORG.address = address;
			newORG.imgOrg = imgOrg;
			newORG.imgAuth = imgAuth;
			newORG.shareAmount = shareAmount;

			orgRepository.insert(conn, newORG);

			return newORG;
		} else {
			// 组织已存在
			throw new ServerException(BaseRC.ECM_ORG_EXIST);
		}

	}

	/**
	 * 更新组织信息，目前全都可以改，将来应该限定code，name等不允许更改</br>
	 * 填写空表示不更改
	 */
	public void setORG(DruidPooledConnection conn, Long orgId, String province, String city, String district,
			String address, String imgOrg, String imgAuth, Integer shareAmount) throws Exception {

		ORG renew = new ORG();
		renew.id = orgId;
		renew.province = province;
		renew.city = city;
		renew.district = district;
		renew.address = address;
		renew.imgOrg = imgOrg;
		renew.imgAuth = imgAuth;
		renew.shareAmount = shareAmount;

		orgRepository.updateByKey(conn, "id", orgId, renew, true);
	}

	/**
	 * 获取全部组织列表
	 */
	public List<ORG> getORGs(DruidPooledConnection conn, int count, int offset) throws Exception {
		return orgRepository.getList(conn, count, offset);
	}

	/**
	 * 获取全部组织列表
	 */
	public ORG getORGById(DruidPooledConnection conn, Long orgId) throws Exception {
		return orgRepository.getByKey(conn, "id", orgId);
	}

	/**
	 * 获取用户对应的组织列表
	 */
	public List<ORG> getUserORGs(DruidPooledConnection conn, Long userId) throws Exception {
		List<ORGRole> ors = orgRoleRepository.getListByKey(conn, "user_id", userId, 512, 0);
		if (ors == null || ors.size() == 0) {
			return new ArrayList<ORG>();
		} else {
			String[] values = new String[ors.size()];
			for (int i = 0; i < ors.size(); i++) {
				values[i] = ors.get(i).orgId.toString();
			}
			return orgRepository.getListByKeyInValues(conn, "id", values);
		}
	}

	/**
	 * 创建股东</br>
	 * 创建特殊的user，ext中存放股东的投票权重shareWeight
	 */
	public void importUser(DruidPooledConnection conn, Long orgId, String mobile, String realName, String idNumber,
			Byte share, Integer shareAmount, Integer weight, Byte duty, Byte visor) throws Exception {
		User existUser = userRepository.getByKey(conn, "id_number", idNumber);
		if (null == existUser) {
			// 用户不存在，直接创建

			User newUser = new User();
			newUser.id = IDUtils.getSimpleId();
			newUser.createDate = new Date();
			newUser.realName = realName;
			newUser.mobile = mobile;
			newUser.idNumber = idNumber;

			// 默认密码,身份证后6位
			newUser.pwd = idNumber.substring(idNumber.length() - 6);

			// 创建用户
			userRepository.insert(conn, newUser);

			// 写入股东信息表
			ORGRole or = new ORGRole();
			or.orgId = orgId;
			or.userId = newUser.id;

			or.realName = realName;
			or.idNumber = idNumber;
			or.share = share;
			or.shareAmount = shareAmount;
			or.weight = weight;
			or.duty = duty;
			or.visor = visor;

			orgRoleRepository.insert(conn, or);
		} else {
			// 用户根据身份证号判定重复，其它信息不更改
			// 用户信息不更新，只更新股东信息表
			ORGRole renew = new ORGRole();
			renew.share = share;
			renew.shareAmount = shareAmount;
			renew.weight = weight;
			renew.duty = duty;
			renew.visor = visor;

			orgRoleRepository.updateByKeys(conn, new String[] { "org_id", "user_id" },
					new Object[] { orgId, existUser.id }, renew, true);
		}
	}

	/**
	 * 修改用户信息，身份证信息不能修改
	 */
	public int updateUser(DruidPooledConnection conn, Long userId, String mobile, String realName, String pwd)
			throws Exception {
		User renew = new User();
		renew.mobile = mobile;
		renew.realName = realName;
		renew.pwd = pwd;

		return userRepository.updateByKey(conn, "id", userId, renew, true);
	}

	/**
	 * 移除组织的用户</br>
	 * 只修改ORGRole表，不删除user本身。
	 */
	public void removeORGUser(DruidPooledConnection conn, Long orgId, Long userId) throws Exception {
		orgRoleRepository.deleteByKeys(conn, new String[] { "org_id", "user_id" }, new Object[] { orgId, userId });
	}

	/**
	 * 修改组织的用户</br>
	 * 只修改ORGRole表，不变动user本身。
	 */
	public int updateORGUser(DruidPooledConnection conn, Long orgId, Long userId, Byte share, Integer weight, Byte duty,
			Byte visor) throws Exception {
		ORGRole renew = new ORGRole();
		renew.share = share;
		renew.weight = weight;
		renew.duty = duty;
		renew.visor = visor;

		return orgRoleRepository.updateByKeys(conn, new String[] { "org_id", "user_id" },
				new Object[] { orgId, userId }, renew, true);
	}

	/**
	 * 成员登录
	 * 
	 * @param mobile
	 *            电话号码
	 * @param pwd
	 *            密码
	 * @param 登录业务对象
	 */
	public LoginBo loginByMobileAndPwd(DruidPooledConnection conn, String mobile, String pwd) throws Exception {
		User existUser = userRepository.getByKey(conn, "mobile", mobile);
		if (null == existUser) {
			// 用户不存在
			throw new ServerException(BaseRC.USER_NOT_EXIST);
		} else {
			// 用户已存在，匹配密码
			// TODO 目前是明文，需要加料然后匹配
			if (pwd.equals(existUser.pwd)) {

				return login(conn, existUser);
			} else {
				// 密码错误
				throw new ServerException(BaseRC.USER_PWD_ERROR);
			}
		}
	}

	/**
	 * 不够严谨的组织登录，暂时没有更好的办法
	 */
	public ORGUserBo loginInORG(DruidPooledConnection conn, Long userId, Long orgId) throws Exception {
		ORGRole role = orgRoleRepository.getByKeys(conn, new String[] { "org_id", "user_id" },
				new Object[] { orgId, userId });
		User user = userRepository.getByKey(conn, "id", userId);
		ServiceUtils.checkNull(role);
		ServiceUtils.checkNull(user);

		return login2(conn, user, role);
	}

	/**
	 * 获取组织的董事会成员
	 */
	public JSONArray getORGDirectors(DruidPooledConnection conn, Long orgId, int count, int offset) throws Exception {
		List<ORGRole> ors = orgRoleRepository.getDirectors(conn, orgId, count, offset);
		return getORGUserByRole(conn, ors);
	}

	/**
	 * 获取组织的股东成员
	 */
	public JSONArray getORGShareholders(DruidPooledConnection conn, Long orgId, int count, int offset)
			throws Exception {
		List<ORGRole> ors = orgRoleRepository.getShareholders(conn, orgId, count, offset);
		return getORGUserByRole(conn, ors);
	}

	private JSONArray getORGUserByRole(DruidPooledConnection conn, List<ORGRole> ors) throws Exception {
		if (ors == null || ors.size() == 0) {
			return new JSONArray();
		} else {
			String[] values = new String[ors.size()];
			for (int i = 0; i < ors.size(); i++) {
				values[i] = ors.get(i).userId.toString();
			}
			List<User> us = userRepository.getListByKeyInValues(conn, "id", values);
			JSONArray ret = new JSONArray();
			for (int i = 0; i < ors.size(); i++) {
				ORGRole or = ors.get(i);
				for (int j = 0; j < us.size(); j++) {
					User u = us.get(j);
					u.pwd = null;
					if (or.userId.equals(u.id)) {
						// 找到匹配的
						JSONObject jo = new JSONObject();
						jo.put("user", u);
						jo.put("role", or);
						ret.add(jo);
						break;
					}
				}
			}
			return ret;
		}
	}

	/**
	 * 获取组织的监事会成员
	 */
	public JSONArray getORGSupervisors(DruidPooledConnection conn, Long orgId, int count, int offset) throws Exception {
		List<ORGRole> ors = orgRoleRepository.getSuperVisors(conn, orgId, count, offset);
		return getORGUserByRole(conn, ors);
	}

	/**
	 * 根据组织编号和身份证号片段（生日），模糊查询
	 */
	public List<ORGRole> getORGRolesLikeIDNumber(DruidPooledConnection conn, Long orgId, String idNumber, Integer count,
			Integer offset) throws Exception {
		return orgRoleRepository.getORGRolesLikeIDNumber(conn, orgId, idNumber, count, offset);
	}

	/**
	 * 导入组织用户列表
	 */
	public void importORGUsers(DruidPooledConnection conn, Long orgId, String url) throws Exception {
		// 1行表头，8列，文件格式写死的
		List<List<Object>> table = ExcelUtils.readExcelOnline(url, 1, 8, 0);

		for (List<Object> row : table) {

			String realName = ExcelUtils.getString(row.get(0));
			String idNumber = ExcelUtils.getString(row.get(1));
			String mobile = ExcelUtils.getString(row.get(2));
			String rShare = ExcelUtils.getString(row.get(3));
			Integer shareAmount = ExcelUtils.parseInt(row.get(4));
			Integer weight = ExcelUtils.parseInt(row.get(5));
			String rDuty = ExcelUtils.getString(row.get(6));
			String rVisor = ExcelUtils.getString(row.get(7));

			Byte share = ORGRole.SHARE.NONE.v();
			if (rShare.equals("股东户代表")) {
				share = ORGRole.SHARE.REPRESENTATIVE.v();
			} else if (rShare.equals("股东")) {
				share = ORGRole.SHARE.SHAREHOLDER.v();
			} else {
				share = ORGRole.SHARE.NONE.v();
			}

			Byte duty = ORGRole.DUTY.NONE.v();
			if (rDuty.equals("董事长")) {
				duty = ORGRole.DUTY.CHAIRMAN.v();
			} else if (rDuty.equals("副董事长")) {
				duty = ORGRole.DUTY.VICE_CHAIRMAN.v();
			} else if (rDuty.equals("董事")) {
				duty = ORGRole.DUTY.DIRECTOR.v();
			} else {
				duty = ORGRole.DUTY.NONE.v();
			}

			Byte visor = ORGRole.VISOR.NONE.v();
			if (rVisor.equals("监事长")) {
				visor = ORGRole.VISOR.CHAIRMAN.v();
			} else if (rVisor.equals("副监事长")) {
				visor = ORGRole.VISOR.VICE_CHAIRMAN.v();
			} else if (rVisor.equals("监事")) {
				visor = ORGRole.VISOR.SUPERVISOR.v();
			} else {
				visor = ORGRole.DUTY.NONE.v();
			}

			importUser(conn, orgId, mobile, realName, idNumber, share, shareAmount, weight, duty, visor);
		}
	}
}
