package zyxhj.economy.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 表决（投票）
 *
 */
@RDSAnnEntity(alias = "tb_ecm_vote")
public class Vote {

	public static enum STATUS implements ENUMVALUE {
		VOTING((byte) 0, "投票中"), //
		WAITING((byte) 1, "等待中"), //
		INVALID((byte) 2, "已作废"), //
		DONE((byte) 3, "已结束"), //
		PAUSED((byte) 4, "人为作废"), //
		;

		private byte v;
		private String txt;

		private STATUS(byte v, String txt) {
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

	public static enum TEMPLATE implements ENUMVALUE {
		ELECT((byte) 0, "选举"), //
		VOTE((byte) 1, "投票"), //
		;

		private byte v;
		private String txt;

		private TEMPLATE(byte v, String txt) {
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

	public static enum TYPE implements ENUMVALUE {
		SINGLE((byte) 0, "单选"), //
		MULTIPLE((byte) 1, "多选"), //
		MULTIPLE_BALLOT((byte) 2, "按用户持有票数限制的多选"), //
		;

		private byte v;
		private String txt;

		private TYPE(byte v, String txt) {
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

	public static enum CROWD implements ENUMVALUE {
		ALL((byte) 0, "所有人"), //
		SHAREHOLDER((byte) 1, "股东"), //
		REPRESENTATIVE((byte) 2, "股东代表"), //
		DIRECTOR((byte) 3, "董事会"), //
		SUPERVISOR((byte) 4, "监事会"), //
		;

		private byte v;
		private String txt;

		private CROWD(byte v, String txt) {
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

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long orgId;

	/**
	 * 组织编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long projectId;

	/**
	 * 模版</br>
	 * 选举，投票
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte template;

	/**
	 * 类型</br>
	 * 单选，多选
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 最多选择的数量，前端限制
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte choiceCount;

	/**
	 * 状态</br>
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 参加投票项目的人群
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String crowd;

	/**
	 * 用户在有效期内是否可以重新编辑选票
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean reeditable;

	/**
	 * 是否实名制
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean realName;

	/**
	 * 是否内部投票（外部可允许任何人参与，用于意见采集）
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean isInternal;

	/**
	 * 是否弃权选项
	 */
	@RDSAnnField(column = RDSAnnField.BOOLEAN)
	public Boolean isAbstain;

	/**
	 * 自动生效人数比例（百分率，整数部分）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte effectiveRatio;

	/**
	 * 自动失效人数比例（百分率，整数部分）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte failureRatio;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 备注
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String remark;

	/**
	 * 扩展信息（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String ext;

	/**
	 * 法定人数
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer quorum;

	/**
	 * 实到人数（有签到才能生效）
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer attended;

	/**
	 * VoteOption编号列表，包含顺序（JSONArray）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String optionIds;
}
