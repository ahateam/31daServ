package zyxhj.core.domain;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 通用标签
 */
@RDSAnnEntity(alias = "tb_tag")
public class Tag {

	public static enum STATUS implements ENUMVALUE {
		DISABLED((byte) 0, "禁用"), //
		ENABLED((byte) 1, "启用"), //
		;

		private byte v;
		private String txt;

		private STATUS(Byte v, String txt) {
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
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 分组关键字
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String groupKeyword;

	/**
	 * 标签名称，用于展示阅读</br>
	 */
	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.TEXT_NAME)
	public String name;

}
