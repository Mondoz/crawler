package com.hiekn.scraj.movert.common.act.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVsAct;
import com.hiekn.scraj.movert.common.act.domain.BrowserKV;
import com.hiekn.sfe4j.util.StringUtils;

public class BrowserFetchKVsActImpl implements BrowserFetchKVsAct {

	@SuppressWarnings("unchecked")
	public Map<String, Object> execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		
		List<Map<String, Object>> fetchValueWithKeys = (List<Map<String, Object>>) paramsMap.get("fetchValueWithKeys");
		
		WebElement dependencyWebElement = (WebElement) paramsMap.get("dependencyWebElement");
		if (null != dependencyWebElement) {
			//
			int expectFieldSize = fetchValueWithKeys.size();// 待提取的字段个数
			int invalidFieldSize = 0;// 提取不正常的字段个数
			
			//
			for (Map<String, Object> fetchValue : fetchValueWithKeys) {
				
				// check field 
				BrowserKV kvPair = null;
				String field = (String) fetchValue.get("field");
				if (!StringUtils.isNullOrEmpty(field)) {
					try {
						fetchValue.put("dependencyWebElement", dependencyWebElement);
						kvPair = mBrowserFetchKVAct.execute(fetchValue, context);
					} catch(RuntimeException e) {
						//
						e.printStackTrace();
						LOGGER.error("RuntimeException ... " + e);
					}
				}
				//
				if (null == kvPair) invalidFieldSize++;
				else if (resultMap.containsKey(kvPair.key)) {
					resultMap.put(kvPair.key, kvPair.value + "" + resultMap.get(kvPair));
				} else {
					resultMap.put(kvPair.key, kvPair.value);
				}
			}// end of loop all props.
			
			// check field val
			if (expectFieldSize > invalidFieldSize) {// 有效字段大于无效字段
				boolean includeSource = null == paramsMap.get("includeSource") ? 
						false : ((Boolean) paramsMap.get("includeSource"));
				
				if (includeSource) resultMap.put("dependencySource", dependencyWebElement.getAttribute("outerHTML"));
			} else {// 字段全部无效  清空数据
				if (expectFieldSize > 1) resultMap.clear();
			}
		}
		
		return resultMap;
	}
	
	public void initial(BrowserFetchKVAct mBrowserFetchKVAct) {
		this.mBrowserFetchKVAct = mBrowserFetchKVAct;
	}
	
	private BrowserFetchKVAct mBrowserFetchKVAct;
	
}
