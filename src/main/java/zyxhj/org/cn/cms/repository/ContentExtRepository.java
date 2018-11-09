package zyxhj.org.cn.cms.repository;

import zyxhj.org.cn.cms.domain.ContentExt;
import zyxhj.org.cn.utils.data.rds.RDSRepository;

public class ContentExtRepository extends RDSRepository<ContentExt> {

	private static ContentExtRepository ins;

	public static synchronized ContentExtRepository getInstance() {
		if (null == ins) {
			ins = new ContentExtRepository();
		}
		return ins;
	}

	private ContentExtRepository() {
		super(ContentExt.class);
	}

}
