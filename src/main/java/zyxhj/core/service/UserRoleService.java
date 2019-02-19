package zyxhj.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.UserRole;
import zyxhj.core.repository.UserRoleRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.Singleton;

/**
 * 用户角色service
 *
 */
public class UserRoleService {

	private static Logger log = LoggerFactory.getLogger(UserRoleService.class);

	private UserRoleRepository roleRepository;

	public UserRoleService() {
		try {
			roleRepository = Singleton.ins(UserRoleRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建自定义角色
	 */
	public UserRole createUserRole(DruidPooledConnection conn, String name, String remark) throws Exception {
		UserRole role = new UserRole();
		role.id = IDUtils.getSimpleId();
		role.name = name;
		role.remark = remark;

		roleRepository.insert(conn, role);

		return role;
	}

	/**
	 * 编辑自定义角色
	 */
	public int editUserRole(DruidPooledConnection conn, Long roleId, String name, String remark) throws Exception {
		UserRole renew = new UserRole();
		renew.name = name;
		renew.remark = remark;

		return roleRepository.updateByKey(conn, "id", roleId, renew, true);
	}

	/**
	 * 删除自定义角色
	 */
	public int delUserRole(DruidPooledConnection conn, Long roleId) throws Exception {
		return roleRepository.deleteByKey(conn, "id", roleId);
	}

	/**
	 * 获取系统角色列表
	 */
	public List<UserRole> getUserRoles(DruidPooledConnection conn, Integer count, Integer offset) throws Exception {
		return roleRepository.getList(conn, count, offset);
	}

}
