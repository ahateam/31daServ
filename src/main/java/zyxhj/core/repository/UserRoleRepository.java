package zyxhj.core.repository;

import zyxhj.core.domain.UserRole;
import zyxhj.utils.data.rds.RDSRepository;

/**
 * 
 */
public class UserRoleRepository extends RDSRepository<UserRole> {

	public UserRoleRepository() {
		super(UserRole.class);
	}

}
