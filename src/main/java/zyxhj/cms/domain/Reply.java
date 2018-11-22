package zyxhj.cms.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;

/**
 * 回复，暂时没设计
 *
 */
@RDSAnnEntity(alias = "tb_reply")
public class Reply {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 被回复对象的编号</br>
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long objId;

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
	 * 上传用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long upUserId;

	/**
	 * 被@的用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long atUserId;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 内容信息（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

}
