package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.Tag;
import zyxhj.core.domain.User;
import zyxhj.core.service.TagService;
import zyxhj.utils.ServiceUtils;
import zyxhj.utils.Singleton;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class TagController extends Controller {

	private static Logger log = LoggerFactory.getLogger(TagController.class);

	private DataSource dsRds;
	private TagService tagService;

	public TagController(String node) {
		super(node);

		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			tagService = Singleton.ins(TagService.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@ENUM(des = "标签状态")
	public Tag.STATUS[] tagStatus = Tag.STATUS.values();

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createSysTagGroup", //
			des = "创建系统标签分组", //
			ret = "所创建的对象"//
	)
	public APIResponse createSysTagGroup(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String keyword, //
			@P(t = "备注") String remark//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.createSysTagGroup(conn, keyword, remark));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createCustomTagGroup", //
			des = "创建自定义标签分组", //
			ret = "所创建的对象"//
	)
	public APIResponse createCustomTagGroup(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String keyword, //
			@P(t = "备注") String remark//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.createCustomTagGroup(conn, keyword, remark));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "updateCumtomTagGroup", //
			des = "更新自定义标签分组", //
			ret = "影响的记录行数"//
	)
	public APIResponse updateCumtomTagGroup(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String keyword, //
			@P(t = "备注") String remark//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.updateCumtomTagGroup(conn, keyword, remark));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getSysTagGroups", //
			des = "获取系统标签分组列表", //
			ret = "标签分组对象列表"//
	)
	public APIResponse getSysTagGroups(//
			@P(t = "用户编号") Long userId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.getSysTagGroups(conn));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getCumtomTagGroups", //
			des = "获取自定义标签分组列表", //
			ret = "标签分组对象列表"//
	)
	public APIResponse getCumtomTagGroups(//
			@P(t = "用户编号") Long userId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.getCumtomTagGroups(conn));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createTag", //
			des = "创建标签", //
			ret = "所创建的对象"//
	)
	public APIResponse createTag(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签名称") String name//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.createTag(conn, groupKeyword, name));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getTags", //
			des = "获取标签列表", //
			ret = "标签列表"//
	)
	public APIResponse getTags(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			Byte status//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.getTags(conn, groupKeyword, status));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "enableTag", //
			des = "启用标签", //
			ret = "影响的记录行数"//
	)
	public APIResponse enableTag(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签名称") String name//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.enableTag(conn, groupKeyword, name));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "disableTag", //
			des = "禁用标签", //
			ret = "影响的记录行数"//
	)
	public APIResponse disableTag(//
			@P(t = "用户编号") Long userId, //
			@P(t = "标签分组关键字") String groupKeyword, //
			@P(t = "标签名称") String name//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			User user = ServiceUtils.userAuth(conn, userId);// user鉴权

			return APIResponse.getNewSuccessResp(tagService.disableTag(conn, groupKeyword, name));
		}
	}
}
