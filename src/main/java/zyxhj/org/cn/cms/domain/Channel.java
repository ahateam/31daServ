package zyxhj.org.cn.cms.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;

/**
 * 内容频道（专栏）实体
 *
 */
@RDSAnnEntity(alias = "tb_channel")
public class Channel {

	public static final Byte STATUS_NORMAL = 0; // 正常
	public static final Byte STATUS_STOP = 1; // 已停止更新
	public static final Byte STATUS_CLOSED = 2; // 已关闭禁用
	public static final Byte STATUS_DELETED = 3; // 已删除

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 状态（暂时没有作用）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 更新时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date updateTime;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 数据（JSON形式）
	 */
	@RDSAnnField(column = RDSAnnField.TEXT)
	public String data;

}
