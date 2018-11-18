package com.oe.util.task;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.oe.util.HttpUtils;

@Component
@Configurable
@EnableScheduling
public class NewSkuTask {	
	
	private String url = "http://113.108.97.88:85/index.php";
	private static int limit = 2;
	
	
	public static Map<String, String> postHeaders = new HashMap<String, String>(){
		{
			put("Host", "113.108.97.88:85;");
			put("Origin", "http://113.108.97.88:85");
			put("Referer", "http://113.108.97.88:85/index.php?m=Index&a=index");
			put("X-Requested-With", "XMLHttpRequest");
			put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36");

		}};
		
	public static Map<String, String> getHeaders = new HashMap<String, String>(){
		{
			put("Host", "113.108.97.88:85;");
			put("Origin", "http://113.108.97.88:85");
			put("Referer", "http://113.108.97.88:85/index.php?m=Index&a=index");
			put("X-Requested-With", "XMLHttpRequest");
			put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.75 Safari/537.36");
		}};
			
	private String loginRefer = "http://113.108.97.88:85/index.php?m=Index&a=index";
	
	@Scheduled(cron = "0 57 8 * * * ")
    public void run(){
        System.out.println ("start running" );
        Random random = new Random();
        //login 
        try {
        	System.out.println("get cookie...");
        	getHeaders.put("Referer", "http://113.108.97.88:85/index.php/Index/main/");
        	Map<String, List<String>> sendGet4Header = HttpUtils.sendGet4Header(url, null, getHeaders);
        	List<String> list = sendGet4Header.get("Set-Cookie");
        	if(list ==null || list.size() ==0) {
        		System.err.println("get cookie error ");
        		return;
        	}
        	String cookie = "";
        	for (String str : list) {
        		cookie += str.split(";")[0];
			}
        	System.out.println(cookie);
        	postHeaders.put("Cookie", cookie);
        	getHeaders.put("Cookie", cookie);
        	
        	System.out.println("start login...");
        	postHeaders.put("Referer", loginRefer);
			String loginResult = HttpUtils.sendPost(url + "/Index/login", "username=叶晓文&password=yxw180&code=", postHeaders);
			System.out.println("login result:" + loginResult);
			
			
			while(true) {
				System.out.println("start get list");
				try {
					Calendar cal = Calendar.getInstance();
					if(cal.get(Calendar.HOUR) > 9 && cal.get(Calendar.MINUTE) > 2) {
						break;
					}
					
					String getUrl = "/Deadstock/getnewproduct?category=FT&keyword=&sort=";
					
					getHeaders.put("Referer", "http://113.108.97.88:85/index.php/Deadstock/getnewproduct?category=&keyword=&sort=");
					String sendGet = HttpUtils.sendGet(url + getUrl, null, getHeaders);
					System.out.println("list result ");
					String parseHtml = parseHtml(sendGet);
					if(parseHtml == null) {
						System.out.println("find no result...");
						Thread.sleep((1500 + random.nextInt(10) * 100));
						continue;
					}
					
					System.out.println("get skus:" + parseHtml);
					postHeaders.put("Referer", url + getUrl);
					String sendPost = HttpUtils.sendPost(url + "Deadstock/getproductrecord/", parseHtml ,postHeaders);
					
					System.out.println("get skus result : " + sendPost);
					break;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	public String parseHtml(String html) {
		Document doc = Jsoup.parse(html);
		Elements select = doc.select("#table td input");
		if(select==null || select.isEmpty()) {
			return null;
		}
		String s=  "";
		for(int i=0; i<select.size() && i < limit; i++) {
			Element element = select.get(i);
			s += element.attr("value");
			if(!(i==select.size() - 1 || i == limit - 1)) {
				s += ",";
			}
		}
		return s;
	}
	
	public static void main(String[] args) throws IOException {
		new NewSkuTask().run();
		
	}
}
