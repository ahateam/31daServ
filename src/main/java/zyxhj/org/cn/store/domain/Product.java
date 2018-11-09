package zyxhj.org.cn.store.domain;

import java.util.Date;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;

/**
 * 
 * 单个的内容实体
 */
@RDSAnnEntity(alias = "tb_content")
public class Product {

	public static final Byte TYPE_ALBUM = 0;// 相册
	public static final Byte TYPE_AUDIO = 1;// 音频
	public static final Byte TYPE_VIDEO_CLIP = 2;// 短视频
	public static final Byte TYPE_VIDEO = 3;// 视频
	public static final Byte TYPE_LIVE = 4;// 直播
	public static final Byte TYPE_H5 = 5;// H5文本
	public static final Byte TYPE_POST = 6;// 帖子
	public static final Byte TYPE_SET = 7;// 内容集合

	public static final Byte STATUS_DRAFT = 0; // 草稿
	public static final Byte STATUS_NORMAL = 1; // 正常
	public static final Byte STATUS_CLOSED = 2; // 已关闭
	public static final Byte STATUS_DELETED = 3; // 已删除

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
	 * 评分（0~100）
	 */
	@RDSAnnField(column = RDSAnnField.BYTE)
	public Byte score;

	/**
	 * 来源，用来鉴别来源的地址，存储时需要简化，以免查询较慢</br>
	 * 一般使用来源标记 + 来源唯一标识，如：</br>
	 * youku.youkuId
	 */
	@RDSAnnField(column = "VARCHAR(128)")
	public String origin;

	/**
	 * 内容元信息，例如电影是movie，电视剧是drama</br>
	 * 英文字母，最长16位
	 * 
	 */
	@RDSAnnField(column = "VARCHAR(16)")
	public String meta;

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
