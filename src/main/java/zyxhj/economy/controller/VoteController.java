package zyxhj.economy.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSONArray;

import zyxhj.economy.domain.Vote;
import zyxhj.economy.service.VoteService;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class VoteController extends Controller {

	private static Logger log = LoggerFactory.getLogger(VoteController.class);

	private static VoteController ins;

	public static synchronized VoteController getInstance(String node) {
		if (null == ins) {
			ins = new VoteController(node);
		}
		return ins;
	}

	private DataSource dsRds;
	private VoteService voteService;

	private VoteController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			voteService = VoteService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@ENUM(des = "投票类型")
	public Vote.TYPE[] voteTypes = Vote.TYPE.values();

	@ENUM(des = "投票参与人群")
	public Vote.CROWD[] voteCrowds = Vote.CROWD.values();

	@ENUM(des = "投票模版类型")
	public Vote.TEMPLATE[] voteTemplates = Vote.TEMPLATE.values();

	@ENUM(des = "投票状态")
	public Vote.STATUS[] voteStatus = Vote.STATUS.values();

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createVoteProject", //
			des = "创建投票项目", //
			ret = "所创建的对象"//
	)
	public APIResponse createVoteProject(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "标题") String title, //
			@P(t = "备注") String remark, //
			@P(t = "起始时间") Date startTime, //
			@P(t = "终止时间") Date expiryTime//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(
					voteService.createVoteProject(conn, orgId, userId, title, remark, startTime, expiryTime));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editVoteProject", //
			des = "编辑投票项目", //
			ret = "更新影响的记录数"//
	)
	public APIResponse editVoteProject(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "标题") String title, //
			@P(t = "备注") String remark, //
			@P(t = "起始时间") Date startTime, //
			@P(t = "终止时间") Date expiryTime//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(
					voteService.editVoteProject(conn, projectId, title, remark, startTime, expiryTime));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delVoteProject", //
			des = "删除投票项目", //
			ret = "更新影响的记录数"//
	)
	public APIResponse delVoteProject(//
			@P(t = "投票项目编号") Long projectId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.delVoteProject(conn, projectId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getVoteProjectsByOrgId", //
			des = "获取组织的投票项目", //
			ret = "所查询的对象列表"//
	)
	public APIResponse getVoteProjectsByOrgId(//
			@P(t = "投票项目编号") Long orgId, //
			@P(t = "项目是否可用") Boolean isActive, //
			Integer count, //
			Integer offset //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse
					.getNewSuccessResp(voteService.getVoteProjectsByOrgId(conn, orgId, isActive, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "addVote", //
			des = "创建投票", //
			ret = "所创建的对象"//
	)
	public APIResponse addVote(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "项目编号") Long projectId, //
			@P(t = "投票模版，Vote.TEMPLATE") Byte template, //
			@P(t = "投票类型，Vote.TYPE") Byte type, //
			@P(t = "最多选择的数量") Byte choiceCount, //
			@P(t = "参加投票项目的人群（JSONArray）,Vote.CROWD") JSONArray crowd, //
			@P(t = "用户在有效期内是否可以重新编辑选票") Boolean reeditable, //
			@P(t = "是否实名制") Boolean realName, //
			@P(t = "是否内部投票（外部可允许任何人参与，用于意见采集）") Boolean isInternal, //
			@P(t = "是否带有弃权选项") Boolean isAbstain, //
			@P(t = "生效人数比例（百分率，50代表50%）") Byte effectiveRatio, //
			@P(t = "失效人数比例（期权人数过多就失效，百分率，50代表50%）") Byte failureRatio, //
			@P(t = "标题") String title, //
			@P(t = "备注") String remark, //
			@P(t = "扩展（JSON）") String ext //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(
					voteService.addVote(conn, orgId, projectId, template, type, choiceCount, crowd, reeditable,
							realName, isInternal, isAbstain, effectiveRatio, failureRatio, title, remark, ext));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editVote", //
			des = "编辑投票", //
			ret = "所创建的对象"//
	)
	public APIResponse editVote(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "投票模版，Vote.TEMPLATE") Byte template, //
			@P(t = "投票类型，Vote.TYPE") Byte type, //
			@P(t = "最多选择的数量") Byte choiceCount, //
			@P(t = "参加投票项目的人群（JSONArray）,Vote.CROWD") JSONArray crowd, //
			@P(t = "用户在有效期内是否可以重新编辑选票") Boolean reeditable, //
			@P(t = "是否实名制") Boolean realName, //
			@P(t = "是否内部投票（外部可允许任何人参与，用于意见采集）") Boolean isInternal, //
			@P(t = "是否带有弃权选项") Boolean isAbstain, //
			@P(t = "生效人数比例（百分率，50代表50%）") Byte effectiveRatio, //
			@P(t = "失效人数比例（期权人数过多就失效，百分率，50代表50%）") Byte failureRatio, //
			@P(t = "标题") String title, //
			@P(t = "备注") String remark, //
			@P(t = "扩展（JSON）") String ext //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(
					voteService.editVote(conn, orgId, projectId, voteId, template, type, choiceCount, crowd, reeditable,
							realName, isInternal, isAbstain, effectiveRatio, failureRatio, title, remark, ext));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "setVoteActivation", //
			des = "启用/禁用投票", //
			ret = "更新影响的记录数"//
	)
	public APIResponse setVoteActivation(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "启用/禁用") Boolean activation //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.setVoteActivation(conn, projectId, voteId, activation));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "setVotePaused", //
			des = "人为作废/恢复投票", //
			ret = "更新影响的记录数"//
	)
	public APIResponse setVotePaused(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "人为废除/恢复") Boolean paused //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.setVotePaused(conn, projectId, voteId, paused));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "setVoteOptionIds", //
			des = "设置投票选项编号列表（包含顺序）", //
			ret = "更新影响的记录数"//
	)
	public APIResponse setVoteOptionIds(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "投票选项编号列表（JSONArray）") JSONArray optionIds //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.setVoteOptionIds(conn, projectId, voteId, optionIds));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delVote", //
			des = "删除投票项目", //
			ret = "更新影响的记录数"//
	)
	public APIResponse delVote(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.delVote(conn, projectId, voteId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getVotes", //
			des = "获取投票项目中的投票", //
			ret = "所查询的对象列表"//
	)
	public APIResponse getVotes(//
			@P(t = "投票项目编号") Long projectId //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVotes(conn, projectId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "addVoteOption", //
			des = "添加投票选项", //
			ret = "添加的选项"//
	)
	public APIResponse addVoteOption(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "标题") String title, //
			@P(t = "备注") String remark, //
			@P(t = "扩展（JSON）") String ext//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse
					.getNewSuccessResp(voteService.addVoteOption(conn, projectId, voteId, false, title, remark, ext));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editVoteOption", //
			des = "修改投票选项", //
			ret = "影响记录的行数"//
	)
	public APIResponse editVoteOption(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票选项编号") Long optionId, //
			@P(t = "标题") String title, //
			@P(t = "备注") String remark, //
			@P(t = "扩展（JSON）") String ext//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			int ret = voteService.editVoteOption(conn, projectId, optionId, title, remark, ext);
			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delVoteOption", //
			des = "删除投票选项", //
			ret = "影响记录的行数")
	public APIResponse delVoteOption(//
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "投票选项编号") Long optionId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.delVoteOption(conn, projectId, voteId, optionId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getVoteOptions", //
			des = "获取投票的选项列表", //
			ret = "选项对象列表"//
	)
	public APIResponse getVoteOptions(//
			@P(t = "投票编号") Long voteId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVoteOptions(conn, voteId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "vote", //
			des = "投票"//
	)
	public APIResponse vote(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "投票项目编号") Long projectId, //
			@P(t = "投票编号") Long voteId, //
			@P(t = "用户编号") Long userId, //
			@P(t = "选项JSON数组（potionId列表）\n" + //
					"[\"134441\",\"234234\"]\n") JSONArray selections, //
			@P(t = "用户的选票数") Integer ballotCount, //
			@P(t = "备注") String remark//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			voteService.vote(conn, orgId, projectId, voteId, userId, selections, ballotCount, remark);
			return APIResponse.getNewSuccessResp();
		}
	}

	@POSTAPI(//
			path = "getVoteDetail", //
			des = "获取投票详细", //
			ret = "投票详细，vote及opt信息"//
	)
	public APIResponse getVoteDetail(//
			@P(t = "投票编号") Long voteId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVoteDetail(conn, voteId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getVoteTicket", //
			des = "获取用户的选票", //
			ret = "用户选票对象"//
	)
	public APIResponse getVoteTicket(//
			@P(t = "投票编号") Long voteId, //
			@P(t = "用户编号") Long userId//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVoteTicket(conn, voteId, userId));
		}
	}
}
