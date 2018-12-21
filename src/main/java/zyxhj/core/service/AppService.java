package zyxhj.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.core.domain.App;
import zyxhj.core.repository.AppRepository;
import zyxhj.utils.IDUtils;

public class AppService {

	private static Logger log = LoggerFactory.getLogger(AppService.class);

	private static AppService ins;

	public static synchronized AppService getInstance() {
		if (null == ins) {
			ins = new AppService();
		}
		return ins;
	}

	private AppRepository appRepository;

	private AppService() {
		try {
			appRepository = AppRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public App createApp(DruidPooledConnection conn, String name, String remark) throws Exception {
		App app = new App();
		app.id = IDUtils.getSimpleId();
		app.name = name;
		app.remark = remark;

		appRepository.insert(conn, app);

		return app;
	}

}
