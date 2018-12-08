package zyxhj.core.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 回复配置
 *
 */
@RDSAnnEntity(alias = "tb_reply")
public class Reply {
	
	public static final Byte STATUS_OPEN = 0;
	public static final Byte STATUS_CLOSE = 1;
	

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

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
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

}
