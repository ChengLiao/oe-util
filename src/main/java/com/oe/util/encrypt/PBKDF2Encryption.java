package com.oe.util.encrypt;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Base64;

public class PBKDF2Encryption {

	private static final int DEFAULT_COST = 10;
	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";//$NON-NLS-1$
	private static final int SIZE = 128;
	private static final Pattern layout = Pattern
			.compile("(\\d\\d?)\\$(.{43})");//$NON-NLS-1$

	private final SecureRandom random;
	private final int cost;

	public PBKDF2Encryption() {
		this(DEFAULT_COST);
	}

	public PBKDF2Encryption(int cost) {
		iterations(cost);
		this.cost = cost;
		this.random = new SecureRandom();
	}

	/**
	 * 迭代，cost越大越耗时
	 * 
	 * @param cost
	 *            0 to 30
	 * @return
	 */
	private static int iterations(int cost) {
		if ((cost & ~0x1F) != 0) {
			throw new IllegalArgumentException("cost:" + cost);
		}
		return 1 << cost;
	}

	/**
	 * 加密
	 * 
	 * @param password
	 *            明文
	 * @return
	 */
	public String encode(String password) {
		return hash(password.toCharArray());
	}

	/**
	 * 加密
	 * 
	 * @param password
	 *            明文
	 * @return 长度为45-46的密文
	 */
	private String hash(char[] password) {
		byte[] salt = new byte[SIZE / 8];
		random.nextBytes(salt);
		byte[] dk = pbkdf2(password, salt, 1 << cost);
		byte[] hash = new byte[salt.length + dk.length];
		System.arraycopy(salt, 0, hash, 0, salt.length);
		System.arraycopy(dk, 0, hash, salt.length, dk.length);
		return String.format("%d%s%s", cost, '$',
				Base64.encodeBase64URLSafeString(hash));
	}

	/**
	 * 验证密码是否一致
	 * 
	 * @param password
	 *            明文
	 * @param token
	 *            密文
	 * @return
	 */
	public boolean authenticate(String password, String token) {
		return authenticate(password.toCharArray(), token);
	}

	/**
	 * 验证密码是否一致
	 * 
	 * @param password
	 *            明文
	 * @param token
	 *            密文
	 * @return
	 */
	private boolean authenticate(char[] password, String token) {
		Matcher m = layout.matcher(token);
		if (!m.matches()) {
			throw new IllegalArgumentException("Invalid token format:" + token);
		}
		int iterations = iterations(Integer.parseInt(m.group(1)));
		byte[] hash = Base64.decodeBase64(m.group(2));
		byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
		byte[] check = pbkdf2(password, salt, iterations);
		int zero = 0;
		for (int idx = 0; idx < check.length; ++idx) {
			zero |= hash[salt.length + idx] ^ check[idx];
		}
		return zero == 0;
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
		KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
		try {
			return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec)
					.getEncoded();
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException("Invalid SecreKeyFactory", e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Missing Algorithm", e);
		}
	}

}
