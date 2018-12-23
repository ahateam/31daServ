package zyxhj.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import io.vertx.core.json.JsonObject;
import zyxhj.utils.api.Controller;

public class ServerAssetController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ServerAssetController.class);

	private static ServerAssetController ins;

	public static synchronized ServerAssetController getInstance(String node) {
		if (null == ins) {
			ins = new ServerAssetController(node);
		}
		return ins;
	}

	private ServerAssetController(String node) {
		super(node);
	}

}
