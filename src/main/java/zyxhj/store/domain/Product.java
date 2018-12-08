package zyxhj.store.domain;

import java.util.Date;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 商品
 */
@RDSAnnEntity(alias = "tb_product")
public class Product {

	public static final Byte STATUS_ENABLE = 0;
	public static final Byte STATUS_DISABLE = 1;

	public static final Byte TYPE_EXPRESS = 0;// 快递物流
	public static final Byte TYPE_DELIVER = 1;// 自行配送
	public static final Byte TYPE_AUTOMATIC = 2;// 自动（电子商品等等）

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 库存
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer stock;

	/**
	 * 成本价
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double costPrice;

	/**
	 * 市场价
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double marketPrice;

	/**
	 * 售价
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double price;

	/**
	 * 会员价
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double memberPrice;

	/**
	 * 牛逼的JSON
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

	/**
	 * 牛逼的JSON
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;

}
