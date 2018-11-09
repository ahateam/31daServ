package zyxhj.org.cn.utils.data;

import zyxhj.org.cn.utils.api.ServerException;

/**
 */
public interface DataSource {

	public Object openConnection() throws ServerException;

	public void closeConnection(Object conn) throws ServerException;
}
