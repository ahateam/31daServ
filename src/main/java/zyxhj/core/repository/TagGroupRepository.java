package zyxhj.core.repository;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.TagGroup;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class TagGroupRepository extends RDSRepository<TagGroup> {

	public TagGroupRepository() {
		super(TagGroup.class);
	}

	public int updateCumtomTagGroup(DruidPooledConnection conn, String keyword, String remark) throws ServerException {
		TagGroup renew = new TagGroup();
		renew.remark = remark;

		// 系统标签不允许修改
		return this.update(conn, "WHERE keyword=? AND type<>0", new Object[] { keyword }, renew, true);
	}
}
