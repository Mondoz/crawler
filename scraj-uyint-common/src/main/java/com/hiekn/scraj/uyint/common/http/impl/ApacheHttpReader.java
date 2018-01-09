package com.hiekn.scraj.uyint.common.http.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.hiekn.scraj.uyint.common.http.HttpReader;
import com.hiekn.sfe4j.util.StringUtils;

/**
 * 
 * 使用apache http client作为引擎  读取静态网页源码
 *
 * @author pzn
 * @since 1.7
 * @version 1.0
 */
public class ApacheHttpReader extends CommonHttpReader implements HttpReader {
	
    CloseableHttpClient httpClient;
    RequestConfig defaultRequestConfig;
    
    public ApacheHttpReader() {
        httpClient = HttpClients.custom()//
                .setUserAgent(userAgents[new Random().nextInt(4)])//
                .build();

        defaultRequestConfig = RequestConfig.custom()//
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)// 连接超时时间
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)// 传输超时时间
                .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)// 请求超时时间
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setCircularRedirectsAllowed(false)
                .build();
    }
    
    public ApacheHttpReader(int connectTimeout, int socketTimeout, int connectionRequestTimeout) {
        httpClient = HttpClients.custom()//
                .setUserAgent(userAgents[new Random().nextInt(userAgents.length)])//
                .build();

        defaultRequestConfig = RequestConfig.custom()//
                .setConnectTimeout(connectTimeout)// 连接超时时间
                .setSocketTimeout(socketTimeout)// 传输超时时间
                .setConnectionRequestTimeout(connectionRequestTimeout)// 请求超时时间
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setCircularRedirectsAllowed(false)
                .build();
    }
    
    public ApacheHttpReader(String hostname, int port) {
        httpClient = HttpClients.custom()//
                .setUserAgent(userAgents[new Random().nextInt(3)])//
                .build();

        defaultRequestConfig = RequestConfig.custom()//
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)// 连接超时时间
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)// 传输超时时间
                .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQUEST_TIMEOUT)// 请求超时时间
                .setProxy(new HttpHost(hostname, port))// 设置代理
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setCircularRedirectsAllowed(false)
                .build();
    }
    
    public String readSource(String url, String charset, String cookie,
    		String requestType, String params) {
    	String pageSource = "";
    	
    	if (StringUtils.isNullOrEmpty(url)) return pageSource;
    	
    	// trim
    	url = url.trim();
    	
    	LOGGER.info("request url ... " + url);
    	CloseableHttpResponse httpResponse = null;
    	for (int tryTime = 0; tryTime < MAX_TRIES; tryTime++) {
    		try {
	    		if (HTTP_GET.equals(requestType)) {
	    			if (!StringUtils.isNullOrEmpty(params)) {
	    				if (url.indexOf("?") > -1) url += "&" + params;
	    				else url += "?" + params;
	    			} 
	    			
	    			HttpGet httpGet = new HttpGet(url);
	                httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	                //httpGet.addHeader("Accept-Encoding", "gzip, deflate, sdch");
	                httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
	                httpGet.addHeader("Connection", "keep-alive");
	                httpGet.addHeader("Cookie", null == cookie ? "" : cookie);
	                
	                httpGet.setConfig(defaultRequestConfig);
					httpResponse = httpClient.execute(httpGet);
	    		} else if (HTTP_POST.equals(requestType)) {
	    			
	    			HttpPost httpPost = new HttpPost(url);
	    			httpPost.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	    			//httpPost.addHeader("Accept-Encoding", "gzip, deflate, sdch");
	    			httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.6");
	    			httpPost.addHeader("Connection", "keep-alive");
					httpPost.addHeader("Cookie", null == cookie ? "" : cookie);
					
					if (!StringUtils.isNullOrEmpty(params)) {
						List<NameValuePair> nvps = new ArrayList<NameValuePair>();
						String[] ps = params.split(";");
						for (String param : ps) {
							String[] nv = param.split("=");
							nvps.add(new BasicNameValuePair(nv[0], nv[1]));
						}
						
						httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
					}
					
					httpPost.setConfig(defaultRequestConfig);
					httpResponse = httpClient.execute(httpPost);
	    		} else {
	    			//
	    			break;
	    		}
	    		
	    		
	    		//
	    		// 处理httpResponse 
	    		//
	    		int status = httpResponse.getStatusLine().getStatusCode();
                LOGGER.info("response code ... " + status);
                if (HttpStatus.SC_OK == status) {
                	//
                    HttpEntity entity = httpResponse.getEntity();
                    if (null != entity) {
                    	// httpclient默认会自动解压gzip 
                    	Header ceHeader= httpResponse.getLastHeader("Content-Encoding");
                    	if (null != ceHeader) {
                    		if (ceHeader.getValue().indexOf("gzip") > -1) {
                    			//
                    			entity = new GzipDecompressingEntity(entity);
                    		}
                    	}
                    	
                    	//
                        if (!StringUtils.isNullOrEmpty(charset)) {
                            pageSource = EntityUtils.toString(entity, charset);
                        } else {// 解析编码
                            ContentType contentType = ContentType.get(entity);
                            String contentCharset = null;
                            if (contentType != null) {
                                if (contentType.getCharset() != null) {
                                    contentCharset = contentType.getCharset().name();
                                }
                            }
                            // 服务器没有返回url指定的编码,先使用ISO-8859-1,然后从源码找查找编码
                            if (contentCharset == null) {
                                contentCharset = Consts.ISO_8859_1.name();

                                // EntityUtils.toString先在entity中找编码，没找到，使用传入的默认编码。没设置默认编码，则使用ISO_8859_1
                                pageSource = EntityUtils.toString(entity, contentCharset);
                                
                                charset = parseCharset(pageSource);//解析到的编码
                                LOGGER.info("detect " + url + " ... charset=" + charset);
                                charset = charset.equals("") ? DEFAULT_CHARSET :  charset;

                                pageSource = new String(pageSource.getBytes(contentCharset), charset);
                            } else {// 如果服务器返回时有指定编码，则不会出现乱码
                                charset = contentCharset;

                                pageSource = EntityUtils.toString(entity, charset);
                            }
                        }
                        // entity 不为null，结束循环
                        break;
                    }
                }
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    			LOGGER.error(e);
    		} catch (IOException e) {
    			e.printStackTrace();
    			LOGGER.error(e);
    		} catch (Exception e) {
    			e.printStackTrace();
    			LOGGER.error(e);
			} finally {
    			if (httpResponse != null) {
                    try {
                        httpResponse.close();
                    } catch (IOException e) {
                        LOGGER.error("IOException " + e);
                    }
                }
    		}
    	}
    	
    	LOGGER.info("request result length ... " + pageSource.length());
    	
    	return pageSource;
    }
    
    public void close() {
        if (null != httpClient) {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("close ... " + e);
            }
            httpClient = null;
        }
    }
    
}
