package com.hiekn.scraj.uyint.common.act.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.StaticPaginationAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.act.proxy.StaticActProxy;
import com.hiekn.sfe4j.util.StringUtils;

import us.codecraft.xsoup.Xsoup;

public class StaticFetchListActImpl implements StaticFetchListAct {

	@SuppressWarnings("unchecked")
	public StaticKV execute(Map<String, Object> paramsMap, StaticActContext context) throws Exception {
		
		//
		String dependencySource = (String) paramsMap.get("dependencySource");
		if (StringUtils.isNullOrEmpty(dependencySource)) return null;
		//
		String field = (String) paramsMap.get("field");
		
		String byType = (String) paramsMap.get("byType");
    	String typeValue = (String) paramsMap.get("typeValue");
    	
    	//
    	// 先提取列表item
    	// 然后在每个item提取具体的数据
    	//
    	List<String> cList = new ArrayList<String>();
    	switch (byType) {
		case STATIC_FETCH_BY_TYPE_XPATH:
			Elements es = Xsoup.compile(typeValue).evaluate(Jsoup.parse(dependencySource)).getElements();
    		for (int i = 0; i < es.size(); i++) {
    			cList.add(es.get(i).outerHtml());
    		}
			break;
		case STATIC_FETCH_BY_TYPE_CSS:
			Elements je = Jsoup.parse(dependencySource).select(typeValue);
			for (int i = 0; i < je.size(); i++) {
    			cList.add(je.get(i).outerHtml());
    		}
			break;
		case STATIC_FETCH_BY_TYPE_REGEX:
			Matcher m = Pattern.compile(typeValue).matcher(dependencySource);
    		while (m.find()) {
    			cList.add(m.group());
    		}
			break;
		}
		
    	//
    	String $refField = null;// open 引用的字段
    	//
    	List<Object> values = new ArrayList<Object>();
    	List<Map<String, Object>> kvalues = new ArrayList<Map<String, Object>>();
    	List<Map<String, Object>> innerCommands = (List<Map<String, Object>>) paramsMap.get("innerCommands");
    	for (int i = 0, size = cList.size(); i < size; i++) {
    		String item = cList.get(i);
    		//
    		for (Map<String, Object> cmd : innerCommands) {
    			String cmdKey = cmd.keySet().iterator().next();
        		Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
        		
        		boolean useCustom = false;
        		switch (cmdKey) {
    			case STATIC_FETCH_KV_ACT_ALIAS:
					params.put("dependencySource", item);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchKVAct> clazz = (Class<StaticFetchKVAct>) context.loadClass(className);
						StaticFetchKVAct customFetchValueAct = clazz.newInstance();
						mStaticFetchKVActProxy.act(customFetchValueAct);
					} else {
						mStaticFetchKVActProxy.act(mStaticFetchKVAct);
					}
					
					//
					StaticKV kvPair = mStaticFetchKVActProxy.execute(params, context);
					if (null == kvPair.key) {
						values.add(kvPair.value);
					} else {
						Map<String, Object> singleKeyMap = new HashMap<String, Object>();
						singleKeyMap.put(kvPair.key, kvPair.value);
						kvalues.add(singleKeyMap);
					}
					//
					break;
				case STATIC_FETCH_KVS_ACT_ALIAS:
					params.put("dependencySource", item);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchKVsAct> clazz = (Class<StaticFetchKVsAct>) context.loadClass(className);
						StaticFetchKVsAct customFetchObjectAct = clazz.newInstance();
						mStaticFetchKVsActProxy.act(customFetchObjectAct);
					} else {
						mStaticFetchKVsActProxy.act(mStaticFetchKVsAct);
					}
					Map<String, Object> multiKeyMap = mStaticFetchKVsActProxy.execute(params, context);
					if (multiKeyMap.size() > 0) kvalues.add(multiKeyMap);;
					//
					break;
				case STATIC_FETCH_LIST_ACT_ALIAS:
					params.put("dependencySource", item);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchListAct> clazz = (Class<StaticFetchListAct>) context.loadClass(className);
						StaticFetchListAct customFetchListAct = clazz.newInstance();
						mStaticFetchListActProxy.act(customFetchListAct);
					} else {
						mStaticFetchListActProxy.act(this);
					}
					StaticKV staticKV = mStaticFetchListActProxy.execute(params, context);
					List<Object> vs = (List<Object>) staticKV.value;
    				if (vs.size() > 0) {
    					Object v = vs.get(0);
    					if (null == staticKV.key) {
    						if (v instanceof Map) {
    							kvalues.addAll((List<Map<String,Object>>) staticKV.value);
    						} else {
    							values.addAll((List<Object>) staticKV.value);
    						}
    					} else {
    						Map<String, Object> hm = kvalues.get(i);
    						if (null == hm) {
    							hm = new HashMap<>();
    							kvalues.add(hm);
    						}
    						hm.put(staticKV.key, staticKV.value);
    					}
    				}
    				//
					break;
				case STATIC_PAGINATION_ACT_ALIAS:
					params.put("dependencySource", item);
					// 添加paginationurl
					if (params.get("strategy").toString().equalsIgnoreCase("rule")) {
						Map<String, Object> rule = (Map<String, Object>) params.get("strategyValue");
						List<String> paginationUrls = (List<String>) rule.get("paginationUrls");
						rule.put("paginationUrl", paginationUrls.get(0));
					}
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticPaginationAct> clazz = (Class<StaticPaginationAct>) context.loadClass(className);
						StaticPaginationAct customPagination = clazz.newInstance();
						mStaticPaginationActProxy.act(customPagination);
					} else {
						mStaticPaginationActProxy.act(mStaticPaginationAct);
					}
					
