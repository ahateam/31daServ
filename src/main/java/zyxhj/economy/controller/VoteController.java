package zyxhj.economy.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.economy.domain.Vote;
import zyxhj.economy.service.VoteService;
import zyxhj.utils.api.APIRequest;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.api.Param;
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

	/**
	 * 创建投票
	 * 
	 * @param orgId
	 *            组织编号
	 * @param title
	 *            标题
	 * @param remark
	 *            备注
	 * @param type
	 *            投票类型，单选多选
	 * @param choiceCount
	 *            多选数量限制
	 * @param startTime
	 *            开始日期
	 * @param expiryTime
	 *            截止日期
	 * @return 创建的投票对象
	 * 
	 */
	@POSTAPI(path = "createVote")
	public APIResponse createVote(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");
		String title = Param.getString(c, "title");
		String remark = Param.getString(c, "remark");

		Byte type = Param.getByte(c, "type");
		Byte choiceCount = Param.getByte(c, "choiceCount");
		Date startTime = new Date(Param.getLong(c, "startTime"));
		Date expiryTime = new Date(Param.getLong(c, "expiryTime"));

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(
					voteService.createVote(conn, orgId, title, remark, type, choiceCount, startTime, expiryTime));
		}
	}

	/**
	 * 获取组织的投票列表
	 * 
	 * @param orgId
	 *            组织编号
	 * @param count
	 *            数量（用于分页）
	 * @param offset
	 *            起始位置（从零开始，用于分页）
	 * 
	 * @return 投票列表（）
	 * 
	 */
	@POSTAPI(path = "getVotes")
	public APIResponse getVotes(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long orgId = Param.getLong(c, "orgId");

		Integer count = Param.getInteger(c, "count");
		Integer offset = Param.getInteger(c, "offset");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			List<Vote> vs = voteService.getVotes(conn, orgId, count, offset);
			JSONArray waiting = new JSONArray();
			JSONArray started = new JSONArray();
			JSONArray fininshed = new JSONArray();

			Date now = new Date();
			for (Vote v : vs) {
				if (now.compareTo(v.startTime) > 0) {
					// 已经开始
					if (now.compareTo(v.expiryTime) > 0) {
						// 已经结束
						fininshed.add(v);
//						System.out.println("fininshed->" + JSON.toJSONString(v));
					} else {
						started.add(v);
					}
				} else {
					waiting.add(v);
				}
			}

			JSONObject ret = new JSONObject();
			ret.put("waiting", waiting);
			ret.put("started", started);
			ret.put("fininshed", fininshed);
			return APIResponse.getNewSuccessResp(ret);
		}
	}

	/**
	 * 添加投票的选项
	 * 
	 * @param voteId
	 *            投票编号
	 * @param title
	 *            标题
	 * @param remark
	 *            备注（非必填）
	 * @return 添加的选项
	 * 
	 */
	@POSTAPI(path = "addVoteOption")
	public APIResponse addVoteOption(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long voteId = Param.getLong(c, "voteId");
		String title = Param.getString(c, "title");
		String remark = Param.getStringDFLT(c, "remark", null);

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.addVoteOption(conn, voteId, title, remark));
		}
	}

	/**
	 * 获取组织的投票的选项列表
	 * 
	 * @param voteId
	 *            投票编号
	 * 
	 * @return 投票选项列表
	 * 
	 */
	@POSTAPI(path = "getVoteOptions")
	public APIResponse getVoteOptions(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long voteId = Param.getLong(c, "voteId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVoteOptions(conn, voteId));
		}
	}

	/**
	 * 设置投票的选项
	 * 
	 * @param optionId
	 *            投票选项编号
	 * @param title
	 *            标题
	 * @param remark
	 *            备注
	 * @return 添加的选项
	 * 
	 */
	@POSTAPI(path = "setVoteOption")
	public APIResponse setVoteOption(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long optionId = Param.getLong(c, "optionId");
		String title = Param.getString(c, "title");
		String remark = Param.getString(c, "remark");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			voteService.setVoteOption(conn, optionId, title, remark);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 删除投票的选项 TODO 将来投票启动之后，不允许随便删除
	 * 
	 * @param optionId
	 *            投票选项编号
	 * 
	 */
	@POSTAPI(path = "delVoteOption")
	public APIResponse delVoteOption(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long optionId = Param.getLong(c, "optionId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			voteService.delVoteOption(conn, optionId);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 投票
	 * 
	 * @param voteId
	 *            投票编号
	 * @param userId
	 *            用户编号
	 * @param selections
	 *            选项JSON数组</br>
	 *            [{"option":"134441","opt","2"},{"option":"234234","opt","1"}]</br>
	 *            option是指VoteOption编号，opt是指具体选项：</br>
	 *            OPT_ABSTAINED = 0;</br>
	 *            OPT_AGREE = 1;</br>
	 *            OPT_DISAGREE = 2;</br>
	 * @param weight
	 *            用户的投票权重
	 * @param remark
	 *            备注
	 * 
	 */
	@POSTAPI(path = "vote")
	public APIResponse vote(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long voteId = Param.getLong(c, "voteId");
		Long userId = Param.getLong(c, "userId");

		JSONArray selections = JSON.parseArray(Param.getString(c, "selections"));

		Integer weight = Param.getInteger(c, "weight");
		String remark = Param.getString(c, "remark");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			voteService.vote(conn, voteId, userId, selections, weight, remark);
			return APIResponse.getNewSuccessResp();
		}
	}

	/**
	 * 删除投票的选项 TODO 将来投票启动之后，不允许随便删除
	 * 
	 * @param voteId
	 *            投票编号
	 * 
	 */
	@POSTAPI(path = "getVoteDetail")
	public APIResponse getVoteDetail(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long voteId = Param.getLong(c, "voteId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVoteDetail(conn, voteId));
		}
	}

	/**
	 * 获取用户的选票
	 * 
	 * @param voteId
	 *            投票编号
	 * @param userId
	 *            用户编号
	 * 
	 */
	@POSTAPI(path = "getVoteTicket")
	public APIResponse getVoteTicket(APIRequest req) throws Exception {
		JSONObject c = Param.getReqContent(req);

		Long voteId = Param.getLong(c, "voteId");
		Long userId = Param.getLong(c, "userId");

		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(voteService.getVoteTicket(conn, voteId, userId));
		}
	}
}
