package zyxhj.core.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xhj.cn.start.MainVerticle;
import zyxhj.utils.api.Controller;

public class ServerController extends Controller {

	private static Logger log = LoggerFactory.getLogger(ServerController.class);

	private static ServerController ins;

	public static synchronized ServerController getInstance(String node, MainVerticle server) {
		if (null == ins) {
			ins = new ServerController(node, server);
		}
		return ins;
	}

	private MainVerticle server;

	private ServerController(String node, MainVerticle server) {
		super(node);
		this.server = server;
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getCtrlList", //
			des = "获取接口列表"//
	)
	public void getCtrlList() throws Exception {
		Map<String, Controller> map = server.getCtrlList();
		
		
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getCtrlDetail", //
			des = "获取接口详细"//
	)
	public void getCtrlDetail(String name) throws Exception {
		server.getCtrlDetail(name);
	}

}
