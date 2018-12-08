package zyxhj.core.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_tag_type")
public class TagType {


	/**
	 * 标签编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 大种类，用于标签自身的管理</br>
	 * 小写英文
	 */
	@RDSAnnID
	@RDSAnnField(column = "VARCHAR(32)")
	public String kind;

	/**
	 * 小类型，用于标签自身的管理</br>
	 * 小写英文
	 */
	@RDSAnnID
	@RDSAnnField(column = "VARCHAR(32)")
	public String type;

	/**
	 * 标签名称，用于显示和存储
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

}
