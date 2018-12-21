package zyxhj.economy.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.economy.domain.ORGRole;
import zyxhj.economy.domain.Vote;
import zyxhj.economy.domain.VoteOption;
import zyxhj.economy.domain.VoteProject;
import zyxhj.economy.domain.VoteTicket;
import zyxhj.economy.repository.ORGRoleRepository;
import zyxhj.economy.repository.VoteOptionRepository;
import zyxhj.economy.repository.VoteProjectRepository;
import zyxhj.economy.repository.VoteRepository;
import zyxhj.economy.repository.VoteTicketRepository;
import zyxhj.utils.CacheCenter;
import zyxhj.utils.IDUtils;
import zyxhj.utils.api.BaseRC;
import zyxhj.utils.api.ServerException;

public class VoteService {

	private static Logger log = LoggerFactory.getLogger(VoteService.class);

	private static VoteService ins;

	public static synchronized VoteService getInstance() {
		if (null == ins) {
			ins = new VoteService();
		}
		return ins;
	}

	private VoteProjectRepository projectRepository;
	private VoteRepository voteRepository;
	private VoteOptionRepository optionRepository;
	private VoteTicketRepository ticketRepository;
	private ORGRoleRepository orgRoleRepository;

	private VoteService() {
		try {
			projectRepository = VoteProjectRepository.getInstance();
			voteRepository = VoteRepository.getInstance();
			optionRepository = VoteOptionRepository.getInstance();
			ticketRepository = VoteTicketRepository.getInstance();
			orgRoleRepository = ORGRoleRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建project
	 */
	public VoteProject createVoteProject(DruidPooledConnection conn, Long orgId, Long userId, String title,
			String remark, Date startTime, Date expiryTime) throws Exception {
		VoteProject vp = new VoteProject();

		vp.id = IDUtils.getSimpleId();
		vp.orgId = orgId;
		vp.creatorId = userId;

		vp.isActive = true;

		vp.title = title;
		vp.remark = remark;

		vp.createTime = new Date();
		vp.startTime = startTime;
		vp.expiryTime = expiryTime;

		projectRepository.insert(conn, vp);
		return vp;
	}

	public int editVoteProject(DruidPooledConnection conn, Long projectId, String title, String remark, Date startTime,
			Date expiryTime) throws Exception {

		VoteProject renew = new VoteProject();
		renew.title = title;
		renew.remark = remark;
		renew.startTime = startTime;
		renew.expiryTime = expiryTime;

		return projectRepository.updateByKey(conn, "id", projectId, renew, true);
	}

	public int delVoteProject(DruidPooledConnection conn, Long projectId) throws Exception {
		VoteProject renew = new VoteProject();
		renew.isActive = false;

		return projectRepository.updateByKey(conn, "id", projectId, renew, true);
	}

	/**
	 * 未开始返回-1</br>
	 * 正在进行返回0</br>
	 * 已经结束返回1</br>
	 */
	private int compareTime(Date startTime, Date expiryTime) {
		Date now = new Date();
		if (now.compareTo(startTime) > 0) {
			// 已经开始
			if (now.compareTo(expiryTime) > 0) {
				// 已经结束
				return 1;
			} else {
				return 0;
			}
		} else {
			return -1;
		}
	}

	public JSONObject getVoteProjectsByOrgId(DruidPooledConnection conn, Long orgId, Boolean isActive, Integer count,
			Integer offset) throws Exception {

		List<VoteProject> vs = projectRepository.getListByKeys(conn, new String[] { "org_id", "is_active" },
				new Object[] { orgId, isActive }, count, offset);

		JSONArray waiting = new JSONArray();
		JSONArray started = new JSONArray();
		JSONArray fininshed = new JSONArray();

		for (VoteProject v : vs) {
			int tmp = compareTime(v.startTime, v.expiryTime);
			if (tmp < 0) {
				waiting.add(v);
			} else if (tmp == 0) {
				started.add(v);
			} else {
				fininshed.add(v);
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("waiting", waiting);
		ret.put("started", started);
		ret.put("fininshed", fininshed);

		return ret;
	}

	private void checkProjectTime(DruidPooledConnection conn, Long projectId) throws Exception {
		if (true) {
			return;
		}
		VoteProject vp = projectRepository.getByKey(conn, "id", projectId);
		if (vp != null) {
			int tmp = compareTime(vp.startTime, vp.expiryTime);
			if (tmp >= 0) {
				// 已开始或已经结束，不能再修改
				throw new ServerException(BaseRC.ECM_VOIT_PROJECT_STARTED);
			} else {
				return;
			}
		} else {
			throw new ServerException(BaseRC.ECM_VOIT_PROJECT_NOTEXIST);
		}
	}

	public Vote addVote(DruidPooledConnection conn, Long orgId, Long projectId, Byte template, Byte type,
			Byte choiceCount, JSONArray crowd, Boolean reeditable, Boolean realName, Boolean isInternal,
			Boolean isAbstain, Byte effectiveRatio, Byte failureRatio, String title, String remark, String ext)
			throws Exception {

		checkProjectTime(conn, projectId);

		Vote v = new Vote();
		v.id = IDUtils.getSimpleId();
		v.orgId = orgId;
		v.projectId = projectId;
		v.template = template;
		v.type = type;
		v.choiceCount = choiceCount;
		v.status = Vote.STATUS.VOTING.v();

		v.crowd = crowd.toJSONString();
		v.reeditable = reeditable;
		v.realName = realName;
		v.isInternal = isInternal;
		v.isAbstain = isAbstain;

		v.effectiveRatio = effectiveRatio;
		v.failureRatio = failureRatio;
		v.title = title;
		v.remark = remark;
		v.ext = ext;

		// 应到人数
		v.quorum = orgRoleRepository.getParticipateCount(conn, orgId, crowd);

		voteRepository.insert(conn, v);

		if (isAbstain) {
			// 如果自动带有弃权选项，则默认创建一个VoteOption
			addVoteOption(conn, projectId, v.id, true, "弃权", "", "");
		}

		return v;
	}

	public int editVote(DruidPooledConnection conn, Long orgId, Long projectId, Long voteId, Byte template, Byte type,
			Byte choiceCount, JSONArray crowd, Boolean reeditable, Boolean realName, Boolean isInternal,
			Boolean isAbstain, Byte effectiveRatio, Byte failureRatio, String title, String remark, String ext)
			throws Exception {

		checkProjectTime(conn, projectId);

		Vote renew = new Vote();
		renew.template = template;
		renew.type = type;
		renew.choiceCount = choiceCount;

		renew.crowd = crowd.toJSONString();
		renew.reeditable = reeditable;
		renew.realName = realName;
		renew.isInternal = isInternal;
		renew.isAbstain = isAbstain;

		renew.effectiveRatio = effectiveRatio;
		renew.failureRatio = failureRatio;
		renew.title = title;
		renew.remark = remark;
		renew.ext = ext;

		// 应到人数
		renew.quorum = orgRoleRepository.getParticipateCount(conn, orgId, crowd);

		int ret = voteRepository.updateByKey(conn, "id", voteId, renew, true);

		VoteOption op = optionRepository.getByKeys(conn, new String[] { "vote_id", "is_abstain" },
				new Object[] { voteId, true });
		if (isAbstain) {
			// 没有要创建
			if (op == null) {
				addVoteOption(conn, projectId, voteId, true, "弃权", "", "");
			}
		} else {
			// 有要删除
			if (op != null) {
				delVoteOption(conn, projectId, voteId, op.id);
			}
		}
		return ret;
	}

	public int setVoteActivation(DruidPooledConnection conn, Long projectId, Long voteId, Boolean activation)
			throws Exception {
		checkProjectTime(conn, projectId);

		Vote exist = voteRepository.getByKey(conn, "id", voteId);
		if (exist == null) {
			throw new ServerException(BaseRC.ECM_VOIT_NOTEXIST);
		} else {
			// 完成或作废状态的Vote，状态无法被更改
			if (activation) {
				// 激活
				if (exist.status == Vote.STATUS.WAITING.v()) {
					// 只有waiting状态可以激活
					Vote renew = new Vote();
					renew.status = Vote.STATUS.VOTING.v();

					return voteRepository.updateByKey(conn, "id", voteId, renew, true);
				}
			} else {
				// 禁用
				if (exist.status == Vote.STATUS.VOTING.v()) {
					// 只有投票中可以被禁用
					Vote renew = new Vote();
					renew.status = Vote.STATUS.WAITING.v();

					return voteRepository.updateByKey(conn, "id", voteId, renew, true);
				}
			}
		}

		return 0;
	}

	public int setVotePaused(DruidPooledConnection conn, Long projectId, Long voteId, Boolean paused) throws Exception {
		checkProjectTime(conn, projectId);

		Vote exist = voteRepository.getByKey(conn, "id", voteId);
		if (exist == null) {
			throw new ServerException(BaseRC.ECM_VOIT_NOTEXIST);
		} else {
			if (paused) {
				// 人为废除
				if (exist.status == Vote.STATUS.VOTING.v()) {
					// 只有投票中可以被人为废除
					Vote renew = new Vote();
					renew.status = Vote.STATUS.PAUSED.v();

					return voteRepository.updateByKey(conn, "id", voteId, renew, true);
				}
			} else {
				// 恢复
				if (exist.status == Vote.STATUS.PAUSED.v()) {
					// 只有人为废除中可以被恢复
					Vote renew = new Vote();
					renew.status = Vote.STATUS.VOTING.v();

					return voteRepository.updateByKey(conn, "id", voteId, renew, true);
				}
			}
		}

		return 0;
	}

	/**
	 * 重新设置选项编号列表（顺序）
	 */
	public int setVoteOptionIds(DruidPooledConnection conn, Long projectId, Long voteId, JSONArray optionIds)
			throws Exception {
		checkProjectTime(conn, projectId);
		Vote renew = new Vote();
		renew.optionIds = JSON.toJSONString(optionIds);

		return voteRepository.updateByKey(conn, "id", voteId, renew, true);
	}

	public int delVote(DruidPooledConnection conn, Long projectId, Long voteId) throws Exception {

		checkProjectTime(conn, projectId);

		return voteRepository.deleteByKey(conn, "id", voteId);

	}

	public List<Vote> getVotes(DruidPooledConnection conn, Long projectId) throws Exception {
		return voteRepository.getListByKey(conn, "project_id", projectId, 512, 0);
	}

	public VoteOption addVoteOption(DruidPooledConnection conn, Long projectId, Long voteId, Boolean isAbstain,
			String title, String remark, String ext) throws Exception {

		checkProjectTime(conn, projectId);

		VoteOption vo = new VoteOption();
		vo.id = IDUtils.getSimpleId();
		vo.voteId = voteId;
		vo.isAbstain = isAbstain;
		vo.title = title;
		vo.remark = remark;
		vo.ext = ext;

		// 必须赋值，否则为空的话，自增会失败
		vo.ballotCount = 0;
		vo.weight = 0;

		optionRepository.insert(conn, vo);

		Vote vote = voteRepository.getByKey(conn, "id", voteId);
		if (vote != null) {
			JSONArray ar = JSON.parseArray(vote.optionIds);
			if (ar == null) {
				ar = new JSONArray();
			}
			ar.add(vo.id);

			Vote renew = new Vote();
			renew.optionIds = JSON.toJSONString(ar);
			voteRepository.updateByKey(conn, "id", voteId, renew, true);
		}
		return vo;
	}

	public int editVoteOption(DruidPooledConnection conn, Long projectId, Long optionId, String title, String remark,
			String ext) throws Exception {

		checkProjectTime(conn, projectId);

		VoteOption renew = new VoteOption();
		renew.title = title;
		renew.remark = remark;
		renew.ext = ext;

		return optionRepository.updateByKey(conn, "id", optionId, renew, true);
	}

	public int delVoteOption(DruidPooledConnection conn, Long projectId, Long voteId, Long optionId) throws Exception {
		checkProjectTime(conn, projectId);

		int ret = optionRepository.deleteByKey(conn, "id", optionId);
		if (ret == 1) {
			Vote vote = voteRepository.getByKey(conn, "id", voteId);
			if (vote != null) {
				JSONArray ar = JSON.parseArray(vote.optionIds);
				if (ar == null) {
					ar = new JSONArray();
				}
				ar.remove(optionId);

				Vote renew = new Vote();
				renew.optionIds = JSON.toJSONString(ar);
				voteRepository.updateByKey(conn, "id", voteId, renew, true);
			}
		}
		return ret;
	}

	public List<VoteOption> getVoteOptions(DruidPooledConnection conn, Long voteId) throws Exception {

		Vote vote = voteRepository.getByKey(conn, "id", voteId);

		JSONArray ja = JSON.parseArray(vote.optionIds);

		if (ja != null && ja.size() > 0) {
			String[] values = new String[ja.size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = ja.get(i).toString();
			}

			return optionRepository.getListByKeyInValues(conn, "id", values);
		} else {
			return new ArrayList<VoteOption>();
		}
	}

	private boolean checkPrmission(Byte[] crowd, Byte t) {
		for (int i = 0; i < crowd.length; i++) {
			if (crowd[i] == Vote.CROWD.ALL.v()) {
				// 包含一个全部
				return true;
			}
		}
		return false;
	}

	private boolean hasPrmission(Byte[] crowd, ORGRole or) {
		if (checkPrmission(crowd, Vote.CROWD.ALL.v())) {
			return true;
		}
		if (or.share == ORGRole.SHARE.SHAREHOLDER.v()) {
			if (checkPrmission(crowd, Vote.CROWD.SHAREHOLDER.v())) {
				return true;
			}
		}
		if (or.share == ORGRole.SHARE.REPRESENTATIVE.v()) {
			if (checkPrmission(crowd, Vote.CROWD.REPRESENTATIVE.v())) {
				return true;
			}
		}
		if (or.duty != ORGRole.DUTY.NONE.v()) {
			if (checkPrmission(crowd, Vote.CROWD.DIRECTOR.v())) {
				return true;
			}
		}
		if (or.visor != ORGRole.VISOR.NONE.v()) {
			if (checkPrmission(crowd, Vote.CROWD.SUPERVISOR.v())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 投票，截止时间前，都可以投票
	 */
	public void vote(DruidPooledConnection conn, Long orgId, Long projectId, Long voteId, Long userId,
			JSONArray selections, Integer ballotCount, String remark) throws Exception {

		// 判断project是否开启，可以投票
		// VoteProject vp = projectRepository.getByKey(conn, "id", projectId);
		// if (vp != null) {
		// // int tmp = compareTime(vp.startTime, vp.expiryTime);
		// // if (tmp == 0) {
		// // // 已开始，未结束，正好
		// // // continue
		// // } else if (tmp < 0) {
		// // throw new ServerException(BaseRC.ECM_VOIT_PROJECT_NOTSTARTED);
		// // } else {
		// // throw new ServerException(BaseRC.ECM_VOIT_PROJECT_FINISHED);
		// // }
		// } else {
		// throw new ServerException(BaseRC.ECM_VOIT_PROJECT_NOTEXIST);
		// }

		Vote vote = null;
		try {
			vote = CacheCenter.VOTE_CACHE.get(voteId);
		} catch (Exception eee) {
		}
		if (vote == null) {
			// 缓存中没有，从数据库中获取
			vote = voteRepository.getByKey(conn, "id", voteId);
			if (vote == null) {
				throw new ServerException(BaseRC.ECM_VOIT_NOTEXIST);
			} else {
				// 放入缓存
				CacheCenter.VOTE_CACHE.put(voteId, vote);
			}
		}

		// 非投票中状态，抛出异常
		if (vote.status != Vote.STATUS.VOTING.v()) {
			throw new ServerException(BaseRC.ECM_VOIT_STATUS_ERROR);
		}

		JSONArray jc = JSONArray.parseArray(vote.crowd);

		// 判断用户是否有权限投票
		ORGRole or = orgRoleRepository.getByKeys(conn, new String[] { "org_id", "user_id" },
				new Object[] { orgId, userId });
		if (or == null) {
			throw new ServerException(BaseRC.ECM_VOTE_ORGROLE_ERROR);
		}

		Byte[] crowd = new Byte[jc.size()];
		for (int i = 0; i < crowd.length; i++) {
			crowd[i] = jc.getByte(i);
		}

		if (hasPrmission(crowd, or)) {
			// 开始投票
			VoteTicket exist = ticketRepository.getByKeys(conn, new String[] { "vote_id", "user_id" },
					new Object[] { voteId, userId });

			boolean firstTime = (exist == null ? true : false);

			// 处理选票
			if (firstTime) {
				// 该用户没有投过票，第一次
				if (selections.size() <= 0) {
					return;
				}

				VoteTicket vt = new VoteTicket();
				vt.voteId = voteId;
				vt.userId = userId;
				vt.voteTime = new Date();
				vt.ballotCount = ballotCount;
				vt.selection = JSON.toJSONString(selections);
				vt.remark = remark;

				// 创建选票
				ticketRepository.insert(conn, vt);

				// 计票
				String[] ids = new String[selections.size()];
				for (int i = 0; i < ids.length; i++) {
					ids[i] = selections.getLong(i).toString();
				}
				optionRepository.countTicket(conn, ids, ballotCount);

			} else {
				// 不再创建，更新

				// 目前不让再投了
				throw new ServerException(BaseRC.ECM_TICKET_EXIST);
			}
		} else {
			throw new ServerException(BaseRC.ECM_VOTE_NO_PROMISS);
		}
	}

	/**
	 * 获取投票详细
	 */
	public JSONObject getVoteDetail(DruidPooledConnection conn, Long voteId) throws Exception {
		Vote vote = voteRepository.getByKey(conn, "id", voteId);

		List<VoteOption> options = optionRepository.getListByKey(conn, "vote_id", voteId, 512, 0);

		JSONObject ret = new JSONObject();
		ret.put("vote", vote);
		ret.put("ops", options);

		return ret;
	}

	/**
	 * 获取用户的选票
	 */
	public VoteTicket getVoteTicket(DruidPooledConnection conn, Long voteId, Long userId) throws Exception {
		return ticketRepository.getByKeys(conn, new String[] { "vote_id", "user_id" }, new Object[] { voteId, userId });
	}
}
