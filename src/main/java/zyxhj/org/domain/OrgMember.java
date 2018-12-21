package zyxhj.org.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 组织董事表
 *
 */
@RDSAnnEntity(alias = "tb_org_org_member")
public class OrgMember {

	public static enum SHAREHOLDERS implements ENUMVALUE {
		NONE((byte) 10, "非股东"), //
		SHAREHOLDER((byte) 11, "股东"), //
		REPRESENTATIVE((byte) 12, "股东代表"), //
		;

		private byte v;
		private String txt;

		private SHAREHOLDERS(byte v, String txt) {
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

	public static enum DIRECTORATE implements ENUMVALUE {
		NONE((byte) 20, "非董事"), //
		DIRECTOR((byte) 21, "董事"), //
		CHAIRMAN((byte) 22, "董事长"), //
		VICE_CHAIRMAN((byte) 23, "副董事长"), //
		;

		private byte v;
		private String txt;

		private DIRECTORATE(byte v, String txt) {
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

	public static enum SUPERVISORS implements ENUMVALUE {
		NONE((byte) 30, "非监事"), //
		SUPERVISOR((byte) 31, "监事"), //
		CHAIRMAN((byte) 32, "监事长"), //
		VICE_CHAIRMAN((byte) 33, "副监事长"), //
		;

		private byte v;
		private String txt;

		private SUPERVISORS(byte v, String txt) {
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
	 * 股份数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer shareAmount;

	/**
	 * 持有的总股份数（自己的部分 + 代持的部分）
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer proxyShareAmount;

	/**
	 * 持有票数（默认为1，0代表不能投票，1以上表示代理其他人的票数）
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer voteCount;

	/**
	 * 标签（职务等标签）
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;
}
