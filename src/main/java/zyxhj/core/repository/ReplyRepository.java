package zyxhj.core.repository;

import zyxhj.core.domain.Reply;
import zyxhj.utils.data.rds.RDSRepository;

public class ReplyRepository extends RDSRepository<Reply> {

	public ReplyRepository() {
		super(Reply.class);
	}

}
