package com.oe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.google.gson.GsonBuilder;

public class JsonUtil {

	private static final Logger logger = Logger.getLogger(JsonUtil.class);

	/**
	 * @param object
	 * @return
	 */
	public static String toJson(Object object) {
		final GsonBuilder gb = new GsonBuilder();
		gb.setPrettyPrinting();
		gb.serializeNulls();
		String json = gb.create().toJson(object);
		return json;
	}

	/**
	 * @param object
	 * @param file
	 * @throws IOException
	 */
	public static void write(Object object, File file) throws IOException {
		PrintWriter out1 = null;
		try {
			out1 = new PrintWriter(file, PropertiesConstants.ENCODING);
			out1.write('\uFEFF');
			out1.write(toJson(object));
			out1.flush();
			logger.debug("Save result to " + file.getAbsolutePath());
		} finally {
			if (out1 != null) {
				out1.close();
			}
		}
	}

	/**
	 * @param json
	 * @param classOfT
	 * @return
	 */
	public static <T> T read(String json, Class<T> classOfT) {
		final GsonBuilder gb = new GsonBuilder();
		return gb.create().fromJson(json, classOfT);
	}

	/**
	 * @param json
	 * @param classOfT
	 * @return
	 * @throws IOException
	 */
	public static <T> T read(File file, Class<T> classOfT) throws IOException {
		BufferedReader reader = null;
		StringBuilder sb;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), PropertiesConstants.ENCODING));
			sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return read(sb.toString(), classOfT);
	}

}