					kvalues.addAll(mStaticPaginationActProxy.execute(params, context));
					break;
				case STATIC_OPEN_ACT_ALIAS:
					// anyway, current item original value cannot use after open act, the item will reset to other value.
					// if open act get the source, will set to the item.
					// else the item value will set to null.
					item = null;
					
					if (StringUtils.isNullOrEmpty($refField)) {
						List<String> urls = (List<String>) params.get("urls");
						if (urls.size() > 0) {
							String refField = urls.get(0);
							$refField = refField.substring(refField.indexOf("$") + 1);
						}
					}//
					
					//
					if (!StringUtils.isNullOrEmpty($refField)) {
						params.put("url", kvalues.get(i).get("url"));
						
						useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
						if (useCustom) {
							String className = (String) params.get("className");
							context.loadJar("ext");
							Class<StaticOpenAct> clazz = (Class<StaticOpenAct>) context.loadClass(className);
							StaticOpenAct customOpen = clazz.newInstance();
							mStaticOpenActProxy.act(customOpen);
						} else {
							mStaticOpenActProxy.act(mStaticOpenAct);
						}
						
						item = mStaticOpenActProxy.execute(params, context);
					}
					break;
				}
        		
    		}
    	}// end of list items
		
    	
    	StaticKV kvPair;
    	if (StringUtils.isNullOrEmpty(field)) {
    		if (kvalues.size() > 0) kvPair = new StaticKV(kvalues);
    		else kvPair = new StaticKV(values);
    	} else {
    		if (kvalues.size() > 0) kvPair = new StaticKV(field, kvalues);
    		else kvPair = new StaticKV(field, values);
    	}
    	
		return kvPair;
	}
	
	public void initial(StaticPaginationAct mStaticPaginationAct, StaticFetchKVsAct mStaticFetchKVsAct,
			StaticFetchKVAct mStaticFetchKVAct, StaticOpenAct mStaticOpenAct) {
		this.mStaticPaginationAct = mStaticPaginationAct;
		this.mStaticFetchKVsAct = mStaticFetchKVsAct;
		this.mStaticFetchKVAct = mStaticFetchKVAct;
		this.mStaticOpenAct = mStaticOpenAct;
		
		//
		//
		//
		mStaticPaginationActProxy = new StaticActProxy<>();
		mStaticFetchListActProxy = new StaticActProxy<>();
		mStaticFetchKVsActProxy = new StaticActProxy<>();
		mStaticFetchKVActProxy = new StaticActProxy<>();
		mStaticOpenActProxy = new StaticActProxy<>();
	}
	
	private StaticPaginationAct mStaticPaginationAct;
	private StaticFetchKVsAct mStaticFetchKVsAct;
	private StaticFetchKVAct mStaticFetchKVAct;
	private StaticOpenAct mStaticOpenAct;
	
	//
	private StaticActProxy<Map<String, Object>, List<Map<String, Object>>> mStaticPaginationActProxy;
	private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchListActProxy;
	private StaticActProxy<Map<String, Object>, Map<String, Object>> mStaticFetchKVsActProxy;
	private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchKVActProxy;
	private StaticActProxy<Map<String, Object>, String> mStaticOpenActProxy;
	
}
