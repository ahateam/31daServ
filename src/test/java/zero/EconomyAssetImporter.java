package zero;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import zyxhj.economy.domain.ORGRole;

public class EconomyAssetImporter {

	public static void main(String[] args) throws Exception {
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

			int columnTotalNum = 8;// 列数

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
							continue;
						}

						Object obj = EconomyUserImporter.getValue(cell);
						array.add(obj);
						System.out.print(obj + "\t");
					}

					System.out.println();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
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
