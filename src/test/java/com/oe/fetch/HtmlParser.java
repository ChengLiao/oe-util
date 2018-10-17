package com.oe.fetch;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.oe.util.CommonUtils;
import com.oe.util.HttpUtils;

public class HtmlParser {
	private static List<Map<String,String>> orgs = new ArrayList<Map<String, String>>();
	private static List<Map<String,String>> users = new ArrayList<Map<String, String>>();
	private static String orgUrl = "http://99.12.115.118/ITHR/ITPE/Address/AddressBookFront.aspx";
	private static String userUrl = "http://99.12.115.118/ITHR/ITPE/Address/AddressList.aspx?v=1142036398&Oper=event&orgid=";
	
	public static void main(String[] args) throws Exception {
		Document doc = Jsoup.parse(HttpUtils.sendGet(orgUrl));
		Element root = doc.select("#orgTree").get(0);
		findOrgNodes(root, ".orgTree_2 a");
		for (Map<String, String> org : orgs) {
			findUser(org);
		}
		PrintWriter writer = new PrintWriter("D:/works/user_import/import_0802.txt");
		for (Map<String, String> user : users) {
			writer.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", 
				user.get("orgId"),
				user.get("parentId"),
				user.get("orgName"),
				user.get("userId"),
				user.get("userName"),
				user.get("position"),
				user.get("tel"),
				user.get("email"),
				user.get("uid")
			));
		}
		
		for (Map<String, String> user : users) {
			System.out.println(String.format("INSERT INTO USER_IMPORT VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');", 
				user.get("orgId"),
				user.get("parentId"),
				user.get("orgName"),
				user.get("userId"),
				user.get("userName"),
				user.get("position"),
				user.get("tel"),
				user.get("email"),
				user.get("uid")
			));
		}
		writer.flush();
		writer.close();
	}

	private static void findOrgNodes(Element root, String select) {
		Elements hrefs = root.select(select);
		for (Element href : hrefs) {
			String orgName = href.text();
			String orgIds = href.attr("href").split(",")[1].replaceAll("['\\)]", "");
			String[] arr = orgIds.split("\\\\+");
			String parentId,orgId;
			if(arr.length>1){
				parentId = arr[arr.length-2].replace("s", "");
			}else{
				parentId = "-1";
			}
			orgId = arr[arr.length-1].replace("s", "");
			Map<String,String> org = new HashMap<String, String>();
			org.put("orgName", orgName);
			org.put("parentId", parentId);
			org.put("orgId", orgId);
			orgs.add(org);
		}
	}
	
	private static void findUser(Map<String,String> org) throws Exception{
		String html = HttpUtils.sendGet(userUrl + org.get("orgId"));
//		System.out.println(html);
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("#address table tr");
		for (Element tr : trs) {
			Map<String,String> user = new HashMap<String, String>();
			Elements tds = tr.select("td");
			if(tds.size()<4){
				continue;
			}
			if(CommonUtils.isBlank(tds.get(0).text()) || tds.get(3).text().startsWith("��ְ")){
				System.out.println(tds.get(1).text() +"-----"+ tds.get(3).text());
				continue;
			}
			int index = 0;
			user.putAll(org);
			user.put("userId", tds.get(index++).text());
			user.put("userName", tds.get(index++).text());
			user.put("position", tds.get(index++).text());
			user.put("status", tds.get(index++).text());
			user.put("homePhone", tds.get(index++).text());
			user.put("tel", tds.get(index++).text().replace("-", ""));
			user.put("email", tds.get(index++).text());
			user.put("n1", tds.get(index++).text());
			user.put("uid", tds.get(index++).text());
			users.add(user);
		}
	}
}
