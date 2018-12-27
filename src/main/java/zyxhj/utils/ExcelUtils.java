package zyxhj.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import zero.EconomyUserImporter;

public class ExcelUtils {

	private static final String EXCEL_XLS = "xls";
	private static final String EXCEL_XLSX = "xlsx";

	private static List<List<Object>> readxxx(Workbook workbook, int skipRowCount, int colCount, int sheetIndex) {
		// 目前只读取第一个sheet
		Sheet sheet = workbook.getSheetAt(sheetIndex); // 遍历第几个Sheet
		int count = 0;

		List<List<Object>> ret = new ArrayList<>();

		for (Row row : sheet) {
			try {
				// 跳过表头，前两行
				if (count < skipRowCount) {
					count++;
					continue;
				}
				// 如果当前行没有数据，跳出循环
				if (StringUtils.isBlank(row.getCell(0).toString())) {
					break;
				}

				ArrayList<Object> objs = new ArrayList<>();
				for (int i = 0; i < colCount; i++) {
					Cell cell = row.getCell(i);
					if (cell == null) {
						System.out.print("null" + "\t");
						objs.add(null);
					} else {
						Object obj = EconomyUserImporter.getValue(cell);
						objs.add(obj);
						System.out.print(obj + "\t");
					}
				}

				System.out.println();

				ret.add(objs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static List<List<Object>> readExcelOnline(String excelUrl, int skipRowCount, int colCount, int sheetIndex)
			throws Exception {

		URL url = new URL(excelUrl);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(3000);
		conn.setReadTimeout(3 * 60 * 1000);
		InputStream is = conn.getInputStream();

		Workbook workbook = ExcelUtils.getWorkbook(is, excelUrl);

		is.close();

		return readxxx(workbook, skipRowCount, colCount, sheetIndex);
	}

	public static List<List<Object>> readExcelFile(String fileName, int skipRowCount, int colCount, int sheetIndex)
			throws Exception {
		File excelFile = new File(fileName);
		ExcelUtils.checkExcel(excelFile);
		FileInputStream in = new FileInputStream(excelFile); // 文件流
		Workbook workbook = ExcelUtils.getWorkbook(in, excelFile);
		in.close();

		return readxxx(workbook, skipRowCount, colCount, sheetIndex);
	}

	private static void checkExcel(File file) throws Exception {
		if (!file.exists()) {
			throw new Exception("文件不存在");
		}
		if (!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))) {
			throw new Exception("文件不是Excel");
		}
	}

	private static Workbook getWorkbook(InputStream in, File file) throws IOException {
		Workbook wb = null;
		if (file.getName().endsWith(EXCEL_XLS)) { // Excel 2003
			wb = new HSSFWorkbook(in);
		} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
			wb = new XSSFWorkbook(in);
		}
		return wb;
	}

	private static Workbook getWorkbook(InputStream in, String url) throws IOException {
		Workbook wb = null;
		if (url.endsWith(EXCEL_XLS)) { // Excel 2003
			wb = new HSSFWorkbook(in);
		} else if (url.endsWith(EXCEL_XLSX)) { // Excel 2007/2010
			wb = new XSSFWorkbook(in);
		}
		return wb;
	}

	public static Double parseDouble(Object o) {
		if (o == null) {
			return null;
		} else {
			String str = o.toString();
			if (StringUtils.isBlank(str)) {
				return null;
			} else {
				try {
					return Double.parseDouble(str);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	public static Integer parseInt(Object o) {
		if (o == null) {
			return null;
		} else {
			String str = o.toString();
			if (StringUtils.isBlank(str)) {
				return null;
			} else {
				try {
					return Integer.parseInt(str);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
	}

	public static String getString(Object o) {
		if (o == null) {
			return null;
		} else {
			return o.toString();
		}
	}

}
