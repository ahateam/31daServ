package zyxhj.utils.data;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import zyxhj.utils.CodecUtils;
import zyxhj.utils.Input;
import zyxhj.utils.data.rds.RDSDataSource;

/**
 */
public class DataSourceUtils {

	private static Logger log = LoggerFactory.getLogger(DataSourceUtils.class);

	public static final String TYPE_OTS = "ots";
	public static final String TYPE_JDBC = "jdbc";
	public static final String TYPE_OPEN_SEARCH = "opensearch";

	public static class ConfigItem {
		public String name;
		public String type;
		public String filename;
	}

	private static Map<String, DataSource> dsMap = new HashMap<>();
	private static Map<String, Properties> configMap = new HashMap<String, Properties>();

	/**
	 * 
	 * @param webappFolder
	 *            webapp的目录
	 */
	public static void initDataSourceConfig() {
		FileInputStream fis;
		List<ConfigItem> array = null;
		try {
			fis = new FileInputStream("configs/ds.cfg");
			log.info("&&&&&initDataSourceConfig>>>{}", "configs/ds.cfg");

			Input in = new Input(fis);
			String str = in.readAllToString(CodecUtils.CHARSET_UTF8);
			in.close();

			array = JSON.parseArray(str, ConfigItem.class);

			for (ConfigItem ci : array) {
				try {
					Properties p = new Properties();
					String ciname = StringUtils.join("configs/", ci.filename);
					ciname = fixSeparator(ciname);
					log.info(">>>ciname>>{}", ciname);
					p.load(new FileInputStream(ciname));

					// if (ci.type.equalsIgnoreCase(TYPE_OTS)) {
					// OTSDataSource ds = new OTSDataSource(p);
					// dsMap.put(ci.name, ds);
					// } else
					if (ci.type.equalsIgnoreCase(TYPE_JDBC)) {
						RDSDataSource ds = new RDSDataSource(p);
						dsMap.put(ci.name, ds);
					}
					// else if (ci.type.equalsIgnoreCase(TYPE_OPEN_SEARCH)) {
					// OpenSearchDataSource ds = new OpenSearchDataSource(p);
					// dsMap.put(ci.name, ds);
					// if(!p.isEmpty()){
					// configMap.put(TYPE_OPEN_SEARCH, p);
					// }
					// }
					else {
						log.error(StringUtils.join("DataSource type undefined:", ci.type));
					}
					log.info(StringUtils.join("DataSource loaded : ", ci.name));
				} catch (Exception e) {
					log.error("DataSource load error.");
					log.error(e.getMessage(), e);
				}
			}

		} catch (Exception e) {
			log.error("initDataSourceConfig failure");
			log.error(e.getMessage(), e);
		}
	}

	// 修复fileName，兼容linux和windows
	private static String fixSeparator(String str) {
		// windows是\，而linux是/
		String spr = System.getProperty("file.separator");
		if (spr.equals("/")) {
			return StringUtils.replaceChars(str, '\\', '/');
		} else {
			return StringUtils.replaceChars(str, '/', '\\');
		}
	}

	public static DataSource getDataSource(String dataSourceName) throws Exception {
		DataSource ds = dsMap.get(dataSourceName);
		if (null != ds) {
			return ds;
		} else {
			throw new Exception(StringUtils.join("DataSource [", dataSourceName, "] not exist"));
		}
	}

}
