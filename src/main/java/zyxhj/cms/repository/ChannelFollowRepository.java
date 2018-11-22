package zyxhj.cms.repository;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.cms.domain.ChannelFollow;
import zyxhj.org.cn.utils.data.rds.RDSRepository;

public class ChannelFollowRepository extends RDSRepository<ChannelFollow> {

	private static ChannelFollowRepository ins;

	public static synchronized ChannelFollowRepository getInstance() {
		if (null == ins) {
			ins = new ChannelFollowRepository();
		}
		return ins;
	}

	private ChannelFollowRepository() {
		super(ChannelFollow.class);
	}

	public ChannelFollow getByUserIdAndChannelId(DruidPooledConnection conn, Long userId, Long channelId)
			throws Exception {
		return get(conn, "WHERE user_id=? AND channel_id=?", new Object[] { userId, channelId });
	}

	public int deleteByUserIdAndChannelId(DruidPooledConnection conn, Long userId, Long channelId) throws Exception {
		return delete(conn, "WHERE user_id=? AND channel_id=?", new Object[] { userId, channelId });
	}

}
