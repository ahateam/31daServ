package zyxhj.org.cn.cms.repository;

import zyxhj.org.cn.cms.domain.Channel;
import zyxhj.org.cn.utils.data.rds.RDSRepository;

public class ChannelRepository extends RDSRepository<Channel> {

	private static ChannelRepository ins;

	public static synchronized ChannelRepository getInstance() {
		if (null == ins) {
			ins = new ChannelRepository();
		}
		return ins;
	}

	private ChannelRepository() {
		super(Channel.class);
	}

}
