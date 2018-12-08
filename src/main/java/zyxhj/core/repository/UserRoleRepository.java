package zyxhj.core.repository;

import zyxhj.core.domain.UserRole;
import zyxhj.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRoleRepository extends RDSRepository<UserRole> {

	private static UserRoleRepository ins;

	public static synchronized UserRoleRepository getInstance() {
		if (null == ins) {
			ins = new UserRoleRepository();
		}
		return ins;
	}

	private UserRoleRepository() {
		super(UserRole.class);
	}

}
