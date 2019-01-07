package zyxhj.economy.repository;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.Asset;
import zyxhj.utils.api.ServerException;
import zyxhj.utils.data.rds.RDSRepository;

public class AssetRepository extends RDSRepository<Asset> {

	private static AssetRepository ins;

	public static synchronized AssetRepository getInstance() {
		if (null == ins) {
			ins = new AssetRepository();
		}
		return ins;
	}

	private AssetRepository() {
		super(Asset.class);
	}

	public List<Asset> searchAssets(DruidPooledConnection conn, Long orgId, String assetType, Integer count,
			Integer offset) throws ServerException {
		ArrayList<Object> objs = new ArrayList<>();

		StringBuffer sb = new StringBuffer();
		sb.append("WHERE org_id=? ");
		if (null != assetType) {
			sb.append("AND asset_type=? ");
			objs.add(assetType);
		}

		return this.getList(conn, sb.toString(), objs.toArray(), count, offset);
	}
}
