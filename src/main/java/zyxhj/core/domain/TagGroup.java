package zyxhj.core.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_tag_group")
public class TagGroup {

	public static final Byte TYPE_SYS = 0;
	public static final Byte TYPE_CUSTOM = 1;

	/**
	 * 分组关键字（分类前缀 + 关键字）
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String keyword;

	/**
	 * 分组类型（系统，自定义）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

}
