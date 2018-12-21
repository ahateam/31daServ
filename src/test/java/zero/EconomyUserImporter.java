package zero;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.druid.pool.DruidPooledConnection;

import zyxhj.economy.domain.ORGRole;
import zyxhj.economy.service.ORGService;
import zyxhj.utils.data.DataSourceUtils;

public class EconomyUserImporter {

	private static final String EXCEL_XLS = "xls";
	private static final String EXCEL_XLSX = "xlsx";

	private static DruidPooledConnection conn;

	private static ORGService orgService;

	static {
		DataSourceUtils.initDataSourceConfig();
		// contentService = ContentService.getInstance();

		orgService = ORGService.getInstance();

		try {
			conn = (DruidPooledConnection) DataSourceUtils.getDataSource("rdsDefault").openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 判断Excel的版本,获取Workbook
	 * 
	 * @param in
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Workbook getWorkbok(InputStream in, File file) throws IOException {
		Workbook wb = null;
		if (file.getName().endsWith(EXCEL_XLS)) { // Excel 2003
			wb = new HSSFWorkbook(in);
		} else if (file.getName().endsWith(EXCEL_XLSX)) { // Excel 2007/2010
			wb = new XSSFWorkbook(in);
		}
		return wb;
	}

	/**
	 * 判断文件是否是excel
	 * 
	 * @throws Exception
	 */
	public static void checkExcelVaild(File file) throws Exception {
		if (!file.exists()) {
			throw new Exception("文件不存在");
		}
		if (!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))) {
			throw new Exception("文件不是Excel");
		}
	}

	public static void main(String[] args) throws Exception {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// 同时支持Excel 2003、2007
			File excelFile = new File("松林村股份经济合作社用户信息表模板.xlsx"); // 创建文件对象
			FileInputStream in = new FileInputStream(excelFile); // 文件流
			checkExcelVaild(excelFile);
			Workbook workbook = getWorkbok(in, excelFile);
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
					// 跳过第一行的表头
					if (count < 1) {
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

						Object obj = getValue(cell);
						array.add(obj);
						System.out.print(obj + "\t");
					}

					Byte share = ORGRole.SHARE.NONE.v();
					Object objShare = array.get(3);
					if (objShare.equals("股东户代表")) {
						share = ORGRole.SHARE.REPRESENTATIVE.v();
					} else if (objShare.equals("股东")) {
						share = ORGRole.SHARE.SHAREHOLDER.v();
					} else {
						share = ORGRole.SHARE.NONE.v();
					}

					Byte duty = ORGRole.DUTY.NONE.v();
					Object objDuty = array.get(6);
					if (objDuty.equals("董事长")) {
						duty = ORGRole.DUTY.CHAIRMAN.v();
					} else if (objDuty.equals("副董事长")) {
						duty = ORGRole.DUTY.VICE_CHAIRMAN.v();
					} else if (objDuty.equals("董事")) {
						duty = ORGRole.DUTY.DIRECTOR.v();
					} else {
						duty = ORGRole.DUTY.NONE.v();
					}

					Byte visor = ORGRole.VISOR.NONE.v();
					Object objVisor = array.get(7);
					if (objVisor.equals("监事长")) {
						visor = ORGRole.VISOR.CHAIRMAN.v();
					} else if (objVisor.equals("副监事长")) {
						visor = ORGRole.VISOR.VICE_CHAIRMAN.v();
					} else if (objVisor.equals("监事")) {
						visor = ORGRole.VISOR.SUPERVISOR.v();
					} else {
						visor = ORGRole.DUTY.NONE.v();
					}

					orgService.importUser(conn, Long.parseLong("395239429596298"), array.get(2).toString(),
							array.get(0).toString(), array.get(1).toString(), share,
							Integer.parseInt(array.get(4).toString()), Integer.parseInt(array.get(5).toString()), duty,
							visor);

					System.out.println();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getValue(Cell cell) {
		Object obj = null;
		switch (cell.getCellTypeEnum()) {
		case BOOLEAN:
			obj = cell.getBooleanCellValue();
			break;
		case ERROR:
			obj = cell.getErrorCellValue();
			break;
		case NUMERIC:
			// obj = cell.getNumericCellValue();
			DecimalFormat df = new DecimalFormat("0");
			obj = df.format(cell.getNumericCellValue());
			break;
		case STRING:
			obj = cell.getStringCellValue();
			break;
		default:
			break;
		}
		return obj;
	}

}
