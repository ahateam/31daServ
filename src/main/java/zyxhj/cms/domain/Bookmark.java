package zyxhj.cms.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 用户已购买内容
 *
 */
@RDSAnnEntity(alias = "tb_cms_bookmark")
public class Bookmark {

	/**
	 * 用户编号，ID列，无需索引
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 内容编号，ID列，无需索引
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long contentId;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

}
