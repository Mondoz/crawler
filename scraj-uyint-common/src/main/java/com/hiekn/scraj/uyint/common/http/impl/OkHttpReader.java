package com.hiekn.scraj.uyint.common.http.impl;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.hiekn.scraj.uyint.common.http.HttpReader;
import com.hiekn.sfe4j.util.StringUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpReader extends CommonHttpReader implements HttpReader {
	
	public static void main(String[] args) throws IOException {
//		OkHttpClient client = new OkHttpClient.Builder()
//				.connectTimeout(15, TimeUnit.SECONDS)
//				.readTimeout(15, TimeUnit.SECONDS)
//				.writeTimeout(15, TimeUnit.SECONDS)
//				.build();
//		Request request = new Request.Builder()//
//				.url("http://news.163.com")//
//				.build();
//		Response response = client.newCall(request).execute();
//		System.out.println(response.body().string());
		
		OkHttpReader okHttpReader = new OkHttpReader();
		String str = okHttpReader.readSource("https://www.itjuzi.com/investfirm/6493");
		System.out.println(str);
		okHttpReader.close();
	}
	
	static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
	
	// instance block, initial okhttp client.
	{
		client = new OkHttpClient.Builder()
				.connectTimeout(15, TimeUnit.SECONDS)
				.readTimeout(15, TimeUnit.SECONDS)
				.writeTimeout(15, TimeUnit.SECONDS)
				.build();
	}
	
	OkHttpClient client;
	
	public String readSource(String url, String charset, String cookies, String requestType, String params) {
		String pageSource = "";
		
		if (StringUtils.isNullOrEmpty(url)) return pageSource;
		
		//
		LOGGER.info("request url... " + url);
		for (int tryTime = 0; tryTime < MAX_TRIES; tryTime++) {
			
			Request request = null;
			if (HTTP_GET.equals(requestType)) {
				if (!StringUtils.isNullOrEmpty(params)) {
    				if (url.indexOf("?") > -1) url += "&" + params;
    				else url += "?" + params;
    			} 
				
				// build request.
				request = new Request.Builder()//
						.url(url)//
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")//
    					//.header("Accept-Encoding", "gzip, deflate, sdch")//
    					.header("Cookie", null == cookies ? "" : cookies)//
    					.header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6")//
    					.header("User-Agent", userAgents[new Random().nextInt(userAgents.length)])//
						.build();
				
			} else if (HTTP_POST.equals(requestType)) {
				Map<String, Object> nvps = new HashMap<String, Object>();
				if (!StringUtils.isNullOrEmpty(params)) {
					String[] ps = params.split(";");
					for (String param : ps) {
						String[] nv = param.split("=");
						nvps.put(nv[0], nv[1]);
					}
				} 
				request = new Request.Builder()
				      .url(url)
				      .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")//
  					  //.header("Accept-Encoding", "gzip, deflate, sdch")//
  					  .header("Cookie", null == cookies ? "" : cookies)//
  					  .header("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6")//
  					  .header("User-Agent", userAgents[new Random().nextInt(userAgents.length)])//
				      .post(RequestBody.create(JSON_TYPE, new Gson().toJson(nvps)))
				      .build();
			} else {
				// just break;
				break;
			}
			
			
			// actually execute request.
			try (Response response = client.newCall(request).execute();
				 ResponseBody responseBody = response.body()) {
				
				int status = response.code();
				LOGGER.info("http response code... " + status);
				if (response.isSuccessful()) {
					// get raw body.
					byte[] raws = responseBody.bytes();
					//
					Charset respCharset = responseBody.contentType().charset();
					if (null != respCharset) charset = respCharset.name();;
					
					// detect charset.
					if (StringUtils.isNullOrEmpty(charset)) {
						String str = new String(raws, ISO_8859_1);
						charset = parseCharset(str);
						LOGGER.info("detect [" + url + "], charset... " + charset);
					}
					
					//
					pageSource = new String(raws, charset.equals("") ? DEFAULT_CHARSET :  charset);
				}
				//
				break;
			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.error(e);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e);
			}
		}// retry for loop end.
		
		LOGGER.info("request result length... " + pageSource.length());
		
		return pageSource;
	}
	
	public void close() {
		client = null;
	}

}
