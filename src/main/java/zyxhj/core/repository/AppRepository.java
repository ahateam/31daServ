package zyxhj.core.repository;

import zyxhj.core.domain.App;
import zyxhj.utils.data.rds.RDSRepository;

public class AppRepository extends RDSRepository<App> {

	private static AppRepository ins;

	public static synchronized AppRepository getInstance() {
		if (null == ins) {
			ins = new AppRepository();
		}
		return ins;
	}

	private AppRepository() {
		super(App.class);
	}

}
