package com.hiekn.scraj.uyint.common.http.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hiekn.scraj.uyint.common.http.HttpReader;

public abstract class CommonHttpReader implements HttpReader {
	
	// 最大尝试次数
	protected final int MAX_TRIES = 3;
    // html默认编码
	protected final String DEFAULT_CHARSET = "gbk";
    // 默认超时时间
	protected final int DEFAULT_CONNECT_TIMEOUT = 15000;
	protected final int DEFAULT_SOCKET_TIMEOUT = 15000;
	protected final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 15000;

	protected final String[] userAgents = {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.82 Safari/537.36",// chrome
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0",// firefox
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:45.0) Gecko/20100101 Firefox/45.0",// firefox
            "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko",// ie
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586"// Edge
    };
	
	
    public String readSource(String url) { return readSource(url, null); }

    public String readSource(String url, String charset) { return readSource(url, charset, null); }
    
    public String readSource(String url, String charset, String cookie) { return readSource(url, charset, cookie, HTTP_GET, null); }
    
    public String parseCharset(String pageSource) {
		// <meta charset="gb2312" />
		// <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
		// <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
		// <meta http-equiv='Content-Language' content='utf-8'/>
		Matcher charsetMatcher = Pattern.compile("(?i)<meta.*?(utf-8|gb2312|gbk|gb18030|iso-8859-1)").matcher(pageSource);
		if (charsetMatcher.find()) return charsetMatcher.group(1).toLowerCase();;
    	
        return "";
	}
    
    /**
     * use {@link #parseCharset()} instead.
     * @param pageSource
     * @return
     */
    @Deprecated
    public String parseCharset0(String pageSource) {
    	Matcher charsetMatcher;
		// <meta http-equiv="Content-Type" content="text/html" charset="UTF-8" />
		// <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
		// <meta charset="gb2312" />
    	charsetMatcher = Pattern.compile("(?i)<meta.*?charset=['\"]?(\\S+?)['\"]+?").matcher(pageSource);
    	if (charsetMatcher.find()) return charsetMatcher.group(1).toLowerCase();
        
    	// <meta http-equiv='Content-Language' content='utf-8'/>
    	charsetMatcher = Pattern.compile("(?i)<meta.*?content=['\"]?(\\S+?)['\"]+?").matcher(pageSource);
    	if (charsetMatcher.find()) return charsetMatcher.group(1).toLowerCase().replace("no-cache", "");
    	
        return "";
    }
    
}
