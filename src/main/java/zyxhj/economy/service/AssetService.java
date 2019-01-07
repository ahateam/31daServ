package zyxhj.economy.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyxhj.economy.domain.Asset;
import zyxhj.economy.repository.AssetRepository;
import zyxhj.utils.ExcelUtils;
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
			String buildTime, Double originPrice, String location, String ownership, String keeper, //
			String businessMode, String businessTime, String holder, Double yearlyIncome, String specType, //
			String estateType, Double area, Double floorArea, String boundary, String locationStart, //
			String locationEnd, String coordinateStart, String coordinateEnd, Double accumulateStock,
			Integer treeNumber, //
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
		a.locationStart = locationStart;

		a.locationEnd = locationEnd;
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
			String buildTime, Double originPrice, String location, String ownership, String keeper, //
			String businessMode, String businessTime, String holder, Double yearlyIncome, String specType, //
			String estateType, Double area, Double floorArea, String boundary, String locationStart, //
			String locationEnd, String coordinateStart, String coordinateEnd, Double accumulateStock,
			Integer treeNumber, //
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
		a.locationStart = locationStart;

		a.locationEnd = locationEnd;
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

	public List<Asset> searchAssets(DruidPooledConnection conn, Long orgId, String assetType, Integer count,
			Integer offset) throws Exception {
		return assetRepository.searchAssets(conn, orgId, assetType, count, offset);
	}

	public void importAssets(DruidPooledConnection conn, Long orgId, String url) throws Exception {

		// 2行表头，38列，文件格式写死的
		List<List<Object>> table = ExcelUtils.readExcelOnline(url, 2, 38, 0);

		for (List<Object> row : table) {
			String originId = ExcelUtils.getString(row.get(0));
			String name = ExcelUtils.getString(row.get(1));
			String sn = ExcelUtils.getString(row.get(2));
			String resType = ExcelUtils.getString(row.get(3));
			String assetType = ExcelUtils.getString(row.get(4));

			String buildTime = ExcelUtils.getString(row.get(5));
			Double originPrice = ExcelUtils.parseDouble(row.get(6));
			String location = ExcelUtils.getString(row.get(7));
			String ownership = ExcelUtils.getString(row.get(8));
			String keeper = ExcelUtils.getString(row.get(9));

			String imgExt1 = ExcelUtils.getString(row.get(10));
			String imgExt2 = ExcelUtils.getString(row.get(11));

			String businessMode = ExcelUtils.getString(row.get(12));
			String businessTime = ExcelUtils.getString(row.get(13));
			String holder = ExcelUtils.getString(row.get(14));
			Double yearlyIncome = ExcelUtils.parseDouble(row.get(15));
			String specType = ExcelUtils.getString(row.get(16));

			String estateType = ExcelUtils.getString(row.get(17));
			Double area = ExcelUtils.parseDouble(row.get(18));
			Double floorArea = ExcelUtils.parseDouble(row.get(19));

			JSONObject b = new JSONObject();
			b.put("east", ExcelUtils.getString(row.get(20)));
			b.put("west", ExcelUtils.getString(row.get(21)));
			b.put("south", ExcelUtils.getString(row.get(22)));
			b.put("north", ExcelUtils.getString(row.get(23)));
			String boundary = JSON.toJSONString(b);

			String locationStart = ExcelUtils.getString(row.get(24));
			String locationEnd = ExcelUtils.getString(row.get(25));
			String coordinateStart = ExcelUtils.getString(row.get(26));
			String coordinateEnd = ExcelUtils.getString(row.get(27));

			String imgStart = ExcelUtils.getString(row.get(28));
			String imgEnd = ExcelUtils.getString(row.get(29));

			Double accumulateStock = ExcelUtils.parseDouble(row.get(30));
			Integer treeNumber = ExcelUtils.parseInt(row.get(31));

			String imgFar = ExcelUtils.getString(row.get(32));
			String imgNear = ExcelUtils.getString(row.get(33));
			String imgFront = ExcelUtils.getString(row.get(34));
			String imgSide = ExcelUtils.getString(row.get(35));
			String imgBack = ExcelUtils.getString(row.get(36));
			JSONObject img = new JSONObject();
			img.put("imgExt1", imgExt1);
			img.put("imgExt2", imgExt2);

			img.put("imgStart", imgStart);
			img.put("imgEnd", imgEnd);

			img.put("imgFar", imgFar);
			img.put("imgNear", imgNear);
			img.put("imgFront", imgFront);
			img.put("imgSide", imgSide);
			img.put("imgBack", imgBack);

			String imgs = JSON.toJSONString(img);
			String remark = ExcelUtils.getString(row.get(37));

			try {
				createAsset(conn, orgId, originId, name, sn, resType, //
						assetType, buildTime, originPrice, location, ownership, //
						keeper, businessMode, businessTime, holder, yearlyIncome, //
						specType, estateType, area, floorArea, boundary, //
						locationStart, locationEnd, coordinateStart, coordinateEnd, accumulateStock, //
						treeNumber, imgs, remark);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
