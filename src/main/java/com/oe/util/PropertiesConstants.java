/**
 * 
 */
package com.oe.util;

import java.text.SimpleDateFormat;

/**
 * @author cns
 * 
 */
public interface PropertiesConstants {
	/**
	 * pattern: <b>yyyy-MM-dd HH:mm:ss</b>
	 */
	public static SimpleDateFormat TIMESTAMP0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static final String ENCODING = "UTF-8";

	/**
	 * get System.getProperty("file.separator")
	 */
	public static final String FILE_SEPARATOR = System.getProperty("file.separator");//$NON-NLS-1$
	
	/**
	 * get System.getProperty("line.separator")
	 */
	public final static String LINE_SEPARATOR = System.getProperty("line.separator");
	/**
	 * 30 seconds.
	 */
	public static final int POST_TIMEOUT = 30;


}
