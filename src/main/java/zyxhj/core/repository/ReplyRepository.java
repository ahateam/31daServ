package zyxhj.core.repository;

import zyxhj.core.domain.Reply;
import zyxhj.utils.data.rds.RDSRepository;

public class ReplyRepository extends RDSRepository<Reply> {

	private static ReplyRepository ins;

	public static synchronized ReplyRepository getInstance() {
		if (null == ins) {
			ins = new ReplyRepository();
		}
		return ins;
	}

	private ReplyRepository() {
		super(Reply.class);
	}

}
