package zyxhj.economy.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import zyxhj.economy.domain.Vote;
import zyxhj.economy.domain.VoteOption;
import zyxhj.economy.domain.VoteTicket;
import zyxhj.economy.repository.VoteOptionRepository;
import zyxhj.economy.repository.VoteRepository;
import zyxhj.economy.repository.VoteTicketRepository;
import zyxhj.utils.IDUtils;

public class VoteService {

	private static Logger log = LoggerFactory.getLogger(VoteService.class);

	private static VoteService ins;

	public static synchronized VoteService getInstance() {
		if (null == ins) {
			ins = new VoteService();
		}
		return ins;
	}

	private VoteRepository voteRepository;
	private VoteOptionRepository optionRepository;
	private VoteTicketRepository ticketRepository;

	private VoteService() {
		try {
			voteRepository = VoteRepository.getInstance();
			optionRepository = VoteOptionRepository.getInstance();
			ticketRepository = VoteTicketRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 创建投票
	 */
	public Vote createVote(DruidPooledConnection conn, Long orgId, String title, String remark, Byte type,
			Byte choiceCount, Date startTime, Date expiryTime) throws Exception {
		Vote o = new Vote();

		o.id = IDUtils.getSimpleId();
		o.orgId = orgId;
		o.title = title;
		o.remark = remark;
		o.type = type;
		o.choiceCount = choiceCount;
		o.createTime = new Date();
		o.startTime = startTime;
		o.expiryTime = expiryTime;

		System.out.println("-----");
		System.out.println("-----" + o.createTime);
		System.out.println("-----" + o.startTime);
		System.out.println("-----" + o.expiryTime);
		System.out.println("-----");

		voteRepository.insert(conn, o);
		return o;
	}

	/**
	 * 获取投票列表，暂时简单做
	 */
	public List<Vote> getVotes(DruidPooledConnection conn, Long orgId, int count, int offset) throws Exception {
		return voteRepository.getListByKey(conn, "org_id", orgId, count, offset);
	}

	/**
	 * 获取投票的选项列表
	 */
	public List<VoteOption> getVoteOptions(DruidPooledConnection conn, Long voteId) throws Exception {
		return optionRepository.getListByKey(conn, "vote_id", voteId, 512, 0);
	}

	/**
	 * 为投票添加选项
	 */
	public VoteOption addVoteOption(DruidPooledConnection conn, Long voteId, String title, String remark)
			throws Exception {
		VoteOption vo = new VoteOption();
		vo.id = IDUtils.getSimpleId();
		vo.voteId = voteId;
		vo.title = title;
		vo.remark = remark;

		// 必须赋值，否则为空的话，自增会失败
		vo.agreeCounter = 0;
		vo.agreeWeight = 0;
		vo.disagreeCounter = 0;
		vo.disagreeWeight = 0;
		vo.abstainedCounter = 0;
		vo.abstainedWeight = 0;

		optionRepository.insert(conn, vo);

		return vo;
	}

	/**
	 * 设置投票选项
	 */
	public void setVoteOption(DruidPooledConnection conn, Long optionId, String title, String remark) throws Exception {
		VoteOption renew = new VoteOption();
		renew.title = title;
		renew.remark = remark;

		optionRepository.updateByKey(conn, "id", optionId, renew, true);
	}

	/**
	 * 删除投票选项
	 */
	public void delVoteOption(DruidPooledConnection conn, Long optionId) throws Exception {
		optionRepository.deleteByKey(conn, "id", optionId);
	}

	/**
	 * 投票，截止时间前，都可以投票
	 */
	public void vote(DruidPooledConnection conn, Long voteId, Long userId, JSONArray selections, Integer weight,
			String remark) throws Exception {

		VoteTicket exist = ticketRepository.getByKeys(conn, new String[] { "vote_id", "user_id" },
				new Object[] { voteId, userId });

		boolean firstTime = (exist == null ? true : false);

		// 处理选票
		if (firstTime) {
			// 该用户没有投过票，第一次

			VoteTicket vt = new VoteTicket();
			vt.voteId = voteId;
			vt.userId = userId;
			vt.voteTime = new Date();
			vt.selection = JSON.toJSONString(selections);
			vt.weight = weight;
			vt.remark = remark;

			// 创建选票
			ticketRepository.insert(conn, vt);
		} else {
			// 不再创建，更新
			VoteTicket renew = new VoteTicket();
			renew.voteTime = new Date();
			renew.selection = JSON.toJSONString(selections);
			renew.weight = weight;
			renew.remark = remark;

			ticketRepository.updateByKeys(conn, new String[] { "vote_id", "user_id" }, new Object[] { voteId, userId },
					renew, true);
		}

		// 计票
		if (firstTime) {
			for (int i = 0; i < selections.size(); i++) {
				JSONObject jo = selections.getJSONObject(i);
				Long optionId = Long.parseLong(jo.getString("option"));
				Byte opt = jo.getByte("opt");

				optionRepository.countTicket(conn, optionId, true, null, opt, weight, weight);
			}
		} else {
			// 曾经投过票
			// 需要将以前的投票结果和现状的投票结果比对后，重新计票

			JSONArray old = JSON.parseArray(exist.selection);

			// 将新旧选择放入map中，按编号去取值，避免json顺序导致结果错误
			HashMap<Long, Byte> oldMap = new HashMap<>();
			HashMap<Long, Byte> newMap = new HashMap<>();
			for (int i = 0; i < old.size(); i++) {
				JSONObject jo = old.getJSONObject(i);
				oldMap.put(Long.parseLong(jo.getString("option")), jo.getByte("opt"));
			}
			for (int i = 0; i < selections.size(); i++) {
				JSONObject jo = selections.getJSONObject(i);
				newMap.put(Long.parseLong(jo.getString("option")), jo.getByte("opt"));
			}

			// 逐条和新的进行对比并重新计票
			for (int i = 0; i < selections.size(); i++) {
				JSONObject jo = selections.getJSONObject(i);
				Long optionId = Long.parseLong(jo.getString("option"));

				Byte oldOpt = oldMap.get(optionId);
				Byte newOpt = newMap.get(optionId);

				optionRepository.countTicket(conn, optionId, false, oldOpt, newOpt, exist.weight, weight);
			}
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
