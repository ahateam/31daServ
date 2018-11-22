package zyxhj.core.repository;

import zyxhj.core.domain.Tag;
import zyxhj.org.cn.utils.data.rds.RDSRepository;

public class TagRepository extends RDSRepository<Tag> {

	private static TagRepository ins;

	public static synchronized TagRepository getInstance() {
		if (null == ins) {
			ins = new TagRepository();
		}
		return ins;
	}

	private TagRepository() {
		super(Tag.class);
	}

}
