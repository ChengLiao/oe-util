package com.oe.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;

/**
 * @author liaocheng
 * @date 2016-6-6
 */
public class Util {
	
	public final static int WRAP_LENGTH = 1024;
	public final static String BR = "\r\n";
	
	/**
	 * 读取字符串，按默认行长度拆分成多条记录
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static List<String> wrap(String str) throws IOException {
		List<String> wraps = Collections.emptyList();
		if (str != null && !str.isEmpty()) {
			String wrapedString = WordUtils.wrap(str, WRAP_LENGTH);
			StringReader reader = new StringReader(wrapedString);
			try {
				wraps = IOUtils.readLines(reader);
			} finally {
				reader.close();
			}
		}
		return wraps;
	}
	

	/**
	 * 读取CLOB对象，返回
	 * @param input
	 * @return
	 * @throws SQLException 
	 * @throws IOException 
	 * @throws Exception
	 */
	public static String readClob(Clob clob) throws SQLException, IOException{
		if(clob == null){
			return "";
		}
		BufferedReader reader = new BufferedReader(clob.getCharacterStream());
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = reader.readLine())!=null){
			sb.append(line).append(BR);
		}
		reader.close();
		return sb.toString();
	}
	
	

	/**
	 * 生成len长度的随机数字字符串
	 * @param len
	 */
	public static String generateRnd(int len){
		StringBuffer sb = new StringBuffer(len);
		for(int i=0; i<len ; i++){
			sb.append((int)(Math.random()*10));
		}
		return sb.toString();
	}

	
	/**
	 * 计算耗时
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String calculateTimeElapsed(long start, long end) {
		long duration = Math.abs(end - start);
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long millis = TimeUnit.MILLISECONDS.toMillis(duration);

		return String.format(
				"Elapsed Time: %d days, %d hours, %d minutes, %d.%d seconds.",
				days, hours - days * 24, minutes - hours * 60, seconds
						- minutes * 60, (millis - seconds * 1000));
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map[] parseErrStacks(Throwable err){
		StackTraceElement[] sts = err.getStackTrace();
		Map[] stacks = new Map[sts.length+1];
		
		Map causedMap = new HashMap();
		causedMap.put("seq", 1);
		causedMap.put("data", err.toString());
		stacks[0] = causedMap;
		
		for(int i=0; i<sts.length; i ++){
			Map map = new HashMap();
			map.put("seq", i+2);
			map.put("data", sts[i].toString());
			stacks[i+1] = map;
		}
		return stacks;
	}
	
	/**
	 * get printStackTrace into a String
	 * 
	 * @param t
	 * @return
	 */
	public static String getStackTraceAsString(Throwable t) {
		if (t != null) {
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			return sw.toString();
		} else {
			return null;
		}
	}
	public final static String DEFAULT_ENCODE = "UTF-8";
	/**
	 * zip text to byte array, then convert to base64 string.
	 * 
	 * @param str
	 *            text need to zip
	 * @return null when text is null.
	 * @throws IOException
	 */
	public static String compress(String str) throws IOException {
		if (str != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GZIPOutputStream zos = new GZIPOutputStream(baos);
			zos.write(str.getBytes(DEFAULT_ENCODE));
			IOUtils.closeQuietly(zos);
			return Base64.encodeBase64String(baos.toByteArray());
		} else {
			return null;
		}
	}
	
	/**
	 * 对象转string
	 * @param o
	 * @return
	 */
	public static String toString(Object o) {
		return o == null ? "" : o.toString();
	}
	
	/**
	 * trim string
	 * @param o
	 * @return
	 */
	public static String trim(Object o) {
		return toString(o).trim();
	}
	
	/**
	 * 清楚文本中的注释
	 * @param content
	 * @return
	 */
	public static String clearComment(String content) {
		//替换快注释
		content = Pattern.compile("/\\*.*?\\*/",Pattern.DOTALL).matcher(content).replaceAll("");
		//替换行注释
		content = content.replaceAll("--.*", "");
		return content;
	}
	
	/**
	 * 获取数据类型的参数列表
	 * @param list
	 * @return Map[]
	 */
	@SuppressWarnings("rawtypes")
	public static Map[] listToArray(List<Map<String, Object>> list) {
		Map[] params = new Map[list.size()];
		for(int i=0; i<list.size(); i++){
			Map<String, Object> param = list.get(i);
			params[i] = param; 
		}
		return params;
	}
	
}
