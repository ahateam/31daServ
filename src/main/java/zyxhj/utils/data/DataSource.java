package zyxhj.utils.data;

import zyxhj.utils.api.ServerException;

/**
 */
public interface DataSource {

	public Object openConnection() throws ServerException;

	public void closeConnection(Object conn) throws ServerException;
}
