package zyxhj.cms.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.cms.repository.ContentExtRepository;
import zyxhj.cms.repository.ContentRepository;
import zyxhj.utils.IDUtils;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class ContentService {

	private static Logger log = LoggerFactory.getLogger(ContentService.class);

	private static ContentService ins;

	public static synchronized ContentService getInstance() {
		if (null == ins) {
			ins = new ContentService();
		}
		return ins;
	}

	private ContentRepository contentRepository;
	private ContentExtRepository contentExtRepository;

	private ContentService() {
		try {
			contentRepository = ContentRepository.getInstance();
			contentExtRepository = ContentExtRepository.getInstance();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 验证内容是否合法：判断是否下线，等等。TODO 目前待完善
	 */
	public Content auth(DruidPooledConnection conn, Long contentId) throws Exception {
		// 先判断user是否存在
		Content content = contentRepository.getByKey(conn, "id", contentId);
		if (null == content) {
			// content不存在
			throw new ServerException(BaseRC.CMS_CONTENT_NOT_EXISET);
		} else {
			// 再判断content状态是否有效，TODO 目前status没有启用
			return content;
		}
	}

	/**
	 * 创建内容
	 */
	public Content createContent(DruidPooledConnection conn, Byte type, Byte level, Long upUserId, Long upChannelId,
			String title, String data) throws Exception {

		Content c = new Content();

		c.id = IDUtils.getSimpleId();

		c.type = type;
		c.status = Content.STATUS_DRAFT;// 首次创建时是草稿状态
		c.level = level;

		c.createTime = new Date();
		c.updateTime = c.createTime;
		c.upUserId = upUserId;
		c.upChannelId = upChannelId;

		c.title = title;
		c.data = data;
		c.tags = "{}";// 设置为JSON数组的空格式，否则后续的编辑操作会没效果（可能是MYSQL的bug）

		contentRepository.insert(conn, c);

		return c;
	}

	/**
	 * 根据关键字搜索内容
	 */
	public List<Content> searchContents(DruidPooledConnection conn, Byte type, Byte status, Byte level, Long upUserId,
			Long upChannelId, String keywords, Integer count, Integer offset) throws Exception {
		return contentRepository.searchContents(conn, type, status, level, upUserId, upChannelId, keywords, count,
				offset);
	}

	/**
	 * 根据标签查询内容
	 */
	public List<Content> queryContents(DruidPooledConnection conn, Byte type, Byte status, Byte level, Long upUserId,
			Long upChannelId, String tagKind, String tagType, JSONArray tags, Integer count, Integer offset)
			throws Exception {
		return contentRepository.queryContents(conn, type, status, level, upUserId, upChannelId, tagKind, tagType, tags,
				count, offset);
	}

	/**
	 * 根据内容编号查询内容
	 */
	public Content getContentById(DruidPooledConnection conn, Long contentId) throws Exception {
		return contentRepository.getByKey(conn, "id", contentId);
	}

	/**
	 * 根据编号删除内容
	 */
	public void deleteContent(DruidPooledConnection conn, Long contentId) throws Exception {
		contentRepository.deleteByKey(conn, "id", contentId);
	}

	/**
	 * 读取内容对应的标签
	 * 
	 */
	public JSONArray getContentTags(DruidPooledConnection conn, Long contentId, String tagKind, String tagType)
			throws Exception {
		return contentRepository.getContentTags(conn, contentId, tagKind, tagType);
	}

	/**
	 * 为内容添加标签
	 */
	public void addContentTags(DruidPooledConnection conn, Long contentId, String tagKind, String tagType,
			JSONArray tags) throws Exception {
		contentRepository.addContentTags(conn, contentId, tagKind, tagType, tags);
	}

	/**
	 * 移除内容的标签
	 */
	public void removeContentTags(DruidPooledConnection conn, Long contentId, String tagKind, String tagType,
			JSONArray tags) throws Exception {
		contentRepository.removeContentTags(conn, contentId, tagKind, tagType, tags);
	}

}
