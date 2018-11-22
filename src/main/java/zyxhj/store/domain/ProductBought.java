package zyxhj.store.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;

/**
 * 用户已购买对象
 *
 */
@RDSAnnEntity(alias = "tb_product_bought")
public class ProductBought {

	/**
	 * 用户编号，ID列，无需索引
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 已购商品类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 内容编号，ID列，无需索引
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long productId;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

}
