package zyxhj.cms.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 用户与专栏的关系表</br>
 * （用户关注专栏）
 *
 */
@RDSAnnEntity(alias = "tb_channel_follow")
public class ChannelFollow {

	/**
	 * 频道编号，ID列，无需索引
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long channelId;

	/**
	 * 用户编号，ID列，无需索引
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 关注时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

}
