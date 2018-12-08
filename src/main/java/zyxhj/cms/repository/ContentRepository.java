package zyxhj.cms.repository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class ContentRepository extends RDSRepository<Content> {

	private static ContentRepository ins;

	public static synchronized ContentRepository getInstance() {
		if (null == ins) {
			ins = new ContentRepository();
		}
		return ins;
	}

	private ContentRepository() {
		super(Content.class);
	}

	/**
	 * RDS的FULLTEXT全文索引尝试失败，查不出东西</br>
	 * 将来准备用opensearch等工具替代，目前使用select like，只查询title字段
	 */
	public List<Content> searchContents(DruidPooledConnection conn, Byte type, Byte status, Byte level, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws ServerException {

		ArrayList<Object> objs = new ArrayList<>();

		StringBuffer sb = new StringBuffer();
		sb.append("WHERE ");
		if (null != type) {
			sb.append("AND type=? ");
			objs.add(type);
		}
		if (null != status) {
			sb.append("AND status=? ");
			objs.add(status);
		}
		if (null != level) {
			sb.append("AND level=? ");
			objs.add(level);
		}
		if (null != upUserId) {
			sb.append("AND up_user_id=? ");
			objs.add(upUserId);
		}
		if (null != upChannelId) {
			sb.append("AND up_channel_id=? ");
			objs.add(upChannelId);
		}
		sb.append("AND title LIKE '%").append(keywords).append("%'");

		return this.getList(conn, sb.toString(), objs.toArray(), count, offset);
	}

	public List<Content> queryContents(DruidPooledConnection conn, Byte type, Byte status, Byte level, Long upUserId,
			Long upChannelId, String tagKind, String tagType, JSONArray tags, Integer count, Integer offset)
			throws ServerException {
		// SELECT * FROM `tb_content` WHERE JSON_CONTAINS(tags->'$.k1', '"t1"');

		ArrayList<Object> objs = new ArrayList<>();

		StringBuffer sb = new StringBuffer();
		sb.append("WHERE app_id=? ");
		if (null != type) {
			sb.append("AND type=? ");
			objs.add(type);
		}
		if (null != status) {
			sb.append("AND status=? ");
			objs.add(status);
		}
		if (null != level) {
			sb.append("AND level=? ");
			objs.add(level);
		}
		if (null != upUserId) {
			sb.append("AND up_user_id=? ");
			objs.add(upUserId);
		}
		if (null != upChannelId) {
			sb.append("AND up_channel_id=? ");
			objs.add(upChannelId);
		}

		String tagKey = StringUtils.join(tagKind, '_', tagType);

		sb.append("AND JSON_CONTAINS(tags->'$.").append(tagKey).append("', '");
		for (Object tag : tags) {
			sb.append("\"").append(tag).append("\",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("')");

		return this.getList(conn, sb.toString(), objs.toArray(), count, offset);
	}

	public JSONArray getContentTags(DruidPooledConnection conn, Long contentId, String tagKind, String tagType)
			throws ServerException {
		return this.getTags(conn, "tags", contentId, StringUtils.join(tagKind, '_', tagType));
	}

	/**
	 * 由于MYSQL的JSON支持，数组操作的api有些简陋，因此仍然选择读取出来再整体覆盖的办法</br>
	 * 但是，查询和检索仍然能够得到不小的提高
	 */
	public void addContentTags(DruidPooledConnection conn, Long contentId, String tagKind, String tagType,
			JSONArray tags) throws ServerException {
		// UPDATE `tb_content` SET tags=JSON_SET(tags, '$.k2',JSON_ARRAY("t4","t5"))
		// WHERE id=386953055161814;
		this.addTags(conn, "tags", contentId, StringUtils.join(tagKind, '_', tagType), tags);
	}

	/**
	 * 由于MYSQL的JSON支持，数组操作的api有些简陋，因此仍然选择读取出来再整体覆盖的办法</br>
	 * 但是，查询和检索仍然能够得到不小的提高
	 */
	public void removeContentTags(DruidPooledConnection conn, Long contentId, String tagKind, String tagType,
			JSONArray tags) throws ServerException {
		// SQL语句跟add相同，都是设置
		// UPDATE `tb_content` SET tags=JSON_SET(tags, '$.k2',JSON_ARRAY("t4","t5"))
		// WHERE id=386953055161814;

		this.removeTags(conn, "tags", contentId, StringUtils.join(tagKind, '_', tagType), tags);
	}

}
