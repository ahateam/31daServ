package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONObject;

import zyxhj.core.domain.Tag;
import zyxhj.core.domain.User;
import zyxhj.core.service.TagService;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.api.APIRequest;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.Param;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class TagController extends Controller {

	private static Logger log = LoggerFactory.getLogger(TagController.class);

	private static TagController ins;

	public static synchronized TagController getInstance(String node) {
		if (null == ins) {
			ins = new TagController(node);
		}
		return ins;
	}

	private DataSource dsRds;
	private TagService tagService;

	private TagController(String node) {
		super(node);

		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			tagService = TagService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建标签
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param groupKeyword
	 *            标签分组关键字
	 * @param name
	 *            标签名称
	 * 
	 * @return 标签列表
	 */
	@POSTAPI(path = "createTag")
	public APIResponse createTag(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long userId = Param.getLong(c, "userId");

		String groupKeyword = Param.getString(c, "groupKeyword");
		String name = Param.getString(c, "tagName");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.createTag(conn, groupKeyword, name));
		}
	}

	/**
	 * 获取标签列表
	 * 
	 * @param userId
	 *            用户编号，用于鉴权
	 * @param groupKeyword
	 *            标签分组关键字
	 * 
	 * @return 标签列表
	 */
	@POSTAPI(path = "getTags")
	public APIResponse getTags(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long userId = Param.getLong(c, "userId");

		String groupKeyword = Param.getString(c, "groupKeyword");
		Byte status = Param.getByteDFLT(c, "status", Tag.STATUS_ENABLED);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.getTags(conn, groupKeyword, status));
		}
	}
}
