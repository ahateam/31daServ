package zyxhj.org.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

@RDSAnnEntity(alias = "tb_org_vote")
public class Vote {

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

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 投票项目编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long projectId;

	/**
	 * 类型</br>
	 * 单选，多选
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 多选数量限制
	 */
	@RDSAnnField(column = RDSAnnField.INTEGER)
	public Integer selectionLimit;

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
	 * 结果（JSON）
	 */
	@RDSAnnField(column = RDSAnnField.SHORT_TEXT)
	public String outcome;

}
