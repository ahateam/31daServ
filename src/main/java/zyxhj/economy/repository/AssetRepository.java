package zyxhj.economy.repository;

import zyxhj.economy.domain.Asset;
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

}
