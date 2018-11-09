package zyxhj.org.cn.cms.repository;

import zyxhj.org.cn.core.domain.Tag;
import zyxhj.org.cn.utils.data.rds.RDSRepository;

public class ChannelTagRepository extends RDSRepository<Tag> {

	private static ChannelTagRepository ins;

	public static synchronized ChannelTagRepository getInstance() {
		if (null == ins) {
			ins = new ChannelTagRepository();
		}
		return ins;
	}

	private ChannelTagRepository() {
		super(Tag.class);
	}

}
