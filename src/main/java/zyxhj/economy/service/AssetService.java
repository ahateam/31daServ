package zyxhj.economy.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.Asset;
import zyxhj.economy.repository.AssetRepository;
import zyxhj.utils.IDUtils;

public class AssetService {

	private static Logger log = LoggerFactory.getLogger(AssetService.class);

	private static AssetService ins;

	public static synchronized AssetService getInstance() {
		if (null == ins) {
			ins = new AssetService();
		}
		return ins;
	}

	private AssetRepository assetRepository;

	private AssetService() {
		try {
			assetRepository = AssetRepository.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public Asset createAsset(DruidPooledConnection conn, Long orgId, //
			String originId, String name, String sn, String resType, String assetType, //
			Date buildTime, Double originPrice, String location, String ownership, String keeper, //
			String businessMode, String businessTime, String holder, Double yearlyIncome, String specType, //
			String estateType, Double area, Double floorArea, String boundary, String loctionStart, //
			String loctionEnd, String coordinateStart, String coordinateEnd, Double accumulateStock, Integer treeNumber, //
			String imgs, String remark) throws Exception {
		Asset a = new Asset();
		a.id = IDUtils.getSimpleId();
		a.orgId = orgId;
		a.createTime = new Date();

		a.originId = originId;
		a.name = name;
		a.sn = sn;
		a.resType = resType;
		a.assetType = assetType;

		a.buildTime = buildTime;
		a.originPrice = originPrice;
		a.location = location;
		a.ownership = ownership;
		a.keeper = keeper;

		a.businessMode = businessMode;
		a.businessTime = businessTime;
		a.holder = holder;
		a.yearlyIncome = yearlyIncome;
		a.specType = specType;

		a.estateType = estateType;
		a.area = area;
		a.floorArea = floorArea;
		a.boundary = boundary;
		a.loctionStart = loctionStart;

		a.loctionEnd = loctionEnd;
		a.coordinateStart = coordinateStart;
		a.coordinateEnd = coordinateEnd;
		a.accumulateStock = accumulateStock;
		a.treeNumber = treeNumber;

		a.imgs = imgs;
		a.remark = remark;

		assetRepository.insert(conn, a);

		return a;
	}

	public int editAsset(DruidPooledConnection conn, Long assetId, //
			String originId, String name, String sn, String resType, String assetType, //
			Date buildTime, Double originPrice, String location, String ownership, String keeper, //
			String businessMode, String businessTime, String holder, Double yearlyIncome, String specType, //
			String estateType, Double area, Double floorArea, String boundary, String loctionStart, //
			String loctionEnd, String coordinateStart, String coordinateEnd, Double accumulateStock, Integer treeNumber, //
			String imgs, String remark) throws Exception {
		Asset a = new Asset();

		a.originId = originId;
		a.name = name;
		a.sn = sn;
		a.resType = resType;
		a.assetType = assetType;

		a.buildTime = buildTime;
		a.originPrice = originPrice;
		a.location = location;
		a.ownership = ownership;
		a.keeper = keeper;

		a.businessMode = businessMode;
		a.businessTime = businessTime;
		a.holder = holder;
		a.yearlyIncome = yearlyIncome;
		a.specType = specType;

		a.estateType = estateType;
		a.area = area;
		a.floorArea = floorArea;
		a.boundary = boundary;
		a.loctionStart = loctionStart;

		a.loctionEnd = loctionEnd;
		a.coordinateStart = coordinateStart;
		a.coordinateEnd = coordinateEnd;
		a.accumulateStock = accumulateStock;
		a.treeNumber = treeNumber;

		a.imgs = imgs;
		a.remark = remark;

		return assetRepository.updateByKey(conn, "id", assetId, a, true);
	}

	public int delAsset(DruidPooledConnection conn, Long assetId) throws Exception {
		return assetRepository.deleteByKey(conn, "id", assetId);
	}

	public List<Asset> getAssets(DruidPooledConnection conn, Long orgId, Integer count, Integer offset)
			throws Exception {
		return assetRepository.getListByKey(conn, "org_id", orgId, count, offset);
	}

	public void importAsset(DruidPooledConnection conn, Long orgId, String url) throws Exception {

	}

	public void importAssets(DruidPooledConnection conn, Long orgId, String url) throws Exception {

	}
}
