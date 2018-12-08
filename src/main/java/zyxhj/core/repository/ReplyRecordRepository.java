package zyxhj.core.repository;

import zyxhj.core.domain.ReplyRecord;
import zyxhj.utils.data.rds.RDSRepository;

public class ReplyRecordRepository extends RDSRepository<ReplyRecord> {

	private static ReplyRecordRepository ins;

	public static synchronized ReplyRecordRepository getInstance() {
		if (null == ins) {
			ins = new ReplyRecordRepository();
		}
		return ins;
	}

	private ReplyRecordRepository() {
		super(ReplyRecord.class);
	}

}
