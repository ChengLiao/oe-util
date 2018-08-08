package com.oe.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Cheng Liao
 * @date 2017-7-28 下午5:59:33
 */
public class HttpUtils {
	/**
	 * 不带参数的get
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String sendGet(String url) throws Exception {
		return sendGet(url, null);
	}

	/**
	 * 发送GET请求
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 */
	public static String sendGet(String url, Map<String, String> parameters) throws Exception {
		String result = "";
		BufferedReader in = null;// 读取响应输入流
		StringBuffer sb = new StringBuffer();// 存储参数
		String params = "";// 编码之后的参数
		try {
			// 编码请求参数
			if (parameters != null && !parameters.isEmpty()) {
				for (String name : parameters.keySet()) {
					sb.append(name)
							.append("=")
							.append(URLEncoder.encode(
									parameters.get(name), PropertiesConstants.ENCODING)).append("&");
				}
				String temp_params = sb.toString();
				params = temp_params.substring(0, temp_params.length() - 1);
				url = url + "?" + params;
			}
			// 创建URL对象
			URL connURL = new URL(url);
			// 打开URL连接
			HttpURLConnection httpConn = (HttpURLConnection) connURL
					.openConnection();
			// 设置通用属性
			httpConn.setRequestProperty("Accept", "*/*");
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
			// 建立实际的连接
			httpConn.connect();
			// 定义BufferedReader输入流来读取URL的响应,并设置编码方式
			in = new BufferedReader(new InputStreamReader(
					httpConn.getInputStream(), PropertiesConstants.ENCODING));
			String line;
			// 读取返回的内容
			while ((line = in.readLine()) != null) {
				result += line + PropertiesConstants.LINE_SEPARATOR;
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	/**
	 * 发送POST请求
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @param headers 额外的header
	 * @return 远程响应结果
	 */
	public static String sendPost(String url, Map<String, String> parameters,Map<String, String> headers) throws Exception {
		String result = "";// 返回的结果
		BufferedReader br = null;// 读取响应输入流
		String params = "";
		try {
			//转化json请求参数
			if (parameters!=null && !parameters.isEmpty()) {
				params = JsonUtil.toJson(parameters);;
			}
			HttpURLConnection conn = getHttpURLConnection(url, headers);
			
			DataOutputStream os = new DataOutputStream(conn.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,PropertiesConstants.ENCODING));
			writer.write(params);
			writer.flush();
			writer.close();
			
			br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(),PropertiesConstants.ENCODING));

			String output;
			StringBuilder sb = new StringBuilder();
			while ((output = br.readLine()) != null) {
				sb.append(PropertiesConstants.LINE_SEPARATOR);
				sb.append(output);
			}
			result = sb.toString();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 发送POST请求
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 */
	public static String sendPost(String url, Map<String, String> parameters) throws Exception {
		return sendPost(url, parameters, null);
	}
	
	/**
	 * @param spec
	 * @return
	 * @throws IOException
	 */
	public static HttpURLConnection getHttpURLConnection(String spec, Map<String, String> headers)
			throws IOException {
		HttpURLConnection conn = null;
		int timeout = PropertiesConstants.POST_TIMEOUT * 1000;
		try {
			URL url = new URL(spec);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(timeout);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			//设置额外的请求header
			if(headers != null){
				for (Entry<String, String> e : headers.entrySet()) {
					conn.setRequestProperty(e.getKey(), e.getValue());
				}
			}
		} catch (IOException e) {
			String msg = String.format(
					"Failed to connect sms API[%s] in [%d] seconds: %s", spec,
					timeout / 1000, e);
			throw new IOException(msg, e);
		}

		return conn;
	}
}
