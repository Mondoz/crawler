package com.hiekn.scraj.movert.common.act.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVsAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchListAct;
import com.hiekn.scraj.movert.common.act.domain.BrowserKV;
import com.hiekn.sfe4j.util.StringUtils;

public class BrowserFetchListActImpl implements BrowserFetchListAct {

	@SuppressWarnings("unchecked")
	public BrowserKV execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		
		String field = (String) paramsMap.get("field");
		List<Object> values = new ArrayList<Object>();
		List<Map<String, Object>> kvalues = new ArrayList<Map<String, Object>>();
		//
		String byType = (String) paramsMap.get("byType");
    	String typeValue = (String) paramsMap.get("typeValue");
    	List<Map<String, Object>> innerCommands = (List<Map<String, Object>>) paramsMap.get("innerCommands");
    	
    	int actualListIndex = (Integer) (paramsMap.get("actualListIndex") == null ? 0 : paramsMap.get("actualListIndex"));
		
    	//
    	List<WebElement> items = new ArrayList<WebElement>();
    	WebElement dependencyWebElement = (WebElement) paramsMap.get("dependencyWebElement");
		RemoteWebDriver driver = context.readWebDriverProxy().webDriver();
		switch (byType) {
		case BROWSER_FETCH_BY_TYPE_ID:
			if (null == dependencyWebElement) items.addAll(driver.findElementsById(typeValue));
			else items.addAll(dependencyWebElement.findElements(By.id(typeValue)));
			break;
		case BROWSER_FETCH_BY_TYPE_CLASS:
			if (null == dependencyWebElement) items.addAll(driver.findElementsByClassName(typeValue));
			else items.addAll(dependencyWebElement.findElements(By.className(typeValue)));
			break;
		case BROWSER_FETCH_BY_TYPE_CSS:
			if (null == dependencyWebElement) {
				items.addAll(driver.findElementsByCssSelector(typeValue));
			} else {
				items.addAll(dependencyWebElement.findElements(By.cssSelector(typeValue)));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_TAGNAME:
			if (null == dependencyWebElement) {
				items.addAll(driver.findElementsByTagName(typeValue));
			} else {
				items.addAll(dependencyWebElement.findElements(By.tagName(typeValue)));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_NAME:
			if (null == dependencyWebElement) {
				items.addAll(driver.findElementsByName(typeValue));
			} else {
				items.addAll(dependencyWebElement.findElements(By.name(typeValue)));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_LINK_TEXT:
			if (null == dependencyWebElement) {
				items.addAll(driver.findElementsByLinkText(typeValue));
			} else {
				items.addAll(dependencyWebElement.findElements(By.linkText(typeValue)));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_PARTIAL_LINK_TEXT:
			if (null == dependencyWebElement) {
				items.addAll(driver.findElementsByPartialLinkText(typeValue));
			} else {
				items.addAll(dependencyWebElement.findElements(By.partialLinkText(typeValue)));
			}
			break;
		case BROWSER_FETCH_BY_TYPE_XPATH:
			if (null == dependencyWebElement) {
				items.addAll(driver.findElementsByXPath(typeValue));
			} else {
				items.addAll(dependencyWebElement.findElements(By.xpath(typeValue)));
			}
			break;
		}
		
		
		if (items.size() > 0) {
			if (actualListIndex > 0) items = items.subList(actualListIndex, items.size());
			//
        	// 循环处理每一个元素
			WebElement item = null;
			for (int i = 0, size = items.size(); i < size; i++) {
				item = items.get(i);
        		// 列表单个元素处理
        		for (Map<String, Object> cmd : innerCommands) {
            		String cmdKey = cmd.keySet().iterator().next();
            		Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
        			switch (cmdKey) {
        			case BROWSER_FETCH_KV_ACT_ALIAS:
        				params.put("dependencyWebElement", item);
        				BrowserKV kvPair = mBrowserFetchKVAct.execute(params, context);
        				// 单个值
        				if (null == kvPair.key) {
        					values.add(kvPair.value);
        				} else {
        					Map<String, Object> kv = new HashMap<String, Object>();
        					kv.put(kvPair.key, kvPair.value);
        					kvalues.add(kv);
        				}
        				break;
        			case BROWSER_FETCH_KVS_ACT_ALIAS:
        				params.put("dependencyWebElement", item);
        				Map<String, Object> kvs = mBrowserFetchKVsAct.execute(params, context);
        				if (kvs.size() > 0) kvalues.add(kvs);
        				break;
        			case BROWSER_FETCH_LIST_ACT_ALIAS:
        				params.put("dependencyWebElement", item);
        				BrowserKV kv = execute(params, context);
        				List<Object> vs = (List<Object>) kv.value;
        				if (vs.size() > 0) {
        					Object v = vs.get(0);
        					if (null == kv.key) {
        						if (v instanceof Map) {
        							kvalues.addAll((List<Map<String,Object>>) kv.value);
        						} else {
        							values.addAll((List<Object>) kv.value);
        						}
        					} else {
        						Map<String, Object> hm = kvalues.get(i);
        						if (null == hm) {
        							hm = new HashMap<>();
        							kvalues.add(hm);
        						}
        						hm.put(kv.key, kv.value);
        					}
        				}
        				break;
        			}
            	}// 单个元素执行完所有命令
        		//
        	}// 列表处理完成
		}
		
		//
		BrowserKV kvPair;
    	if (StringUtils.isNullOrEmpty(field)) {
    		if (kvalues.size() > 0) kvPair = new BrowserKV(kvalues);
    		else kvPair = new BrowserKV(values);
    	} else {
    		if (kvalues.size() > 0) kvPair = new BrowserKV(field, kvalues);
    		else kvPair = new BrowserKV(field, values);
    	}
    	
		return kvPair;
	}
	
	
	public void initial(BrowserFetchKVAct mBrowserFetchKVAct, 
			BrowserFetchKVsAct mBrowserFetchKVsAct) {
		this.mBrowserFetchKVAct = mBrowserFetchKVAct;
		this.mBrowserFetchKVsAct = mBrowserFetchKVsAct;
	}
	
	private BrowserFetchKVAct mBrowserFetchKVAct;
	private BrowserFetchKVsAct mBrowserFetchKVsAct;
	
}
