package com.hiekn.scraj.uyint.common.act.impl;

import java.util.Map;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.http.HttpReader;

public class StaticOpenActImpl implements StaticOpenAct {
	
	@SuppressWarnings("unchecked")
	public String execute(Map<String, Object> paramsMap, StaticActContext context) throws Exception {
		String pageSource = "";
		
		String url = (String) paramsMap.get("url");
		String charset = (String) paramsMap.get("charset");
        String requestType = (String) (paramsMap.get("requestType") == null ? HttpReader.HTTP_GET : paramsMap.get("requestType"));
        String params = (String) paramsMap.get("params");
        
        if (url == null || url.equals("")) {
        	return pageSource;
        }
        pageSource = context.readHttpReader().readSource(url, charset, context.readCookie(), requestType, params);
		
        // 源码格式化
        Map<String, Object> formatter = (Map<String, Object>) paramsMap.get("formatter");
        if (null != formatter && formatter.size() > 0) {
        	pageSource = context.readFliver().doFormat(pageSource, GSON.toJson(formatter));
        }
        
		return pageSource;
	}

}
