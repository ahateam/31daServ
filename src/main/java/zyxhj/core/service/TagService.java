package zyxhj.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.Tag;
import zyxhj.core.domain.TagGroup;
import zyxhj.core.repository.TagGroupRepository;
import zyxhj.core.repository.TagRepository;
import zyxhj.utils.IDUtils;

public class TagService {

	private static Logger log = LoggerFactory.getLogger(TagService.class);

	private static TagService ins;

	public static synchronized TagService getInstance() {
		if (null == ins) {
			ins = new TagService();
		}
		return ins;
	}

	private TagRepository tagRepository;
	private TagGroupRepository groupRepository;

	private TagService() {
		try {
			tagRepository = TagRepository.getInstance();
			groupRepository = TagGroupRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private TagGroup createTagGroup(DruidPooledConnection conn, String keyword, Byte type, String remark)
			throws Exception {
		TagGroup tg = new TagGroup();
		tg.keyword = keyword;
		tg.type = type;
		tg.remark = remark;

		groupRepository.insert(conn, tg);

		return tg;
	}

	/**
	 * 创建系统标签分组
	 */
	public TagGroup createSysTagGroup(DruidPooledConnection conn, String keyword, String remark) throws Exception {
		return createTagGroup(conn, keyword, TagGroup.TYPE_SYS, remark);
	}

	/**
	 * 创建自定义标签分组
	 */
	public TagGroup createCustomTagGroup(DruidPooledConnection conn, String keyword, String remark) throws Exception {
		return createTagGroup(conn, keyword, TagGroup.TYPE_CUSTOM, remark);
	}

	/**
	 * 更新自定义标签分组
	 */
	public int updateCumtomTagGroup(DruidPooledConnection conn, String keyword, String remark) throws Exception {
		return groupRepository.updateCumtomTagGroup(conn, keyword, remark);
	}

	/**
	 * 获取标签分组列表
	 */
	public List<TagGroup> getTagGroups(DruidPooledConnection conn, Byte type) throws Exception {
		return groupRepository.getListByKey(conn, "type", type, 512, 0);
	}

	/**
	 * 创建标签
	 */
	public Tag createTag(DruidPooledConnection conn, String groupKeyword, String name) throws Exception {
		Tag ct = new Tag();

		ct.id = IDUtils.getSimpleId();
		ct.status = Tag.STATUS_ENABLED;
		ct.groupKeyword = groupKeyword;
		ct.name = name;

		tagRepository.insert(conn, ct);

		return ct;
	}

	/**
	 * 根据groupKeyword和status获取标签列表标签
	 */
	public JSONObject getTags(DruidPooledConnection conn, String groupKeyword, Byte status) throws Exception {
		List<Tag> tags = tagRepository.getListByKeys(conn, new String[] { "group_keyword", "status" },
				new Object[] { groupKeyword, status }, 512, 0);
		JSONObject ret = new JSONObject();
		for (Tag t : tags) {
			JSONArray ja = ret.getJSONArray(t.groupKeyword);
			if (null == ja) {
				ja = new JSONArray();
				ja.add(t.name);
				ret.put(t.groupKeyword, ja);
			} else {
				ja.add(t.name);
			}
		}
		return ret;
	}

	/**
	 * 启用标签
	 */
	public int updateTag(DruidPooledConnection conn, Long tagId) throws Exception {
		Tag renew = new Tag();
		renew.status = Tag.STATUS_ENABLED;

		return tagRepository.updateByKey(conn, "id", tagId, renew, true);
	}

	/**
	 * 禁用标签
	 */
	public int disableTag(DruidPooledConnection conn, Long tagId) throws Exception {
		Tag renew = new Tag();
		renew.status = Tag.STATUS_DISABLED;

		return tagRepository.updateByKey(conn, "id", tagId, renew, true);
	}
}
