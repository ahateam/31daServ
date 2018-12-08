package zyxhj.store.domain;

import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 支付记录
 *
 */
@RDSAnnEntity(alias = "tb_payment_record")
public class PaymentRecord {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnField(column = RDSAnnField.ID)
	public Long providerId;

	/**
	 * 支付金额总数
	 */
	@RDSAnnField(column = RDSAnnField.DOUBLE)
	public Double amount;

	/**
	 * 支付状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;
}
