package zyxhj.core.repository;

import zyxhj.core.domain.Valid;
import zyxhj.org.cn.utils.data.ots.OTSRepository;

public class ValidRepository extends OTSRepository<Valid> {

	private static ValidRepository ins;

	public static synchronized ValidRepository getInstance() {
		if (null == ins) {
			ins = new ValidRepository();
		}
		return ins;
	}

	private ValidRepository() {
		super(Valid.class);
	}

}