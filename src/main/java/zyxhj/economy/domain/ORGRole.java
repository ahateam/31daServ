package zyxhj.economy.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 组织董事表
 *
 */
@RDSAnnEntity(alias = "tb_ecm_org_role")
public class ORGRole {

	public static enum SHARE implements ENUMVALUE {
		NONE((byte) 20, "非股东"), //
		SHAREHOLDER((byte) 21, "股东"), //
		REPRESENTATIVE((byte) 22, "股东代表"), //
		;

		private byte v;
		private String txt;

		private SHARE(byte v, String txt) {
			this.v = v;
			this.txt = txt;
		}

		@Override
		public byte v() {
			return v;
		}

		@Override
		public String txt() {
			return txt;
		}
	}

	public static enum DUTY implements ENUMVALUE {
		NONE((byte) 10, "非董事"), //
		DIRECTOR((byte) 11, "董事"), //
		CHAIRMAN((byte) 12, "董事长"), //
		VICE_CHAIRMAN((byte) 13, "副董事长"), //
		;

		private byte v;
		private String txt;

		private DUTY(byte v, String txt) {
			this.v = v;
			this.txt = txt;
		}

		@Override
		public byte v() {
			return v;
		}

		@Override
		public String txt() {
			return txt;
		}
	}

	public static enum VISOR implements ENUMVALUE {
		NONE((byte) 10, "非监事"), //
		SUPERVISOR((byte) 11, "监事"), //
		CHAIRMAN((byte) 12, "监事长"), //
		VICE_CHAIRMAN((byte) 13, "副监事长"), //
		;

		private byte v;
		private String txt;

		private VISOR(byte v, String txt) {
			this.v = v;
			this.txt = txt;
		}

		@Override
		public byte v() {
			return v;
		}

		@Override
		public String txt() {
			return txt;
		}
	}

	/**
	 * 组织编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orgId;

	/**
	 * 用户编号
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long userId;

	/**
	 * 用户真名（数据冗余）
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String realName;

	/**
	 * 身份证号（数据冗余）
	 */
	@RDSAnnField(column = "VARCHAR(32)")
	public String idNumber;

	/**
	 * 股份类型（NONE，董事，副董事长，董事长）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte share;

	/**
	 * 股份数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer shareAmount;

	/**
	 * 投票权重
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer weight;

	/**
	 * 董事会职务类型（NONE，董事，副董事长，董事长）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte duty;

	/**
	 * 监事会职务类型（NONE，监事，副监事长，监事长）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte visor;
}
