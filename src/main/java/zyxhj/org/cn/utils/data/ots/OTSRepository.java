package zyxhj.org.cn.utils.data.ots;

import com.alicloud.openservices.tablestore.model.Condition;
import com.alicloud.openservices.tablestore.model.DeleteRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowRequest;
import com.alicloud.openservices.tablestore.model.GetRowResponse;
import com.alicloud.openservices.tablestore.model.PrimaryKey;
import com.alicloud.openservices.tablestore.model.PutRowRequest;
import com.alicloud.openservices.tablestore.model.Row;
import com.alicloud.openservices.tablestore.model.RowDeleteChange;
import com.alicloud.openservices.tablestore.model.RowExistenceExpectation;
import com.alicloud.openservices.tablestore.model.RowPutChange;
import com.alicloud.openservices.tablestore.model.SingleRowQueryCriteria;

import zyxhj.org.cn.utils.api.BaseRC;
import zyxhj.org.cn.utils.api.ServerException;

public abstract class OTSRepository<T> {

	protected OTSObjectMapper<T> mapper;

	protected OTSRepository(Class<T> clazz) {
		this.mapper = new OTSObjectMapper<T>(clazz);
	}

	/**
	 * 插入，检查是否存在，如果不存在，插入；否则插入失败 <功能详细描述>
	 * 
	 */
	public boolean putObject(OTSAutoCloseableClient client, T t, boolean cover) throws ServerException {
		try {
			PrimaryKey primaryKey = mapper.getPrimaryKeyFromObject(t);
			RowPutChange rowPutChange = new RowPutChange(mapper.getTableName(), primaryKey);

			// 排除重复，不然会覆盖，不检查的话，默认会覆盖
			if (!cover) {
				// 预期不存在，如果存在则异常
				rowPutChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_NOT_EXIST));
			}

			// 添加需要修改的列值
			rowPutChange.addColumns(mapper.getColumnListFromObject(t));

			// 开始写入
			client.putRow(new PutRowRequest(rowPutChange));
			return true;
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_PUT_ERROR, e.getMessage());
		}
	}

	public T getByKey(OTSAutoCloseableClient client, Object... params) throws ServerException {
		try {
			PrimaryKey primaryKey = mapper.getPrimaryKeyFromParams(params);

			SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(mapper.getTableName(), primaryKey);
			// 设置读取最新版本
			criteria.setMaxVersions(1);

			GetRowResponse getRowResponse = client.getRow(new GetRowRequest(criteria));
			Row row = getRowResponse.getRow();
			if (row.isEmpty()) {
				return null;
			} else {
				return mapper.deserialize(row);
			}
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_GET_ERROR);
		}
	}

	public void deleteByKey(OTSAutoCloseableClient client, Object... params) throws ServerException {
		try {
			PrimaryKey primaryKey = mapper.getPrimaryKeyFromParams(params);

			RowDeleteChange rowChange = new RowDeleteChange(mapper.getTableName(), primaryKey);
			rowChange.setCondition(new Condition(RowExistenceExpectation.EXPECT_EXIST));

			DeleteRowRequest request = new DeleteRowRequest();
			request.setRowChange(rowChange);
			client.deleteRow(request);
		} catch (Exception e) {
			throw new ServerException(BaseRC.REPOSITORY_DELETE_ERROR);
		}
	}

	// protected List<T> getRange(OTSAutoCloseableClient client,
	// OTSRangeParamSetter setter) throws ServerException {
	// RangeRowQueryCriteria criteria = new
	// RangeRowQueryCriteria(mapper.getTableName());
	// setter.set(criteria);
	// String[] cols = mapper.getAliasList();
	// criteria.addColumnsToGet(cols);
	//
	// GetRangeRequest request = new GetRangeRequest();
	// request.setRangeRowQueryCriteria(criteria);
	// GetRangeResult result = client.getRange(request);
	//
	// List<Row> rows = result.getRows();
	//
	// List<T> ret = new ArrayList<>();
	// try {
	// for (Row row : rows) {
	// T t = mapper.deserialize(row);
	// ret.add(t);
	// }
	// return ret;
	// } catch (Exception e) {
	// throw new ServerException(BaseRC.REPOSITORY_GET_ERROR);
	// }
	// }
	//
	// /**
	// * OTSMap存储结果，list里存返回列表，next存下一个RowPrimaryKey
	// * <功能详细描述>
	// * @param client
	// * @param nextStartPrimaryKey
	// * @param setter
	// * @return
	// * @throws ServerException
	// * @see [类、类#方法、类#成员]
	// */
	// protected OTSMap<T> getRangeMap(OTSAutoCloseableClient client,
	// OTSRangeParamSetter setter) throws ServerException {
	// RangeRowQueryCriteria criteria = new
	// RangeRowQueryCriteria(mapper.getTableName());
	// setter.set(criteria);
	// String[] cols = mapper.getAliasList();
	// criteria.addColumnsToGet(cols);
	//
	// GetRangeRequest request = new GetRangeRequest();
	// request.setRangeRowQueryCriteria(criteria);
	// GetRangeResult result = client.getRange(request);
	//
	// List<Row> rows = result.getRows();
	//
	// List<T> ret = new ArrayList<>();
	// try {
	// for (Row row : rows) {
	// T t = mapper.deserialize(row);
	// ret.add(t);
	// }
	// OTSMap<T> otsMap = new OTSMap<>();
	// RowPrimaryKey rpk = result.getNextStartPrimaryKey();
	// Map<String, Object> nextPkMap = new HashMap<>();
	// if (rpk != null) {
	// Map<String, PrimaryKeyValue> pks = rpk.getPrimaryKey();
	// if (pks != null && !pks.isEmpty()) {
	//
	// for (Entry<String, PrimaryKeyValue> entry : pks.entrySet()) {
	// String key = entry.getKey();
	// PrimaryKeyValue pkv = entry.getValue();
	// PrimaryKeyType pkt = pkv.getType();
	// if (PrimaryKeyType.INTEGER.name().equals(pkt.name())) {
	// nextPkMap.put(key, pkv.asLong());
	// } else if (PrimaryKeyType.STRING.name().equals(pkt.name())) {
	// nextPkMap.put(key, pkv.asString());
	// } else if (PrimaryKeyType.BINARY.name().equals(pkt.name())) {
	// nextPkMap.put(key, pkv.asBinary());
	// } else {
	// logger.info("载入OTSMap，未知的primarykey类型...");
	// }
	// }
	// }
	// }
	// otsMap.nextPkMap = nextPkMap;
	// otsMap.list = ret;
	// return otsMap;
	// } catch (Exception e) {
	// throw new ServerException(BaseRC.REPOSITORY_GET_ERROR);
	// }
	// }

}
