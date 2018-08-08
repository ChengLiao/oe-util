/**
 * 
 */
package com.oe.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * @author cns
 * 
 */
public class CharsetDetector {

	/**
	 * 获取文件字符集
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public synchronized static String getCharset(File file) throws IOException {
		String encoding = null;
		byte[] buf = new byte[4096];
		UniversalDetector detector = new UniversalDetector(null);
		java.io.FileInputStream fis = null;
		try {
			fis = new java.io.FileInputStream(file);
			int nread;
			while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			encoding = detector.getDetectedCharset();
			if (encoding == null) {
				throw new IOException("No encoding detected.file:"
						+ file.getAbsolutePath());
			}
			// System.out.println(file.getAbsolutePath() + "\tencoding:" +
			// encoding);
			return encoding;
		} catch (IOException e) {
			throw e;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
			detector.reset();
		}
	}
	
	/**
	 * 获取文件字符集
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public synchronized static String getCharset(InputStream is) throws IOException {
		String encoding = null;
		byte[] buf = new byte[4096];
		UniversalDetector detector = new UniversalDetector(null);
		try {
			int nread;
			while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			detector.dataEnd();
			encoding = detector.getDetectedCharset();
			if (encoding == null) {
				throw new IOException("No encoding detected in InputStream.");
			}
			return encoding;
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			detector.reset();
		}
	}
	
}
