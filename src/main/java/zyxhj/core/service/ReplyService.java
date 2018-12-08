package zyxhj.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zyxhj.core.repository.ReplyRepository;

public class ReplyService {

	private static Logger log = LoggerFactory.getLogger(ReplyService.class);

	private static ReplyService ins;

	public static synchronized ReplyService getInstance() {
		if (null == ins) {
			ins = new ReplyService();
		}
		return ins;
	}

	private ReplyRepository replyRepository;

	private ReplyService() {
		try {
			replyRepository = ReplyRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
