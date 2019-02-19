package zyxhj.core.repository;

import zyxhj.core.domain.ReplyRecord;
import zyxhj.utils.data.rds.RDSRepository;

public class ReplyRecordRepository extends RDSRepository<ReplyRecord> {

	public ReplyRecordRepository() {
		super(ReplyRecord.class);
	}

}
