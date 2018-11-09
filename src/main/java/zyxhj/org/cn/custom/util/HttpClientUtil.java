package zyxhj.org.cn.custom.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class HttpClientUtil {

	/**
	 * @描述 get方式获取外部接口返回json串
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String get(String url) throws IOException {
		String returnVal = "";
		// 定义httpClient的实例
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();// 设置请求和传输超时时间
		httpGet.setConfig(requestConfig);
		try {
			CloseableHttpResponse response2 = httpclient.execute(httpGet);// 执行请求
			// log.info();
			HttpEntity entity2 = (HttpEntity) response2.getEntity();
			if (entity2 != null) {

				returnVal = EntityUtils.toString(entity2, "UTF-8");

			} else {
				returnVal = null;
			}

		} catch (ClientProtocolException e) {
			// log.info();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// log.info();

		} finally {

			if (httpclient != null) {

				httpclient.close();
			}

		}

		return returnVal;

	}

	/**
	 * @描述 json串转换map
	 * @param bizData
	 * @return
	 */
	public static Map<String, Object> parseJSON2Map(String bizData) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			JSONObject bizDataJson = JSONObject.parseObject(bizData);
			// 获取json对象值
			for (Object key : bizDataJson.keySet()) {
				Object value = bizDataJson.get(key);
				// 判断值是否为json数组类型
				if (value instanceof JSONArray) {
					// 如果为json数组类型迭代循环取值
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					Iterator<Object> it = ((JSONArray) value).iterator();

					while (it.hasNext()) {
						JSONObject json2 = (JSONObject) it.next();
						list.add(parseJSON2Map(json2.toString()));
					}
					ret.put(String.valueOf(key), list);
				} else {
					ret.put(String.valueOf(key), String.valueOf(value));
				}
			}
		} catch (Exception e) {
			// log.info();
		}
		return ret;
	}

	/**
	 * post请求（用于请求json格式的参数）
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, String params) throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);// 创建httpPost
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		String charSet = "UTF-8";
		StringEntity entity = new StringEntity(params, charSet);
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			StatusLine status = response.getStatusLine();
			int state = status.getStatusCode();
			if (state == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				String jsonString = EntityUtils.toString(responseEntity);
				return jsonString;
			} else {
				// log.error("请求返回:"+state+"("+url+")");
			}
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
