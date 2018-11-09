package zyxhj.org.cn.utils.data.ots;

import com.alicloud.openservices.tablestore.SyncClient;

public class OTSAutoCloseableClient extends SyncClient implements AutoCloseable {

	public OTSAutoCloseableClient(String endpoint, String accessKeyId, String accessKeySecret, String instanceName) {
		super(endpoint, accessKeyId, accessKeySecret, instanceName);
	}

	@Override
	public void close() throws Exception {
		// do nothing
		// OTS 公用一个连接，不关闭。
		// 实现AutoCloseable只是为了统一代码风格
	}

}
