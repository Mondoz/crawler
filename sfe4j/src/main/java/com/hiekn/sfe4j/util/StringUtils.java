package com.hiekn.sfe4j.util;

import java.io.UnsupportedEncodingException;

public class StringUtils {
	
	/**
	 * 
	 * 过滤字符串两端空格
	 * 
	 * @param str
	 * @return
	 */
	public static final String trim(String str) {
		str = str.trim();
		if (str.startsWith("\u3000")) str = str.substring(1, str.length());
		if (str.endsWith("\u3000")) str = str.substring(0, str.length() - 1);
		return str.trim();
	}
	
	/**
	 * 判断字符串是否为null或者""
	 * @param str
	 * @return
	 */
	public static final boolean isNullOrEmpty(String str) { 
		return null == str || str.length() == 0; 
	}
	
	/**
	 * toString 返回""或者string字符串,
	 * never return null.
	 * 
	 * @param val
	 * @return
	 */
	public static final String toString(Object val) {
		if (null == val) return "";;
		return val.toString();
	}
	
	/**
	 * 按指定编码格式对字符串进行编码,
	 * 如果字符串为null 返回 长度为0的字节数组
	 * @param str
	 * @param charsetName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final byte[] encode(String str, String charsetName) throws UnsupportedEncodingException {
		if (null == str || "".equals(str)) return new byte[0];;
		return str.getBytes(null == charsetName || "".equals(charsetName) ? "utf-8" : charsetName);
	}
	
	/**
	 * 
	 * @param bytes
	 * @param charsetName
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static final String decode(byte[] bytes, String charsetName) throws UnsupportedEncodingException {
		if (null == bytes || bytes.length == 0) return "";;
		return new String(bytes, null == charsetName || "".equals(charsetName) ? "utf-8" : charsetName);
	}
	
	/**
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static final String truncate(String str, int length) {
		if (null == str || "".equals(str)) return "";;
		if (str.length() > length) return str.substring(0, length);;
		return str;
	}
}
