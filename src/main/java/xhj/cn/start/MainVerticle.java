package xhj.cn.start;

import io.vertx.core.Vertx;
import zyxhj.core.controller.TagController;
import zyxhj.core.controller.TestController;
import zyxhj.core.controller.UserController;
import zyxhj.utils.Singleton;
import zyxhj.utils.ZeroVerticle;
import zyxhj.utils.data.DataSourceUtils;

public class MainVerticle extends ZeroVerticle {

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new MainVerticle());
	}

	public String name() {
		return "zero";
	}

	protected void init() throws Exception {

		DataSourceUtils.initDataSourceConfig();

		initCtrl(ctrlMap, Singleton.ins(TestController.class, "test"));

		initCtrl(ctrlMap, Singleton.ins(UserController.class, "user"));

		initCtrl(ctrlMap, Singleton.ins(TagController.class, "tag"));

	}

}
