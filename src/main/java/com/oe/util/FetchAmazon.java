package com.oe.util;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
@SuppressWarnings("serial")
public class FetchAmazon {
	
	public static String skuUrl = "http://113.108.97.88:91/v3real/productindex.php?keys=&goodscategory=-1&module=warehouse&action=%E8%B4%A7%E5%93%81%E8%B5%84%E6%96%99%E7%AE%A1%E7%90%86&warehouse=&isuse=%E5%9C%A8%E5%8D%96%20%E6%9C%89%E5%BA%93%E5%AD%98&searchs=6&factory=&cguser=&salesuser=&sale_lb=2&stock_count=&pageindex=";
	public static String mainUrl = "http://113.108.97.88:91/v3real/";
	public static Map<String, String> commonHeaders = new HashMap<String, String>(){
		{
			put("Cookie", "PHPSESSID=qlm29lgtckuqqvrkf2nmau20j4; token=2346_0410e44d2e4dc8fa8d6a91f932ad756e;  ");
		}};
	
		public static Map<String, String> amazonHeaders = new HashMap<String, String>(){
			{
				put("Host", "www.amazon.com");
				put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
				put("Accept-Encoding", "gzip, deflate, br");
			}};
	
	public static List<Sku> skus = new ArrayList<Sku>();
	
	public static void main(String[] args) throws Exception {
		int index = 1;
		while(true) {
			System.out.println("request v3, index : " + index);
			String skuList = HttpUtils.sendGet(skuUrl + index , null, commonHeaders);
			System.out.println("request v3, index : " + index + ";end");
			Document doc = Jsoup.parse(skuList);
			Elements select = doc.select("#main .listViewBody .list>tbody .oddListRowS1");
			
			for (Element element : select) {
				long start = System.currentTimeMillis();
				Sku sku = new Sku();
				try {
					sku.setId(element.child(1).select("div").get(2).html());
					sku.setPicLink(element.child(1).select("div").get(4).select("a").get(0).attr("href"));
					sku.setPrice(parsePirce(element.child(4).select("div").get(3).html()));
					sku.setLink(element.child(7).select("li").get(4).child(0).attr("href"));
					
					SubSku subSku = new SubSku();
					subSku.setId(element.child(1).select("div").get(1).child(0).html());
					subSku.setSkuid(element.child(1).select("div").get(2).html());
					
					if(getAgeGroup(sku)) {
						if(skus.contains(sku)) {
							find(skus, sku.getId()).getSubSkus().add(subSku);
						}else {
							sku.getSubSkus().add(subSku);
							skus.add(sku);
						}
					}
					
					System.out.println("searched : " + sku.getId());
					
					//查找链接， 访问，获取name desc size
					getName(sku);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//查找图片
				//TODO
				
				
				long end = System.currentTimeMillis();
				System.out.println("reqeusting time : " + (start-end)/1000 + "s");
				System.out.println("current sku size : " + skus.size());
			}
			if(select.size() == 0 ) {
				break;
			}
			index ++;
			if(index > 3) {
				break;
			}
		}
		
		
		for (Sku sku : skus) {
			for(int i=0; i< sku.getSubSkus().size(); i++) {
				sku.getSubSkus().get(i).setSize(sku.getSizes().get(i));
			}
		}
		System.out.println("writting file...");
		System.out.println(skus);
//		FileUtils.writeStringToFile(new File("/root/amazon/skus.json"), JsonUtil.toJson(skus));
	}


	
	private static boolean getAgeGroup(Sku sku) {
		if(sku.getId().startsWith("FT")) {
			sku.setAgeGroup("Child");
			sku.setSizeGroup("Girls");
			return true;
		}else if(sku.getId().startsWith("FF")) {
			sku.setAgeGroup("Adult");
			sku.setSizeGroup("Women");
			return true;
		}else if(sku.getId().startsWith("FN")) {
			sku.setAgeGroup("Adult");
			sku.setSizeGroup("Women");
			return true;
		}
		return false;
	}



	public static void getName(Sku sku) throws Exception {
		System.out.println("search links ...");
		String url = mainUrl + sku.getLink();
		String links = HttpUtils.sendGet(url, null, commonHeaders);
		System.out.println("search links end ...");
		Document doc = Jsoup.parse(links);
		Elements select = doc.select(".tb_list>tbody>tr");
		for (Element element : select) {
			if(element.child(1).child(0).html().contains("amazon")) {//平台
				String lang = element.child(3).child(2).select("i").get(0).html();
				if(lang.endsWith("us") || lang.endsWith("ca")) {
					String amazonLink = element.child(3).select("a").get(0).attr("href");
					sku.setAmazonLink(amazonLink);
					visitAmazon(sku);
					break;
				}
			}
		}
	}


	public static void visitAmazon(Sku sku) throws Exception {
		System.out.println("search amazon ...; url:" + sku.getAmazonLink());
		byte[] sendGet = HttpUtils.sendGet4Byte(sku.getAmazonLink() , null , amazonHeaders);
		System.out.println("search amazon end ...");
		String content = StringUtils.uncompressToString(sendGet);
		Document doc = Jsoup.parse(content);
		String title = doc.select("#productTitle").html().trim();
		String description = "";
		Elements select = doc.select("#feature-bullets .a-list-item");
		for (Element element : select) {
			description += element.html() + "<BR>";
		}
		
		String feature = null;
		if(doc.select("#productDescription p").size() > 0) {
			feature = doc.select("#productDescription p").get(0).html();
		}
		
		List<String> sizes = new ArrayList<String>();
		Elements sizesElem = doc.select("#native_dropdown_selected_size_name > option");
		for (Element element : sizesElem) {
			if(!element.attr("value").equals("-1")) {
				sizes.add(element.html().trim());
			}
		}
		sku.setName(title);
		sku.setDescription(description);
		sku.setKeyFeature(feature == null ? description : feature);
		sku.setSizes(sizes);
	}



	private static Sku find(List<Sku> list, String id) {
		for (Sku sku : list) {
			if(sku.getId().equals(id)) {
				return sku;
			}
		}
		return null;
	}



	private static double parsePirce(String price) {
		price = price.split("\\$")[1];
		BigDecimal decimal = new BigDecimal(price);
		decimal = decimal.add(new BigDecimal(5)).setScale(0, BigDecimal.ROUND_HALF_UP);
		decimal = decimal.subtract(new BigDecimal(0.01));
		return decimal.doubleValue();
	}

}
