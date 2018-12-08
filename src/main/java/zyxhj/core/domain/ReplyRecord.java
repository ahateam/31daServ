package zyxhj.core.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;
import zyxhj.utils.data.rds.RDSAnnIndex;

/**
 * 回复记录
 *
 */
@RDSAnnEntity(alias = "tb_reply_record")
public class ReplyRecord {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 所属replay的编号
	 */
	@RDSAnnIndex(type = RDSAnnIndex.NORMAL)
	@RDSAnnField(column = RDSAnnField.ID)
	public Long replyId;

	/**
	 * 被回复对象的编号</br>
	 */
	@RDSAnnIndex(type = RDSAnnIndex.NORMAL)
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
	 * 文本
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String text;

	/**
	 * 扩展信息（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String ext;

}
