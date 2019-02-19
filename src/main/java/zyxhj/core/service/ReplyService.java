package zyxhj.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zyxhj.core.repository.ReplyRepository;
import zyxhj.utils.Singleton;

public class ReplyService {

	private static Logger log = LoggerFactory.getLogger(ReplyService.class);

	private ReplyRepository replyRepository;

	public ReplyService() {
		try {
			replyRepository = Singleton.ins(ReplyRepository.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
