package zyxhj.utils.api;

/**
 * 服务器内部错误
 * 
 */
@SuppressWarnings("serial")
public class ServerException extends Exception {

	private RC rc;

	public ServerException(RC rc, String msg) {
		super(msg);
		this.rc = rc;
	}

	public ServerException(RC rc) {
		super(rc.msg);
		this.rc = rc;
	}

	public RC getRC() {
		return rc;
	}

}
