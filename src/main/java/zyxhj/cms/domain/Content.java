package zyxhj.cms.domain;

import java.util.Date;

import zyxhj.utils.api.Controller.ENUMVALUE;
import zyxhj.utils.data.rds.RDSAnnEntity;
import zyxhj.utils.data.rds.RDSAnnField;
import zyxhj.utils.data.rds.RDSAnnID;

/**
 * 
 * 单个的内容实体
 */
@RDSAnnEntity(alias = "tb_cms_content")
public class Content {

	public static enum TYPE implements ENUMVALUE {
		ALBUM((byte) 0, "相册"), //
		AUDIO((byte) 1, "音频"), //
		VIDEO_CLIP((byte) 2, "短视频"), //
		VIDEO((byte) 3, "视频"), //
		LIVE((byte) 4, "直播"), //
		H5((byte) 5, "H5文本"), //
		POST((byte) 6, "帖子"), //
		SET((byte) 7, "内容集合"),//
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

	public static enum STATUS implements ENUMVALUE {
		DRAFT((byte) 0, "草稿"), //
		NORMAL((byte) 1, "正常"), //
		CLOSED((byte) 2, "已关闭"), //
		DELETED((byte) 3, "已删除"), //
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

	public static final Byte LEVEL_PUBLIC = 0; // 完全公开

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long id;

	/**
	 * 内容类型
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte type;

	/**
	 * 状态
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte status;

	/**
	 * 分级（用于权限控制）</br>
	 * （未实现）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte level;

	/**
	 * 创建时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date createTime;

	/**
	 * 更新时间
	 */
	@RDSAnnField(column = RDSAnnField.TIME)
	public Date updateTime;

	/**
	 * 上传用户编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long upUserId;

	/**
	 * 上传专栏编号
	 */
	@RDSAnnField(column = RDSAnnField.ID)
	public Long upChannelId;

	/**
	 * 标题
	 */
	@RDSAnnField(column = RDSAnnField.TEXT_TITLE)
	public String title;

	/**
	 * 数据</br>
	 * JSON形式存储内容信息结构体，具体结构体视项目而定
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

	/**
	 * 牛逼的JSON
	 */
	@RDSAnnField(column = RDSAnnField.JSON)
	public String tags;

}
