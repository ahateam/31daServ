package zero;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import zyxhj.economy.service.AssetService;
import zyxhj.utils.ExcelUtils;
import zyxhj.utils.data.DataSourceUtils;

public class EconomyAssetImporter {

	private static DruidPooledConnection conn;

	private static AssetService assetService;

	static {
		DataSourceUtils.initDataSourceConfig();

		assetService = AssetService.getInstance();

		try {
			conn = (DruidPooledConnection) DataSourceUtils.getDataSource("rdsDefault").openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		// List<List<Object>> table = ExcelUtils.readExcelFile("固定资产表.xlsx", 2, 38, 0);

		String url = "https://jitijingji-test1.oss-cn-hangzhou.aliyuncs.com/test/%E5%9B%BA%E5%AE%9A%E8%B5%84%E4%BA%A7%E8%A1%A8.xlsx";
		List<List<Object>> table = ExcelUtils.readExcelOnline(url, 2, 38, 0);

		if (true) {
			return;
		}

		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// 同时支持Excel 2003、2007
			File excelFile = new File("固定资产表.xlsx"); // 创建文件对象
			FileInputStream in = new FileInputStream(excelFile); // 文件流
			EconomyUserImporter.checkExcelVaild(excelFile);
			Workbook workbook = EconomyUserImporter.getWorkbok(in, excelFile);
			// Workbook workbook = WorkbookFactory.create(is); // 这种方式
			// Excel2003/2007/2010都是可以处理的

			int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
			/**
			 * 设置当前excel中sheet的下标：0开始
			 */
			Sheet sheet = workbook.getSheetAt(0); // 遍历第一个Sheet
			// Sheet sheet = workbook.getSheetAt(2); // 遍历第三个Sheet

			// 获取总行数
			// System.out.println(sheet.getLastRowNum());

			// 为跳过第一行目录设置count
			int count = 0;

			int columnTotalNum = 38;// 列数

			for (Row row : sheet) {
				try {
					// 跳过表头，前两行
					if (count < 2) {
						count++;
						continue;
					}
					// 如果当前行没有数据，跳出循环
					if (StringUtils.isBlank(row.getCell(0).toString())) {
						return;
					}

					ArrayList<Object> array = new ArrayList<>();
					for (int i = 0; i < columnTotalNum; i++) {
						Cell cell = row.getCell(i);
						if (cell == null) {
							System.out.print("null" + "\t");
							array.add(null);
						} else {
							Object obj = EconomyUserImporter.getValue(cell);
							array.add(obj);
							System.out.print(obj + "\t");
						}
					}

					System.out.println();

					importRow(array);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

	private static void importRow(ArrayList<Object> row) {

		Long orgId = Long.parseLong("395239429596298");

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
			assetService.createAsset(conn, orgId, originId, name, sn, resType, //
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

	// public static List<List<Object>> read(String fileUrl) throws IOException {
	// List<List<Object>> allRows = new ArrayList<List<Object>>();
	// InputStream is = null;
	// Workbook wb = null;
	// try {
	// URL url = new URL(encodedUri);
	// URLConnection conn = url.openConnection();
	// conn.setConnectTimeout(3000);
	// conn.setReadTimeout(3 * 60 * 1000);
	// is = conn.getInputStream();
	//
	//
	// wb = WorkbookFactory.create(is);
	// Sheet sheet = wb.getSheetAt(0);
	// int maxRowNum = sheet.getLastRowNum();
	// int minRowNum = sheet.getFirstRowNum();
	//
	// // 跳过头，从第二行开始读取
	// for (int i = minRowNum + 1; i <= maxRowNum; i++) {
	// Row row = sheet.getRow(i);
	// if (row == null) {
	// continue;
	// }
	// List<Object> rowData = readLine(row);
	// allRows.add(rowData);
	// }
	//
	// } catch (Exception e) {
	// throw new IOException(e);
	// } finally {
	// if (is != null) {
	// is.close();
	// }
	// if (wb != null && wb instanceof SXSSFWorkbook) {
	// SXSSFWorkbook xssfwb = (SXSSFWorkbook) wb;
	// xssfwb.dispose();
	// }
	// }
	//
	// return allRows;
	// }

}
