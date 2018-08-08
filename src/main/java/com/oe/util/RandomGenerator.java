/**
 * 
 */
package com.oe.util;

import java.util.Random;

/**
 * @author cns
 * 
 */
public class RandomGenerator {

	private static final char[] symbols;

	static {
		StringBuilder tmp = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch) {
			tmp.append(ch);
		}
		for (char ch = 'A'; ch <= 'Z'; ++ch) {
			tmp.append(ch);
		}
		symbols = tmp.toString().toCharArray();
	}

	private final Random random = new Random();

	private final char[] buf;

	/**
	 * @param length
	 *            需要生成的随机串长度
	 */
	public RandomGenerator(int length) {
		this(System.nanoTime(), length);
	}

	/**
	 * @param seed
	 *            随机种子
	 * @param length
	 *            需要生成的随机串长度
	 */
	public RandomGenerator(long seed, int length) {
		if (length < 1) {
			throw new IllegalArgumentException("length < 1: " + length);
		}
		buf = new char[length];
		random.setSeed(seed);
	}

	/**
	 * 获取下一个随机串
	 * 
	 * @return
	 */
	public String nextString() {
		for (int idx = 0; idx < buf.length; ++idx) {
			buf[idx] = symbols[random.nextInt(symbols.length)];
		}
		return new String(buf);
	}
}
