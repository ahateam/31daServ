package zyxhj.org.cn.cms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.org.cn.cms.domain.Content;
import zyxhj.org.cn.cms.service.ContentService;
import zyxhj.org.cn.core.domain.User;
import zyxhj.org.cn.utils.ServiceUtils;
import zyxhj.org.cn.utils.api.APIRequest;
import zyxhj.org.cn.utils.api.APIResponse;
import zyxhj.org.cn.utils.api.Controller;
import zyxhj.org.cn.utils.api.Param;
import zyxhj.org.cn.utils.data.DataSource;
import zyxhj.org.cn.utils.data.DataSourceUtils;

public class ContentController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ContentController.class);

	private static ContentController ins;

	public static synchronized ContentController getInstance(String node) {
		if (null == ins) {
			ins = new ContentController(node);
		}
		return ins;
	}

	private DataSource dsRds;
	private ContentService contentService;

	private ContentController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			contentService = ContentService.getInstance();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建内容
	 * 
	 * @param userId
	 *            用户编号
	 * @param type
	 *            类型</br>
	 *            TYPE_ALBUM = 0;// 相册</br>
	 *            TYPE_AUDIO = 1;// 音频</br>
	 *            TYPE_VIDEO_CLIP = 2;// 短视频</br>
	 *            TYPE_VIDEO = 3;// 视频</br>
	 *            TYPE_LIVE = 4;// 直播</br>
	 *            TYPE_H5 = 5;// H5文本</br>
	 *            TYPE_POST = 6;// 帖子</br>
	 *            TYPE_SET = 7;// 内容集合</br>
	 * @param level
	 *            （选填，默认LEVEL_PUBLIC）</br>
	 *            分级（用于权限控制）</br>
	 *            （未实现）
	 * @param upChannelId
	 *            上传所属专栏编号
	 * @param title
	 *            标题
	 * @param origon
	 *            （选填，默认为appId）</br>
	 *            内容来源
	 * @param meta
	 *            内容元信息，用于进一步标识内容的用途
	 * @param data
	 *            数据</br>
	 *            JSON形式存储内容信息结构体，具体结构体视项目而定
	 */
	@POSTAPI(path = "createContent")
	public APIResponse createContent(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");

		// id
		Byte type = Param.getByte(c, "type");
		// status
		Byte level = Param.getByteDFLT(c, "level", Content.LEVEL_PUBLIC);
		// createTime
		// updateTime
		Long upUserId = userId;
		Long upChannelId = Param.getLong(c, "upChannelId");
		String title = Param.getString(c, "title");
		// score
		String origin = Param.getStringDFLT(c, "origin", null);
		String meta = Param.getString(c, "meta");
		String data = Param.getString(c, "data");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content cnt = contentService.createContent(conn, type, level, upUserId, upChannelId, title, origin, meta,
					data);

			return APIResponse.getNewSuccessResp(cnt.id);
		}
	}

	/**
	 * 为Content添加标签
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param contentId
	 *            内容编号
	 * @param type
	 *            标签分组名称
	 * @param tags
	 *            标签列表（JSON数组格式）
	 */
	@POSTAPI(path = "addContentTags")
	public APIResponse addContentTags(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");

		Long contentId = Param.getLong(c, "contentId");
		String type = Param.getString(c, "type");
		JSONArray tags = Param.getArray(c, "tags");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			contentService.addContentTags(conn, contentId, type, tags);

			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 为Content移除标签
	 * 
	 * @param appId
	 *            应用编号
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param contentId
	 *            内容编号
	 * @param type
	 *            标签分组名称
	 * @param tags
	 *            标签列表（JSON数组格式）
	 * 
	 */
	@POSTAPI(path = "removeContentTags")
	public APIResponse removeContentTags(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long appId = Param.getLong(c, "appId");
		Long userId = Param.getLong(c, "userId");

		Long contentId = Param.getLong(c, "contentId");
		String type = Param.getString(c, "type");
		JSONArray tags = Param.getArray(c, "tags");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			contentService.removeContentTags(conn, contentId, type, tags);

			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param type
	 *            （选填，默认不参与查询）</br>
	 *            内容类型（可空）
	 * @param status
	 *            （选填，默认不参与查询）</br>
	 *            内容状态
	 * @param level
	 *            （选填，默认不参与查询）</br>
	 *            内容等级
	 * @param upUserId
	 *            （选填，默认不参与查询）</br>
	 *            上传者的用户编号
	 * @param upChannelId
	 *            （选填，默认不参与查询）</br>
	 *            上传专栏的专栏编号
	 * @param type
	 *            标签分组名称
	 * @param tags
	 *            标签列表（JSON数组格式）
	 * @param count
	 *            （选填，默认10）</br>
	 *            分页读取的记录数量
	 * @param offset
	 *            （选填，默认0）</br>
	 *            分页读取的起点位置
	 * 
	 * @return 内容对象数组
	 */
	@POSTAPI(path = "queryContents")
	public APIResponse queryContents(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");

		Byte contentType = Param.getByteDFLT(c, "contentType", null);
		Byte status = Param.getByteDFLT(c, "status", null);
		Byte level = Param.getByteDFLT(c, "level", null);
		Long upUserId = Param.getLongDFLT(c, "upUserId", null);
		Long upChannelId = Param.getLongDFLT(c, "upChannelId", null);

		String tagType = Param.getString(c, "tagType");
		JSONArray tags = Param.getArray(c, "tags");

		Integer count = Param.getIntegerDFLT(c, "count", 10);
		Integer offset = Param.getIntegerDFLT(c, "offset", 0);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			List<Content> ret = contentService.queryContents(conn, contentType, status, level, upUserId, upChannelId,
					tagType, tags, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param type
	 *            （选填，默认不参与查询）</br>
	 *            内容类型（可空）
	 * @param status
	 *            （选填，默认不参与查询）</br>
	 *            内容状态
	 * @param level
	 *            （选填，默认不参与查询）</br>
	 *            内容等级
	 * @param upUserId
	 *            （选填，默认不参与查询）</br>
	 *            上传者的用户编号
	 * @param upChannelId
	 *            （选填，默认不参与查询）</br>
	 *            上传专栏的专栏编号
	 * @param tagKey
	 *            标签分组名称
	 * @param tags
	 *            标签列表（JSON数组格式）
	 * @param count
	 *            （选填，默认10）</br>
	 *            分页读取的记录数量
	 * @param offset
	 *            （选填，默认0）</br>
	 *            分页读取的起点位置
	 * 
	 * @return 内容对象数组
	 */
	@POSTAPI(path = "searchContents")
	public APIResponse searchContents(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");

		Byte type = Param.getByteDFLT(c, "type", null);
		Byte status = Param.getByteDFLT(c, "status", null);
		Byte level = Param.getByteDFLT(c, "level", null);
		Long upUserId = Param.getLongDFLT(c, "upUserId", null);
		Long upChannelId = Param.getLongDFLT(c, "upChannelId", null);

		String keywords = Param.getString(c, "keywords");

		Integer count = Param.getIntegerDFLT(c, "count", 10);
		Integer offset = Param.getIntegerDFLT(c, "offset", 0);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			List<Content> ret = contentService.searchContents(conn, type, status, level, upUserId, upChannelId,
					keywords, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param contentId
	 *            内容编号
	 * 
	 * @return 内容对象
	 */
	@POSTAPI(path = "getContentById")
	public APIResponse getContentById(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");
		Long contentId = Param.getLong(c, "contentId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content ret = contentService.getContentById(conn, contentId);

			return APIResponse.getNewSuccessResp(Param.checkNull(ret));
		}
	}

	/**
	 * 根据内容编号和标签分组名称，获取该内容的标签列表
	 * 
	 * @param userId
	 *            用户编号
	 * @param contentId
	 *            内容编号
	 * @param tagKey
	 *            标签分组名称
	 * @return 内容对应标签分组的标签数组，JSONArray格式
	 */
	@POSTAPI(path = "getContentTags")
	public APIResponse getContentTags(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");

		Long contentId = Param.getLong(c, "contentId");
		String tagType = Param.getString(c, "tagType");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			JSONArray ret = contentService.getContentTags(conn, contentId, tagType);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 返回应用对应的全部内容标签
	 * 
	 * @param appId
	 *            应用编号
	 * @param userId
	 *            用户编号，用于鉴权
	 * @return 标签的JSON对象，例如</br>
	 * 
	 */
	@POSTAPI(path = "getAllTagsByType")
	public APIResponse getAllTagsByType(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);
		Long userId = Param.getLong(c, "userId");
		String tagType = Param.getString(c, "tagType");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			JSONObject ret = contentService.getTagsByType(conn, tagType);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

}
