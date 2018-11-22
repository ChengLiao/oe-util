package com.oe.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;

import com.google.gson.GsonBuilder;

public class ReadJson {
	public static void main(String[] args) throws Exception {
		
		GsonBuilder gb = new GsonBuilder();
		List<Map> read = JsonUtil.read(new File("C:\\Users\\cheng\\Documents\\temp\\skus.json"), List.class);
//		List<Map> read = JsonUtil.read(new File("/root/amazon/skus.json"), List.class);
		List<Sku> skus = new ArrayList<Sku>();
		for (Map map : read) {
			Sku sku = new Sku();
			BeanUtils.populate(sku, map);
			skus.add(sku);
		}
		System.out.println("================================>");
//		complete(skus);
		List<String> ids = new ArrayList<String>();
		
		for (Sku sku : skus) {
//			System.out.println(sku);
			if(sku.getSubSkus()!=null && sku.getSubSkus().size() > 0) {
				List subSkus = sku.getSubSkus();
				for (Object subSku : subSkus) {
					
					
					StringBuilder sb = new StringBuilder();
					Map map = (Map)subSku;
					
					if(ids.contains(map.get("id"))) {
						continue;
					}
					ids.add((String) map.get("id"));
					
					sb.append("%s\t%s\tUPC");
					sb.append(tab(8));
					sb.append("2038711\tFaithtur");
					sb.append(tab(18));
					sb.append("%s");
					sb.append(tab(9));
					sb.append("White\tWhite\t\t\tFemale\t%s\t%s\t" + map.get("size") + "\t\t0.50\t1b\t\t\tclothingSize\tcolor");
					sb.append("\t").append(sku.getAmazonLink());
					sb.append("\t").append(sku.getPicLink());
					System.out.println(String.format(sb.toString(), map.get("id") ,sku.getName(),
							sku.getPrice(),sku.getAgeGroup(), sku.getSizeGroup()));
				}
			}else {
				if(ids.contains(sku.getId())) {
					continue;
				}
				ids.add(sku.getId());
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("%s\t%s\tUPC");
				sb.append(tab(8));
				sb.append("2038711\tFaithtur");
				sb.append(tab(18));
				sb.append("%s");
				sb.append(tab(9));
				sb.append("White\tWhite\t\t\tFemale\t%s\t%s\tsize\t\t0.50\t1b\t\t\tclothingSize\tcolor");
				sb.append("\t").append(sku.getAmazonLink());
				sb.append("\t").append(sku.getPicLink());
				System.out.println(String.format(sb.toString(), sku.getId() ,sku.getName(),
						sku.getPrice(),sku.getAgeGroup(), sku.getSizeGroup()));
			}
		}
		
		
	}

	private static String tab(int num) {
		String str = "";
		for(int i=0; i<num; i++) {
			str+="\t";
		}
		return str;
	}

	public static void complete(List<Sku> skus) throws IOException {
		for (Sku sku : skus) {
			if(sku.getName() == null && sku.getAmazonLink() !=null) {
				try {
					FetchAmazon.visitAmazon(sku);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(sku);
			}
		}
		
		FileUtils.writeStringToFile(new File("/root/amazon/skus.json"), JsonUtil.toJson(skus));
	}
}
