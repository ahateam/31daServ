package zyxhj.economy.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 组织
 *
 */
@RDSAnnEntity(alias = "tb_ecm_org")
public class ORG {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 名称
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

	/**
	 * 组织机构代码
	 */
	@RDSAnnField(column = "VARCHAR(64)")
	public String code;

	/**
	 * 省
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String province;

	/**
	 * 市
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String city;

	/**
	 * 区
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String district;

	/**
	 * 具体地址
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String address;

	/**
	 * 组织机构证书图片
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String imgOrg;

	/**
	 * 授权证书图片
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String imgAuth;

	/**
	 * 股份份数总数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer shareAmount;

}
