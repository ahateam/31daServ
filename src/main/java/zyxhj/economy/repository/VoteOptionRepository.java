package zyxhj.economy.repository;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.Vote;
import zyxhj.economy.domain.VoteOption;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class VoteOptionRepository extends RDSRepository<VoteOption> {

	private static VoteOptionRepository ins;

	public static synchronized VoteOptionRepository getInstance() {
		if (null == ins) {
			ins = new VoteOptionRepository();
		}
		return ins;
	}

	private VoteOptionRepository() {
		super(VoteOption.class);
	}

	public void countTicket(DruidPooledConnection conn, Long id, boolean firstTime, Byte oldOpt, Byte newOpt,
			Integer oldWeight, Integer newWeight) throws ServerException {
		if (firstTime) {
			// 第一次，直接计票

			StringBuffer set = new StringBuffer("SET ");
			if (newOpt == Vote.OPT_AGREE) {
				set.append(" agree_counter=agree_counter+1 , agree_weight=agree_weight+?");
			} else if (newOpt == Vote.OPT_DISAGREE) {
				set.append(" disagree_counter=disagree_counter+1 , disagree_weight=disagree_weight+?");
			} else {
				set.append(" abstained_counter=abstained_counter+1 , abstained_weight=abstained_weight+?");
			}
			update(conn, set.toString(), new Object[] { newWeight }, "WHERE id=?", new Object[] { id });
		} else {
			// 不是第一次，从新计算并计票
			if (oldOpt == newOpt) {
				// 两次选项相同，不重新计票
				return;
			} else {
				// 把原来的改回来
				StringBuffer set = new StringBuffer("SET ");
				if (oldOpt == Vote.OPT_AGREE) {
					set.append(" agree_counter=agree_counter-1 , agree_weight=agree_weight-?");
				} else if (oldOpt == Vote.OPT_DISAGREE) {
					set.append(" disagree_counter=disagree_counter-1 , disagree_weight=disagree_weight-?");
				} else {
					set.append(" abstained_counter=abstained_counter-1 , abstained_weight=abstained_weight-?");
				}
				// 把现在的加上去
				if (newOpt == Vote.OPT_AGREE) {
					set.append(" , agree_counter=agree_counter+1 , agree_weight=agree_weight+?");
				} else if (newOpt == Vote.OPT_DISAGREE) {
					set.append(" , disagree_counter=disagree_counter+1 , disagree_weight=disagree_weight+?");
				} else {
					set.append(" , abstained_counter=abstained_counter+1 , abstained_weight=abstained_weight+?");
				}
				update(conn, set.toString(), new Object[] { oldWeight, newWeight }, "WHERE id=?", new Object[] { id });
			}
		}
	}
}
