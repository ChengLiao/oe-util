package com.oe.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author Cheng Liao
 * @date 2017-7-28 下午5:59:33
 */
public class HttpUtils {
	
	//15个ua随机用，减少503的机率
    public static String [] ua = {
            "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
            "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68",
            "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)",
            "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:7.0.1) Gecko/20100101 Firefox/7.0.1",
            "Opera/9.80 (Macintosh; Intel Mac OS X 10.9.1) Presto/2.12.388 Version/12.16",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36 OPR/18.0.1284.68",
            "Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) CriOS/30.0.1599.12 Mobile/11A465 Safari/8536.25",
            "Mozilla/5.0 (iPad; CPU OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4",
            "Mozilla/5.0 (iPad; CPU OS 7_0_2 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A501 Safari/9537.53",
    };
	
	
	
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
	 * 不带参数的get
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String sendGet(String url, Map<String, String> parameters) throws Exception {
		return sendGet(url, parameters, null);
	}

	/**
	 * 发送GET请求
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 */
	public static byte[] sendGet4Byte(String url, Map<String, String> parameters, Map<String, String> headers) throws Exception {
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
				if(url.indexOf("?")==-10) {
					url = url + "?";
				}else {
					url = url + "&";
				}
				url = url + params;
			}
			// 创建URL对象
			URL connURL = new URL(url);
			// 打开URL连接
			HttpURLConnection httpConn = (HttpURLConnection) connURL
					.openConnection();
			// 设置通用属性
			httpConn.setRequestProperty("Accept", "*/*");
			
			
			if(headers != null){
				for (Entry<String, String> e : headers.entrySet()) {
					httpConn.setRequestProperty(e.getKey(), e.getValue());
				}
			}
			
//			httpConn.setRequestProperty("User-Agent", ua[Math.abs(new Random().nextInt()%15)]);
			
			// 建立实际的连接
			httpConn.connect();
			
			return getBytesFromStream(httpConn.getInputStream());
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送GET请求
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 */
	public static Map<String, List<String>> sendGet4Header(String url, Map<String, String> parameters, Map<String, String> headers) throws Exception {
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
				if(url.indexOf("?")==-10) {
					url = url + "?";
				}else {
					url = url + "&";
				}
				url = url + params;
			}
			// 创建URL对象
			URL connURL = new URL(url);
			// 打开URL连接
			HttpURLConnection httpConn = (HttpURLConnection) connURL
					.openConnection();
			// 设置通用属性
			httpConn.setRequestProperty("Accept", "*/*");
			
			if(headers != null){
				for (Entry<String, String> e : headers.entrySet()) {
					httpConn.setRequestProperty(e.getKey(), e.getValue());
				}
			}
			// 建立实际的连接
			httpConn.connect();
			
			return httpConn.getHeaderFields();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送GET请求
	 * @param url
	 *            目的地址
	 * @param parameters
	 *            请求参数，Map类型。
	 * @return 远程响应结果
	 */
	public static String sendGet(String url, Map<String, String> parameters, Map<String, String> headers) throws Exception {
		return new String(sendGet4Byte(url, parameters, headers), PropertiesConstants.ENCODING);
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
	 * @param headers 额外的header
	 * @return 远程响应结果
	 */
	public static String sendPost(String url, String params,Map<String, String> headers) throws Exception {
		String result = "";// 返回的结果
		BufferedReader br = null;// 读取响应输入流
		try {
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
	 * @param headers 额外的header
	 * @return 远程响应cookie
	 */
	public static Map<String, List<String>> sendPost4Header(String url, String params,Map<String, String> headers) throws Exception {
		HttpURLConnection conn = getHttpURLConnection(url, headers);
		
		DataOutputStream os = new DataOutputStream(conn.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,PropertiesConstants.ENCODING));
		writer.write(params);
		writer.flush();
		writer.close();
		
		return conn.getHeaderFields();
			/*
			br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(),PropertiesConstants.ENCODING));

			String output;
			StringBuilder sb = new StringBuilder();
			while ((output = br.readLine()) != null) {
				sb.append(PropertiesConstants.LINE_SEPARATOR);
				sb.append(output);
			}
			result = sb.toString();*/
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
//			conn.setRequestProperty("User-Agent", ua[Math.abs(new Random().nextInt()%15)]);
		} catch (IOException e) {
			String msg = String.format(
					"Failed to connect sms API[%s] in [%d] seconds: %s", spec,
					timeout / 1000, e);
			throw new IOException(msg, e);
		}

		return conn;
	}
	private static byte[] getBytesFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] kb = new byte[1024];
        int len;
        while ((len = is.read(kb)) != -1) {
            baos.write(kb, 0, len);
        }
        byte[] bytes = baos.toByteArray();
        baos.close();
        is.close();
        return bytes;
    }

}
