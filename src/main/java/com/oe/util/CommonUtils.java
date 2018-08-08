package com.oe.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.log4j.Logger;
/**
 * 通用工具类
 * @author liaocheng
 *
 */
public class CommonUtils {
	
	private static final Logger logger = Logger.getLogger(CommonUtils.class);
	
	/**
	 * 生成32位随机UUID
	 * @return
	 */
	public static String generateUUID(){
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
	/**
	 * 判断字符串是否为空
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s ==null || s.equals("");
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
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String upperCaseFirst(String str) {
		if(toString(str).equals("")){
			return "";
		}
		String s = str.substring(0,1).toUpperCase() + str.substring(1);
		return s;
	}
	
	/**
	 * 添加 通过长度拆分字符串,取len前一个分割符,进行拆分
	 * @param line
	 * @param len
	 * @param lastSepr 最后一个分割符
	 * @return
	 */
	 public static List<String> splitStringByLenSeparator(String line,int len,String lastSepr) {
		  List<String> rst = new ArrayList<String>();
		  int from = 0;
		  int end = len;
		  if(line.length() < end){
			  end = line.length();
		  }
		  // 如果 line的总长度不足初始化的长度时，使用其最大长度
		  while(from<line.length()){
			   //先截取从from到end的字符串,再从该字符串截取从0到最后一个 lastSepr 的字符串
			   String subLine = line.substring(from, end);
			   int lseparator = subLine.lastIndexOf(lastSepr);
			   //实际截取长度
			   int realLen = len;
			   if(lseparator >= 0){
				   realLen = lseparator+1;
				   subLine = subLine.substring(0, lseparator+1);
			   }
			   rst.add(subLine);
			   from += realLen;
			   end += realLen;
			   if(end > line.length()){
				   end = line.length();
			   }
		  }
		  return rst;
	 }

	 /**
	  * 把日期格式化字符串(defaultFormat = "yyyy-MM-dd HH:mm:ss")
	  * @param date
	  * @return
	  */
	public static String format(Date date){
		String defaultFormat = "yyyy-MM-dd HH:mm:ss";
		return format(date,defaultFormat);
	}
	 
	/**
	 * 把日期格式化字符串
	 * @param o
	 * @return
	 */
	public static String format(Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	/**
	 * 解析字符串为日期
	 * @param o
	 * @return
	 * @throws Exception 
	 */
	public static Date parse(String date, String format)  {
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(date);
		} catch (ParseException e) {
			logger.error("日期解析失败", e);
		}
		return null;
	}
	/**
	 * 后台逻辑分页，通过params里面START和LIMIT，截取list中对应的记录返回
	 * 数据量少时，可以使用
	 * @param list 需要截取的list
	 * @param params 参数列表，必须包含START, LIMIT,如果START, LIMIT为空，则直接返回原结果
	 * @return 
	 */
	public static List<Object> fakePagination(List<Object> list,Map<String,Object> params ){
		List<Object> result = new ArrayList<Object>();
		if(list==null){
			return list;
		}
		
		String startStr = toString(params.get("START"));
		String limitStr = toString(params.get("LIMIT"));
		if(startStr.equals("")  || limitStr.equals("")){
			return list;
		}
		int start = Integer.valueOf(startStr);
		int limit = Integer.valueOf(limitStr);
		int index = 0;
		int addCount = 0;
		for (Object object : list) {
			if(index>=start && addCount<limit){
				result.add(object);
				addCount++;
			}
			index ++;
		}
		return result;
	}
	
	/**
	 * 切割list
	 * @param srcList
	 * @param splitSize
	 * @return
	 */
	public static <E> List<List<E>> splitList(List<E> srcList,int splitSize){
		
		if(null == srcList){
			return null;
		}
		
		int size = srcList.size();
		List<List<E>> resultList = new ArrayList<List<E>>();
		if(size <= splitSize){
			resultList.add(srcList);
		}else{
			int limit = 0;
			for(int i=0;i<size;i+=splitSize){
				limit = i+splitSize;
				if(limit > size){
					limit = size;
				}
				
				resultList.add(srcList.subList(i, limit));
			}
		}
		
		return resultList;
	}
	
	/**
	 * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
	 * @param bytes
	 * @return
	 */
	public static String bytes2kb(long bytes){
		BigDecimal filesize = new BigDecimal(bytes);
		BigDecimal megabyte = new BigDecimal(1024*1024);
		float returnValue = filesize.divide(megabyte,2,BigDecimal.ROUND_DOWN).floatValue();
		if(returnValue > 1){
			return (returnValue + "MB");
		}
		
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue = filesize.divide(kilobyte,2,BigDecimal.ROUND_DOWN).floatValue();
		
		return (returnValue + "KB");
	}
	
	/**
	 * 将MB转成bytes
	 * @param mb
	 * @return
	 */
	public static float mb2bytes(float mb){
		return kb2bytes(mb*1024);
	}
	/**
	 * 将 kb 转换成 bytes
	 * @param kb
	 * @return
	 */
	public static float kb2bytes(float kb){
		BigDecimal filesize = new BigDecimal(kb);
		BigDecimal kilobyte = new BigDecimal(1024);
		float returnValue = filesize.multiply(kilobyte).floatValue();
		
		return returnValue;
	}
	
	public static boolean haveText(String value){
		return !isBlank(value);
	}
	
	public static boolean isBlank(String value){
		if(null == value){
			return true;
		}
		int length = value.length();
		if(length == 0){
			return true;
		}
		
		for(int i=0; i<length;i++){
			if(!Character.isWhitespace(value.charAt(i))){
				return false;
			}
		}
		
		return true;
	}

	
	
	/**
	 * 通过文件路径获取byte[]
	 * @param filePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] getBytes(String filePath) throws FileNotFoundException,IOException{
		return FileUtils.readFileToByteArray(new File(filePath));
	}
	
	/**
	 * base64 to byte , then unzip .
	 * 
	 * @param str
	 *            text need to zip
	 * @return null when text is null.
	 * @throws IOException
	 */
	public static String unCompress(String str) throws IOException {
		String result = "";
		if (str != null) {
			byte[] buf = Base64.decodeBase64(str);
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(buf));
			List<String> readLines = IOUtils.readLines(in, DEFAULT_ENCODE);
			for (String line : readLines) {
				result += line + "\r\n";
			}
			IOUtils.closeQuietly(in);
		} 
		return result;
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
	
	public static String read(File file, String defaultCharset) throws IOException {
		String LINESEP = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		String charset = defaultCharset;
		try {
			charset = CharsetDetector.getCharset(file);
		} catch (IOException e) {
			logger.warn(
					String.format("Failed to get Charset file:",
							file.getAbsolutePath()), e);
		}
		LineIterator lines = null;
		try {
			lines = FileUtils.lineIterator(file, charset);
			while (lines.hasNext()) {
				sb.append(LINESEP);
				sb.append(lines.next());
			}
		} finally {
			if(lines != null){
				lines.close();
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取请求中的IP信息
	 * @param request
	 * @return
	 */
	public static String getClientIpAddr(HttpServletRequest request){
		String ip = null;
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("X-Forwarded-For");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
			ip = request.getRemoteAddr();
		}
		return ip;
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
	
	/**
	 * 生成指定位的随机数
	 * @param num
	 * @return
	 */
	public static String generateRandomArray(int num){
		
		RandomGenerator rg = new RandomGenerator(num);
		return rg.nextString();
	}
	
	/**
	 * 替换
	 * @param content
	 * @param key
	 * @param value
	 * @return
	 */
	 public static String stringReplaceFirst(String content, String key, String value) {
       if (content != null && key != null && value != null) {
           try {
              return java.util.regex.Pattern
                     .compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE)
                     .matcher(content).replaceFirst(value);
           } catch (IllegalArgumentException e) {
              throw new IllegalArgumentException(String.format("key: %s ,value: %s ,content: %s .", key, value,
                     content), e);
           }
       }
       return content;
    }
	 
	 /**
		 * 替换
		 * @param content
		 * @param key
		 * @param value
		 * @return
		 */
		 public static String stringReplaceAll(String content, String key, String value) {
	       if (content != null && key != null && value != null) {
	           try {
	              return java.util.regex.Pattern
	                     .compile(Pattern.quote(key), Pattern.CASE_INSENSITIVE)
	                     .matcher(content).replaceAll(value);
	           } catch (IllegalArgumentException e) {
	              throw new IllegalArgumentException(String.format("key: %s ,value: %s ,content: %s .", key, value,
	                     content), e);
	           }
	       }
	       return content;
	    }
}
