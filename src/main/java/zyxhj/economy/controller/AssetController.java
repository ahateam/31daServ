package zyxhj.economy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.service.AssetService;
import zyxhj.utils.api.APIResponse;
import zyxhj.utils.api.Controller;
import zyxhj.utils.data.DataSource;
import zyxhj.utils.data.DataSourceUtils;

public class AssetController extends Controller {

	private static Logger log = LoggerFactory.getLogger(AssetController.class);

	private static AssetController ins;

	public static synchronized AssetController getInstance(String node) {
		if (null == ins) {
			ins = new AssetController(node);
		}
		return ins;
	}

	private DataSource dsRds;
	private AssetService assetService;

	private AssetController(String node) {
		super(node);
		try {
			dsRds = DataSourceUtils.getDataSource("rdsDefault");

			assetService = AssetService.getInstance();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "createAsset", //
			des = "创建资产", //
			ret = "所创建的对象"//
	)
	public APIResponse createAsset(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "资产原始编号") String originId, //
			@P(t = "资产原始名称") String name, //
			@P(t = "资产证件号", r = false) String sn, //
			@P(t = "资源类型") String resType, //
			@P(t = "资产类型（动产，不动产）") String assetType, //

			@P(t = "构建时间") String buildTime, //
			@P(t = "原始价格（万元）") Double originPrice, //
			@P(t = "坐落或置放位置") String location, //
			@P(t = "权属") String ownership, //
			@P(t = "保管人", r = false) String keeper, //

			@P(t = "（经营属性）经营方式") String businessMode, //
			@P(t = "（经营属性）经营起止时间", r = false) String businessTime, //
			@P(t = "（经营属性）承租方或投资对象", r = false) String holder, //
			@P(t = "（经营属性）年收益，万元", r = false) Double yearlyIncome, //
			@P(t = "（动产属性）规格型号", r = false) String specType, //

			@P(t = "（不动产属性）不动产类型", r = false) String estateType, //
			@P(t = "（不动产属性）建筑面积，平方米", r = false) Double area, //
			@P(t = "（不动产属性）占地面积，平方米", r = false) Double floorArea, //
			@P(t = "（不动产属性）四至边界，JSONObject{east:东,west:西,south:南,north:北}", r = false) String boundary, //
			@P(t = "（不动产属性）起点位置", r = false) String locationStart, //

			@P(t = "（不动产属性）终点位置", r = false) String locationEnd, //
			@P(t = "（不动产属性）起点坐标", r = false) String coordinateStart, //
			@P(t = "（不动产属性）终点坐标", r = false) String coordinateEnd, //
			@P(t = "（不动产属性）蓄积，立方米", r = false) Double accumulateStock, //
			@P(t = "（不动产属性）棵", r = false) Integer treeNumber, //

			@P(t = "图片地址列表（JSONObject\n\\n" + //
					"			 * imgExt1 附属图片1（基础属性）\n" + //
					"			 * imgExt2 附属图片2（基础属性）\n" + //
					"			 * imgStart 起点图片（不动产属性）\n" + //
					"			 * imgEnd 终点图片（不动产属性）\n" + //
					"			 * imgFar 远景图片（不动产属性）\n" + //
					"			 * imgNear 近景图片（不动产属性）\n" + //
					"			 * imgFront 正面图片（不动产属性）\n" + //
					"			 * imgSide 侧面图片（不动产属性）\n" + //
					"			 * imgBack 背面图片（不动产属性）", r = false) String imgs, //
			@P(t = "备注", r = false) String remark //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(assetService.createAsset(conn, orgId, originId, name, sn, resType,
					assetType, buildTime, originPrice, location, ownership, keeper, businessMode, businessTime, holder,
					yearlyIncome, specType, estateType, area, floorArea, boundary, locationStart, locationEnd,
					coordinateStart, coordinateEnd, accumulateStock, treeNumber, imgs, remark));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "editAsset", //
			des = "编辑资产", //
			ret = "所影响记录行数"//
	)
	public APIResponse editAsset(//
			@P(t = "资产编号") Long assetId, //
			@P(t = "资产原始编号", r = false) String originId, //
			@P(t = "资产原始名称", r = false) String name, //
			@P(t = "资产证件号", r = false) String sn, //
			@P(t = "资源类型", r = false) String resType, //
			@P(t = "资产类型（动产，不动产）", r = false) String assetType, //

			@P(t = "构建时间", r = false) String buildTime, //
			@P(t = "原始价格（万元）", r = false) Double originPrice, //
			@P(t = "坐落或置放位置", r = false) String location, //
			@P(t = "权属", r = false) String ownership, //
			@P(t = "保管人", r = false) String keeper, //

			@P(t = "（经营属性）经营方式", r = false) String businessMode, //
			@P(t = "（经营属性）经营起止时间", r = false) String businessTime, //
			@P(t = "（经营属性）承租方或投资对象", r = false) String holder, //
			@P(t = "（经营属性）年收益，万元", r = false) Double yearlyIncome, //
			@P(t = "（动产属性）规格型号", r = false) String specType, //

			@P(t = "（不动产属性）不动产类型", r = false) String estateType, //
			@P(t = "（不动产属性）建筑面积，平方米", r = false) Double area, //
			@P(t = "（不动产属性）占地面积，平方米", r = false) Double floorArea, //
			@P(t = "（不动产属性）四至边界，JSONObject{east:东,west:西,south:南,north:北}", r = false) String boundary, //
			@P(t = "（不动产属性）起点位置", r = false) String locationStart, //

			@P(t = "（不动产属性）终点位置", r = false) String locationEnd, //
			@P(t = "（不动产属性）起点坐标", r = false) String coordinateStart, //
			@P(t = "（不动产属性）终点坐标", r = false) String coordinateEnd, //
			@P(t = "（不动产属性）蓄积，立方米", r = false) Double accumulateStock, //
			@P(t = "（不动产属性）棵", r = false) Integer treeNumber, //

			@P(t = "图片地址列表（JSONObject\n\\n" + //
					"			 * imgExt1 附属图片1（基础属性）\n" + //
					"			 * imgExt2 附属图片2（基础属性）\n" + //
					"			 * imgStart 起点图片（不动产属性）\n" + //
					"			 * imgEnd 终点图片（不动产属性）\n" + //
					"			 * imgFar 远景图片（不动产属性）\n" + //
					"			 * imgNear 近景图片（不动产属性）\n" + //
					"			 * imgFront 正面图片（不动产属性）\n" + //
					"			 * imgSide 侧面图片（不动产属性）\n" + //
					"			 * imgBack 背面图片（不动产属性）", r = false) String imgs, //
			@P(t = "备注", r = false) String remark //
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(assetService.editAsset(conn, assetId, originId, name, sn, resType,
					assetType, buildTime, originPrice, location, ownership, keeper, businessMode, businessTime, holder,
					yearlyIncome, specType, estateType, area, floorArea, boundary, locationStart, locationEnd,
					coordinateStart, coordinateEnd, accumulateStock, treeNumber, imgs, remark));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "delAsset", //
			des = "删除资产", //
			ret = "所影响记录行数"//
	)
	public APIResponse delAsset(//
			@P(t = "资产编号") Long assetId) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(assetService.delAsset(conn, assetId));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "getAssets", //
			des = "获取资产列表", //
			ret = "资产列表"//
	)
	public APIResponse getAssets(//
			@P(t = "组织编号") Long orgId, //
			Integer count, //
			Integer offset//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			return APIResponse.getNewSuccessResp(assetService.getAssets(conn, orgId, count, offset));
		}
	}

	/**
	 * 
	 */
	@POSTAPI(//
			path = "importAssets", //
			des = "导入资产列表" //
	)
	public APIResponse importAssets(//
			@P(t = "组织编号") Long orgId, //
			@P(t = "excel文件url") String url//
	) throws Exception {
		try (DruidPooledConnection conn = (DruidPooledConnection) dsRds.openConnection()) {
			assetService.importAssets(conn, orgId, url);
			return APIResponse.getNewSuccessResp();
		}
	}
}
