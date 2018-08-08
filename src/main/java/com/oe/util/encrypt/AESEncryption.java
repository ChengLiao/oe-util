package com.oe.util.encrypt;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密解密对象
 * <p/>
 * 非严格对象,忽略各种异常
 * 
 * @version 1.0.0
 */
public class AESEncryption {

	private static final String CHARSET = "UTF-8";//$NON-NLS-1$
	private static final String SYS_PASSWORD_DEFAULT = "1234567890123456";//$NON-NLS-1$

	// 密钥
	private SecretKeySpec key;
	// 字符编码
	private String code;

	public AESEncryption() {
		this(SYS_PASSWORD_DEFAULT);
	}

	public AESEncryption(String sysPwd) {
		this(sysPwd == null ? SYS_PASSWORD_DEFAULT : sysPwd, CHARSET);
	}

	/**
	 * 初始化密钥
	 * 
	 * @param password
	 *            公钥
	 * @param code
	 *            公钥编码
	 */
	private AESEncryption(String password, String code) {
		try {
			this.code = code;
			key = new SecretKeySpec(password.getBytes(this.code), 0, 16, "AES");
		} catch (UnsupportedEncodingException e) {
			// 忽略错误
		}
	}

	/**
	 * 加密字符串
	 * <p/>
	 * 操作失败返回null
	 * 
	 * @param text
	 *            加密前字符串
	 * @return String 加密后字符串
	 */
	public String encode(String text) {
		// 必须满足格式
		if (text == null) {
			return null;
		}
		byte[] buffer;
		try {
			buffer = text.getBytes(code);
		} catch (UnsupportedEncodingException e) {
			// 忽略异常
			return null;
		}
		buffer = encrypt(buffer, Cipher.ENCRYPT_MODE);
		return bytesToString(buffer);
	}

	/**
	 * 根据操作模式，进行加密解密操作
	 * <p/>
	 * 操作失败返回null
	 * 
	 * @param bytes
	 *            字节码
	 * @param mode
	 *            操作方式
	 * @return 操作后字节码
	 */
	private byte[] encrypt(byte[] bytes, int mode) {
		// 密钥初始化失败，直接操作失败
		if (key == null) {
			return null;
		}
		// 操作的字节码为空，直接操作失败
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(mode, key);
			return cipher.doFinal(bytes);
		} catch (Exception e) {
			// 忽略错误
		}
		return null;
	}

	/**
	 * 解密字符串
	 * <p/>
	 * 操作失败返回 encryptString
	 * 
	 * @param encryptString
	 *            加密后文字
	 * @return 解密后文字
	 */
	public String decode(String encryptString) {
		// 字符串为空，操作失败
		if (encryptString == null || encryptString.isEmpty()) {
			return encryptString;
		}
		try {
			byte[] buffer = toBytes(encryptString);
			buffer = encrypt(buffer, Cipher.DECRYPT_MODE);
			if (buffer != null) {
				return new String(buffer, code);
			}
		} catch (Exception e) {
			// 忽略异常
		}
		return encryptString;
	}

	/**
	 * 将密文字符串转化为字节,密文为双字节16进制格式
	 * 
	 * @param str
	 *            原字符串
	 * @return byte[]
	 */
	private byte[] toBytes(String str) {
		int l = str.length();
		byte[] ret = new byte[l / 2 + l % 2];
		for (int i = 0; i < l; i += 2) {
			String s;
			if (i + 2 >= str.length()) {
				s = str.substring(i);
			} else {
				s = str.substring(i, i + 2);
			}
			ret[i / 2] = (byte) Integer.parseInt(s, 16);
		}
		return ret;
	}

	/**
	 * bytes change String
	 * 
	 * @param buffer
	 *            buffer
	 * @return Strng
	 */
	private String bytesToString(byte[] buffer) {
		if (buffer == null) {
			return null;
		}
		StringBuilder tsResult = new StringBuilder();
		int tnLength = buffer.length;
		for (int i = 0; i < tnLength; ++i) {
			CharSequence tsTemp = Integer.toHexString(buffer[i] & 0x000000FF);
			if (tsTemp.length() == 1) {
				tsTemp = new StringBuilder(2).append("0").append(tsTemp);
			}
			tsResult.append(tsTemp);
		}
		return tsResult.toString().toUpperCase();
	}

}
