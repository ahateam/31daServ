package zyxhj.cms.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.cms.domain.Content;
import zyxhj.cms.service.ContentService;
import zyxhj.core.domain.User;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

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

	@ENUM(des = "内容类型")
	public Content.TYPE[] contentTypes = Content.TYPE.values();

	@ENUM(des = "内容状态")
	public Content.STATUS[] contentStatus = Content.STATUS.values();

	/**
	 * 
	 */
	@POSTAPI(path = "createContent", //
			des = "创建内容", //
			ret = "所创建的对象"//
	)
	public APIResponse createContent(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE") Byte type, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题") String title, //
			@P(t = "数据（JSON）") String data//
	) throws Exception {
		Long upUserId = userId;
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content cnt = contentService.createContent(conn, type, Content.LEVEL_PUBLIC, upUserId, upChannelId, title,
					data);
			return APIResponse.getNewSuccessResp(cnt);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "addContentTags", //
			des = "为内容添加标签" //
	)
	public APIResponse addContentTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签大类") String tagKind, //
			@P(t = "标签小类") String tagType, //
			@P(t = "标签列表（JSON数组）") JSONArray tags//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			contentService.addContentTags(conn, contentId, tagKind, tagType, tags);

			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "removeContentTags", //
			des = "为内容移除标签" //
	)
	public APIResponse removeContentTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签大类") String tagKind, //
			@P(t = "标签小类") String tagType, //
			@P(t = "标签列表（JSON数组）") JSONArray tags//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content content = contentService.auth(conn, contentId);// content鉴权

			contentService.removeContentTags(conn, contentId, tagKind, tagType, tags);

			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 
	 */
	@POSTAPI(path = "queryContents", //
			des = "查询标签", //
			ret = "内容对象数组"//
	)
	public APIResponse queryContents(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE", r = false) Byte contentType, //
			@P(t = "内容状态Content.STATUS", r = false) Byte status, //
			@P(t = "上传用户编号", r = false) Long upUserId, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "标题", r = false) String title, //
			@P(t = "标签大类", r = false) String tagKind, //
			@P(t = "标签小类", r = false) String tagType, //
			@P(t = "标签列表（JSONArray）", r = false) JSONArray tags, //
			@P(t = "数量") Integer count, //
			@P(t = "偏移量") Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			List<Content> ret = contentService.queryContents(conn, contentType, status, Content.LEVEL_PUBLIC, upUserId,
					upChannelId, tagKind, tagType, tags, count, offset);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param contentType
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
	 * @param keyword
	 *            标题关键字
	 * @param count
	 *            （选填，默认10）</br>
	 *            分页读取的记录数量
	 * @param offset
	 *            （选填，默认0）</br>
	 *            分页读取的起点位置
	 * 
	 * @return 内容对象数组
	 */
	@POSTAPI(//
			path = "searchContents", //
			des = "搜索标签", //
			ret = "内容对象数组"//
	)
	public APIResponse searchContents(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容类型Content.TYPE", r = false) Byte contentType, //
			@P(t = "内容状态Content.STATUS", r = false) Byte status, //
			@P(t = "上传用户编号", r = false) Long upUserId, //
			@P(t = "上传专栏编号", r = false) Long upChannelId, //
			@P(t = "搜索关键字") String keyword, //
			@P(t = "数量") Integer count, //
			@P(t = "偏移量") Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			List<Content> ret = contentService.searchContents(conn, contentType, status, Content.LEVEL_PUBLIC, upUserId,
					upChannelId, keyword, count, offset);

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
	@POSTAPI(//
			path = "getContentById", //
			des = "根据编号获取内容", //
			ret = "编号对应的内容"//
	)
	public APIResponse getContentById(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			Content ret = contentService.getContentById(conn, contentId);

			return APIResponse.getNewSuccessResp(ServiceUtils.checkNull(ret));
		}
	}

	/**
	 * 根据内容编号和标签分组名称，获取该内容的标签列表
	 * 
	 * @param userId
	 *            用户编号
	 * @param contentId
	 *            内容编号
	 * @param tagKind
	 *            标签大类
	 * @param tagType
	 *            标签小类
	 * @return 内容对应标签分组的标签数组，JSONArray格式
	 */
	@POSTAPI(//
			path = "getContentTags", //
			des = "获取内容上的标签", //
			ret = "标签名称数组（JSONArray）"//
	)
	public APIResponse getContentTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "内容编号") Long contentId, //
			@P(t = "标签大类") String tagKind, //
			@P(t = "标签小类") String tagType//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			JSONArray ret = contentService.getContentTags(conn, contentId, tagKind, tagType);

			return APIResponse.getNewSuccessResp(ret);
		}
	}

}
