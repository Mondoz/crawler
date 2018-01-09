package com.hiekn.scraj.uyint.common.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.hiekn.sfe4j.util.StringUtils;

/**
 * 
 * 
 * HttpFileReader
 * 
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 * 
 */
public final class HttpFileReader {
	
	/* static final variables */
	private static final Logger LOGGER = Logger.getLogger(HttpFileReader.class);
	private static final int tries = 3;
	private static final String HTTP_DEFAULT_METHOD = "GET";
	/* static final variables */
	
	/**
	 * 通过网络文件的url<br>
	 * 
	 * 读取网络文件的二进制数据
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	public static final byte[] getBytes(String url, String method, String cookie)
			throws MalformedURLException {
		return getByteArrayOutputStream(url, method, cookie).toByteArray();
	}

	/**
	 * 
	 * 文件二进制流 字符串形式
	 * 
	 * @param url
	 *            待下载文件的url
	 * @param method
	 *            文件下载请求方式
	 * @param charset
	 *            字节转字符串使用的解码格式
	 * @return
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 */
	public static final String getEncodeString(String url, String method,
			String charset, String cookie) throws MalformedURLException,
			UnsupportedEncodingException {
		// 字节转字符串
		ByteArrayOutputStream outs = getByteArrayOutputStream(url, method, cookie);
		return outs.size() > 0 ? outs.toString(charset) : "";
	}

	/**
	 * 文件二进制流经过base64 编码后的字符串
	 * 
	 * @param url
	 * @param method
	 * @return
	 * @throws MalformedURLException
	 */
	public static final String getBase64String(String url, String method, String cookie)
			throws MalformedURLException {
		ByteArrayOutputStream outs = getByteArrayOutputStream(url, method, cookie);
		// base64 encode
		return outs.size() > 0 ? Base64.encodeBase64String(outs.toByteArray()) : "";
	}

	private static final ByteArrayOutputStream getByteArrayOutputStream(
			String url, String method, String cookie) throws MalformedURLException {
		LOGGER.info("开始读取文件流 ... " + url);
		URL u = new URL(url);
		InputStream in = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (int i = 0; i < tries; i++) {
			out.reset();
			try {
				
				// First set the default cookie manager.
				CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

				HttpURLConnection httpConn = (HttpURLConnection) u
						.openConnection();
				
				httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.87 Safari/537.36");
				httpConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
				if (!StringUtils.isNullOrEmpty(cookie)) httpConn.setRequestProperty("Cookie", cookie);
				
				httpConn.setConnectTimeout(10000);
				httpConn.setReadTimeout(20000);
				httpConn.setRequestMethod(StringUtils.isNullOrEmpty(method) ? HTTP_DEFAULT_METHOD
						: method);

				int responseCode = httpConn.getResponseCode();
				// normally, 3xx is redirect
				if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
						|| responseCode == HttpURLConnection.HTTP_MOVED_PERM
						|| responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
					// get redirect url from "location" header field
					String newUrl = httpConn.getHeaderField("Location");
					// get the cookie if need, for login
					String cookies = httpConn.getHeaderField("Set-Cookie");
					// open the new connnection again
					httpConn = (HttpURLConnection) new URL(newUrl)
							.openConnection();
					if (!StringUtils.isNullOrEmpty(cookies)) httpConn.setRequestProperty("Cookie", cookies);
					httpConn.setConnectTimeout(10000);
					httpConn.setReadTimeout(20000);
					// reset responseCode
					responseCode = httpConn.getResponseCode();
				}

				// always check HTTP response code first
				if (responseCode == HttpURLConnection.HTTP_OK) {
					String fileName = "";
					String disposition = httpConn
							.getHeaderField("Content-Disposition");
					String contentType = httpConn.getContentType();
					int contentLength = httpConn.getContentLength();
					if (disposition != null) {
						// extracts file name from header field
						int index = disposition.indexOf("filename=");
						if (index > 0) {
							fileName = disposition.substring(index + 10,
									disposition.length() - 1);
						}
					} else {
						// extracts file name from URL
						fileName = url.substring(url.lastIndexOf("/") + 1,
								url.length());
					}
					LOGGER.info("Content-Type = " + contentType);
					LOGGER.info("Content-Disposition = " + disposition);
					LOGGER.info("Content-Length = " + contentLength);
					LOGGER.info("fileName = " + fileName);
					in = httpConn.getInputStream();
					byte[] buff = new byte[4096];
					int len = -1;
					while ((len = in.read(buff)) > -1) {
						out.write(buff, 0, len);
					}
				} else {
					LOGGER.error("No file to download. Server replied HTTP code: "
							+ responseCode);
				}
				httpConn.disconnect();
				break;
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error("IOException ... " + e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		LOGGER.info("读取到的文件流大小 ... " + out.size());
		return out;
	}
}
