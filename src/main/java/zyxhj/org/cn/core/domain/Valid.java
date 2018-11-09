package zyxhj.org.cn.core.domain;

import zyxhj.org.cn.utils.data.ots.OTSAnnEntity;
import zyxhj.org.cn.utils.data.ots.OTSAnnField;
import zyxhj.org.cn.utils.data.ots.OTSAnnID;

@OTSAnnEntity(alias = "valid")
public class Valid {

	@OTSAnnID(keyType = OTSAnnID.KeyType.PARTITION_KEY)
	@OTSAnnField(column = OTSAnnField.ColumnType.INTEGER)
	public Long id;

	/**
	 * 有效时间，单位，分钟
	 */
	@OTSAnnField(column = OTSAnnField.ColumnType.INTEGER)
	public Integer expire;

	/**
	 * 验证码，一般是4位或6位随机数
	 */
	@OTSAnnField(column = OTSAnnField.ColumnType.STRING)
	public String code;

}
