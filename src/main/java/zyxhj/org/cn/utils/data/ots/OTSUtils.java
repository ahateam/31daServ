package zyxhj.org.cn.utils.data.ots;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.Column;
import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PrimaryKeyBuilder;
import com.alicloud.openservices.tablestore.model.PrimaryKeyValue;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.RowPutChange;

public class OTSUtils {

	public static void main(String[] args) {

		final String endPoint = "http://topointsSpeed.cn-beijing.ots.aliyuncs.com";
		final String accessKeyId = "Q6q7mKRDJpAxlnYm";
		final String accessKeySecret = "7j4QVsWTL6KI2GWAqK05og0M2x1Qrl";
		final String instanceName = "topointsSpeed";

		SyncClient client = new SyncClient(endPoint, accessKeyId, accessKeySecret, instanceName);

		
		long testId = 111L;
		putRow(client,testId);

	}

	private static final String PRIMARY_KEY_NAME = "id";
	private static final String TABLE_NAME = "session";

	private static void putRow(SyncClient client, long pkValue) {
		// 构造主键
		PrimaryKeyBuilder primaryKeyBuilder = PrimaryKeyBuilder.createPrimaryKeyBuilder();
		primaryKeyBuilder.addPrimaryKeyColumn(PRIMARY_KEY_NAME, PrimaryKeyValue.fromLong(pkValue));
		PrimaryKey primaryKey = primaryKeyBuilder.build();

		RowPutChange rowPutChange = new RowPutChange(TABLE_NAME, primaryKey);
		// 加入一些属性列
		for (int i = 0; i < 10; i++) {
			rowPutChange.addColumn(new Column("Col" + i, ColumnValue.fromLong(i)));
		}
		client.putRow(new PutRowRequest(rowPutChange));
	}
}
