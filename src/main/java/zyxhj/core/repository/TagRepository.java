package zyxhj.core.repository;

import zyxhj.core.domain.Tag;
import zyxhj.utils.data.rds.RDSRepository;

public class TagRepository extends RDSRepository<Tag> {

	public TagRepository() {
		super(Tag.class);
	}

}
