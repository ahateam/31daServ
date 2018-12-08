package zyxhj.store.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_order")
public class Order {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 门店编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long storeId;

	/**
	 * 商品编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long productId;

	/**
	 * 买家用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long buyerId;

	/**
	 * 卖家推广员用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long pramoterId;

	/**
	 * 分销用户编号（只记录直接上级）
	 */
	public Long resellerId;

	/**
	 * 商品售价
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double productPrice;

	/**
	 * 最终成交价
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double donePrice;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

}
