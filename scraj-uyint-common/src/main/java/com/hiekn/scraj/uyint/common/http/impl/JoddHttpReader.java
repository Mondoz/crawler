package com.hiekn.scraj.uyint.common.http.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.hiekn.scraj.uyint.common.http.HttpReader;
import com.hiekn.sfe4j.util.StringUtils;

import jodd.http.HttpBrowser;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.ProxyInfo;
import jodd.util.StringPool;

/**
 * 
 * 使用jodd HTTP作为引擎 读取静态网页源码
 * 
 * 
 * @since 1.7
 * @version 2.0
 * @date 2016/07/28
 * 
 */
public class JoddHttpReader extends CommonHttpReader implements HttpReader {
	
	HttpBrowser browser;
	int connectTimeout;
	int socketTimeout;
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		
		JoddHttpReader httpReader = new JoddHttpReader();
		
		String cookie = "pgv_pvi=4522435584; gr_user_id=346b1667-ee99-40f6-b62e-e11119896514; acw_tc=AQAAACdBpV657Q0A/XtRZctAzuTQ7GGS; session=07a8c71cb1c3f9c8e43e81726f54eff072987719; pgv_si=s2251020288; _gat=1; Hm_lvt_1c587ad486cdb6b962e94fc2002edf89=1469684347,1469860770,1469861181,1471239287; Hm_lpvt_1c587ad486cdb6b962e94fc2002edf89=1471239661; gr_session_id_eee5a46c52000d401f969f4535bdaa78=9b506f6b-fa7b-486b-a5fc-544af22c18bf; _ga=GA1.2.602458110.1467601125";
		
		String source = httpReader.readSource("https://www.itjuzi.com/investfirm/6493", null, cookie);
		System.out.println(source);
		httpReader.close();
	}
	
	{
		browser = new HttpBrowser();
		browser.setKeepAlive(true);
	}
	
	public JoddHttpReader() {
		this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		this.socketTimeout = DEFAULT_SOCKET_TIMEOUT;
	}
	
	public JoddHttpReader(int connectTimeout, int socketTimeout) {
		this.connectTimeout = connectTimeout;
		this.socketTimeout = socketTimeout;
	}
	
	public JoddHttpReader(String proxyAddress, int proxyPort, String proxyUser, String proxyPassword) {
		ProxyInfo proxyInfo = ProxyInfo.httpProxy(proxyAddress, proxyPort, proxyUser, proxyPassword);
		browser.setProxyInfo(proxyInfo);
		this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
		this.socketTimeout = DEFAULT_SOCKET_TIMEOUT;
	}
	
	public String readSource(String url, String charset, String cookie, String requestType, String params) {
		String pageSource = "";
    	
		if (StringUtils.isNullOrEmpty(url)) return pageSource;
		
    	// trim
    	url = url.trim();
    	LOGGER.info("request url ... " + url);
    	HttpResponse httpResponse = null;
    	for (int tryTime = 0; tryTime < MAX_TRIES; tryTime++) {
    		try {
    			if (HTTP_GET.equals(requestType)) {
	    			if (!StringUtils.isNullOrEmpty(params)) {
	    				if (url.indexOf("?") > -1) url += "&" + params;
	    				else url += "?" + params;
	    			} 
	    			
	    			HttpRequest httpGet = HttpRequest.get(url)
	    					.timeout(socketTimeout)
	    					//.connectionTimeout(connectTimeout)// 这个设置了  有些https网址会报错
	    					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
	    					//.header("Accept-Encoding", "gzip, deflate, sdch")
	    					.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6")
	    					.header("Cookie", null == cookie ? "" : cookie)
	    					.header("User-Agent", userAgents[new Random().nextInt(userAgents.length)]);
					 
					 //
					 httpResponse = browser.sendRequest(httpGet);
    			} else if (HTTP_POST.equals(requestType)) {
    				HttpRequest httpPost = HttpRequest.post(url)
    				.timeout(socketTimeout)
    				//.connectionTimeout(connectTimeout)// 这个设置了  有些https网址会报错
    				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
					//.header("Accept-Encoding", "gzip, deflate, sdch")
					.header("Connection", "keep-alive")
					.header("Cookie", null == cookie ? "" : cookie)
					.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6")
    				.header("User-Agent", userAgents[new Random().nextInt(userAgents.length)]);
    				 
    				 if (!StringUtils.isNullOrEmpty(params)) {
 						Map<String, Object> nvps = new HashMap<String, Object>();
 						String[] ps = params.split(";");
 						for (String param : ps) {
 							String[] nv = param.split("=");
 							nvps.put(nv[0], nv[1]);
 						}
 						httpPost.form(nvps);
 					}
    				//
    				httpResponse = browser.sendRequest(httpPost);
    			} else {
    				//
    				break;
    			}
    			
    			int status = httpResponse.statusCode();
    			LOGGER.info("response code ... " + status);
    			if (status == 200) {
    				String rawBody = httpResponse.unzip().body();
    				
    				String respCharset = httpResponse.charset();
    				if (null != respCharset) charset = respCharset;
    				
    				if (StringUtils.isNullOrEmpty(charset)) {
    					charset = parseCharset(rawBody);
    					LOGGER.info("detect " + url + " ... charset=" + charset);
    				}
    				
    				pageSource = new String(rawBody.getBytes(StringPool.ISO_8859_1), charset.equals("") ? DEFAULT_CHARSET :  charset);
    				//
    			}
    			
    			// if response is not null, whatever break.
    			break;
    		} catch (Exception e) {
    			e.printStackTrace();
    			LOGGER.error(e);
			} finally {
				if (null != httpResponse) httpResponse.close();
			}
    	}
		
    	LOGGER.info("request result length ... " + pageSource.length());
    	
		return pageSource;
	}
	
	public void close() {
		if (null != browser) browser.close();;
		browser = null;
	}

}
