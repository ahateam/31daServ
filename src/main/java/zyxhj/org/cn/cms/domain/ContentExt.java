package zyxhj.org.cn.cms.domain;

import zyxhj.org.cn.utils.data.rds.RDSAnnEntity;
import zyxhj.org.cn.utils.data.rds.RDSAnnField;
import zyxhj.org.cn.utils.data.rds.RDSAnnID;

/**
 * 内容扩展数据</br>
 * 
 * 收费或隐秘内容的数据以及其它扩展内容都放这里</br>
 * 用ots比较合适
 */
@RDSAnnEntity(alias = "tb_content_ext")
public class ContentExt {

	@RDSAnnID
	@RDSAnnField(column = RDSAnnField.ID)
	public Long contentId;

	@RDSAnnField(column = RDSAnnField.JSON)
	public String data;

}
