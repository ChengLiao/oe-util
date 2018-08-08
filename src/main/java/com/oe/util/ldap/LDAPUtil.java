package com.oe.util.ldap;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 * @author Cheng Liao
 * @date 2017-8-3 下午4:48:10
 */
public class LDAPUtil {
	public static void main(String[] args) throws IOException {
		InputStream in = LDAPUtil.class.getResourceAsStream("/config.properties");
		Properties props = new Properties();
		props.load(in);
		
		String LDAP_URL = props.getProperty("URL");	// LDAP访问地址
		String adminName = props.getProperty("ADMIN"); // 注意用户名的写法：domain\User或
		String adminPassword = props.getProperty("PWD"); // 密码
		String base = props.getProperty("BASE");
		String filter = props.getProperty("FILTER");
		System.out.println(String.format("url:%s,admin:%s,password:%s,base:%s,filter:%s", LDAP_URL, adminName, adminPassword, base, filter));
		try{
			Vector<HashMap<String, String>> list= syncUserADService(adminName,adminPassword, LDAP_URL, base, filter);
			System.out.print(list.size());
			for(int i=0;i<list.size();i++)
			{
				HashMap<String, String> HashMap= list.get(i);
				System.out.println(HashMap);
				System.out.print(HashMap.get("sAMAccountName")+"   ");
				System.out.print(HashMap.get("name")+"   ");
				System.out.print(HashMap.get("telephoneNumber")+"   ");
				System.out.print(HashMap.get("mobile")+"   ");
				System.out.print(HashMap.get("department")+"   ");
				System.out.print(HashMap.get("mail")+"   \r\n");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//System.exit(0);
	}
	
	/**
	 * 
	 * Ldap连接初始化
	 * 
	 * @return LdapContext
	 */
	private static LdapContext getLdapContext(String adminName,String adminPassword,String ldap_url) throws Exception {
		LdapContext ctx=null;
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.SECURITY_PRINCIPAL, adminName);
        env.put(Context.SECURITY_CREDENTIALS, adminPassword);
        env.put(Context.PROVIDER_URL, ldap_url);
		try {
			ctx= new InitialLdapContext(env, null);// 初始化上下文
			System.out.println("get LdapContext success");// 这里可以改成异常抛出。
		} catch (javax.naming.AuthenticationException e) {
			System.out.println("get LdapContext fail");
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			System.out.println("get LdapContext error：" + e);
			e.printStackTrace();
			throw e;
		}
		return ctx;
	}
	/**同步AD信息*/
	public static Vector<HashMap<String, String>> syncUserADService(String adminName,String adminPassword,String ldap_url, String base,String filter) throws Exception
	{
		Vector<HashMap<String, String>> vals = new Vector<HashMap<String, String>>();
		LdapContext ctx=null;
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String[] returnedAtts = { "url", "name", "telephoneNumber", "homePhone", "mobile",
                "department", "sAMAccountName","sAMAccountType",  "mail" }; // 定制返回属性
        sc.setReturningAttributes(null);
        int i=0;
		NamingEnumeration<SearchResult> ne=null;
		try {
			ctx=getLdapContext(adminName,adminPassword,ldap_url);
			ne = ctx.search(base, filter, sc);
			while (ne.hasMoreElements()) {
				SearchResult sr = (SearchResult) ne.next();
				i++;
				HashMap<String, String> valueH=new HashMap<String,String>();
				 //得属性数组
				Attributes at = sr.getAttributes();
				System.out.println(at);
				NamingEnumeration<? extends Attribute> ane = at.getAll();
				while (ane.hasMoreElements()) {
					Attribute attr = (Attribute) ane.next();
					String attrType = attr.getID();
					NamingEnumeration<?> values = attr.getAll();
					
					// Another NamingEnumeration object, this time
					// to iterate through attribute values.
					while (values.hasMoreElements()) {
						Object oneVal = values.nextElement();
						if (oneVal instanceof String) {
							valueH.put(attrType, (String)oneVal);
						} else {
							valueH.put(attrType, new String((byte[]) oneVal));
						}
					}
				}
				if(valueH.size()>0)vals.add(valueH);
			}
		} catch (Exception nex) {
			nex.printStackTrace();
			throw nex;
		}finally{
			if(ctx!=null)
			{
				ctx.close();
			}
		}
		System.out.println("记录总数："+i);
		return vals;
	}
}
