package com.hiekn.scraj.movert.common.act.impl;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserClickAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVsAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchListAct;
import com.hiekn.scraj.movert.common.act.BrowserHandleListAct;
import com.hiekn.scraj.movert.common.act.BrowserOpenAct;
import com.hiekn.scraj.movert.common.act.domain.BrowserKV;
import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.sfe4j.util.StringUtils;

public class BrowserHandleListActImpl implements BrowserHandleListAct {

	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> execute(Map<String, Object> paramsMap, BrowserActContext context)
			throws Exception {
		// 
		int sleepMillis = ((Double) paramsMap.get("sleepMillis")).intValue();
		List<Map<String, Object>> innerCommands = (List<Map<String, Object>>) paramsMap.get("innerCommands");
		List<Map<String, Object>> kvList = (List<Map<String, Object>>) paramsMap.get("kvList");
		
		String $refField = null;// open 引用的字段
		// 
		handleList: 
		for (Map<String, Object> kv : kvList) {
			
			//
			String currentPageSource = null;
			for (Map<String, Object> cmd : innerCommands) {
				String cmdKey = cmd.keySet().iterator().next();
				Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
				
				//
				switch (cmdKey) {
				case BROWSER_OPEN_ACT_ALIAS:
					if (StringUtils.isNullOrEmpty($refField)) {
						List<String> urls = (List<String>) params.get("urls");
						if (urls.size() > 0) {
							String refField = urls.get(0);
							$refField = refField.substring(refField.indexOf("$") + 1);
						}
					}
					if (StringUtils.isNullOrEmpty($refField)) break handleList;
					
					params.put("url", kv.get($refField));
					
					currentPageSource = mBrowserOpenAct.execute(params, context);
					break;
				case BROWSER_CLICK_ACT_ALIAS:
					mBrowserClickAct.execute(params, context);
					break;
				case BROWSER_FETCH_KV_ACT_ALIAS:
					BrowserKV browserKV = mBrowserFetchKVAct.execute(params, context);
					kv.put(browserKV.key, browserKV.value);
					break;
				case BROWSER_FETCH_KVS_ACT_ALIAS:
					Map<String, Object> fetchMap = mBrowserFetchKVsAct.execute(params, context);
					if (fetchMap.size() > 0) kv.putAll(fetchMap);;
					break;
				case BROWSER_FETCH_LIST_ACT_ALIAS:
					BrowserKV browserKV2 = mBrowserFetchListAct.execute(params, context);
					if (null == browserKV2.key) {
						kv.put(UUID.randomUUID().toString().replaceAll("-", "p"), browserKV2.value);
					} else {
						kv.put(browserKV2.key, browserKV2.value);
					}
					break;
				case STATIC_OPEN_ACT_ALIAS:
					if (StringUtils.isNullOrEmpty($refField)) {
						List<String> urls = (List<String>) params.get("urls");
						if (urls.size() > 0) {
							String refField = urls.get(0);
							$refField = refField.substring(refField.indexOf("$") + 1);
						}
					}
					if (StringUtils.isNullOrEmpty($refField)) break handleList;
					
					params.put("url", kv.get($refField));
					
					currentPageSource = mStaticOpenAct.execute(params, mStaticActContext);
					break;
				case STATIC_FETCH_KV_ACT_ALIAS:
					params.put("dependencySource", currentPageSource);
					StaticKV kvPair = mStaticFetchKVAct.execute(params, mStaticActContext);
					kv.put(kvPair.key, kvPair.value);
					break;
				case STATIC_FETCH_KVS_ACT_ALIAS:
					params.put("dependencySource", currentPageSource);
					Map<String, Object> fetchMap2 = mStaticFetchKVsAct.execute(params, mStaticActContext);
					if (fetchMap2.size() > 0) kv.putAll(fetchMap2);;
					break;
				case STATIC_FETCH_LIST_ACT_ALIAS:
					params.put("dependencySource", currentPageSource);
					StaticKV kvPair2 = mStaticFetchListAct.execute(params, mStaticActContext);
					if (null == kvPair2.key) {
						kv.put(UUID.randomUUID().toString().replaceAll("-", "p"), kvPair2.value);
					} else {
						kv.put(kvPair2.key, kvPair2.value);
					}
					break;
				}
				
			}// single kv end.
			
			// sleep
			if (sleepMillis > 0) TimeUnit.MILLISECONDS.sleep(new Random().nextInt(sleepMillis) + 1);;
			
		}// list kv end.
		
		return kvList;
	}
	
	
	public void initial(BrowserOpenAct mBrowserOpenAct, BrowserClickAct mBrowserClickAct, 
			BrowserFetchKVAct mBrowserFetchKVAct, BrowserFetchKVsAct mBrowserFetchKVsAct, 
			BrowserFetchListAct mBrowserFetchListAct, StaticActContext mStaticActContext, 
			StaticOpenAct mStaticOpenAct, StaticFetchKVAct mStaticFetchKVAct, StaticFetchKVsAct mStaticFetchKVsAct, 
			StaticFetchListAct mStaticFetchListAct) {
		this.mBrowserOpenAct = mBrowserOpenAct;
		this.mBrowserClickAct = mBrowserClickAct;
		this.mBrowserFetchKVAct = mBrowserFetchKVAct;
		this.mBrowserFetchKVsAct = mBrowserFetchKVsAct;
		this.mBrowserFetchListAct = mBrowserFetchListAct;
		
		//
		this.mStaticActContext = mStaticActContext;
		this.mStaticOpenAct = mStaticOpenAct;
		this.mStaticFetchKVAct = mStaticFetchKVAct;
		this.mStaticFetchKVsAct = mStaticFetchKVsAct;
		this.mStaticFetchListAct = mStaticFetchListAct;
	}
	
	//
	private BrowserOpenAct mBrowserOpenAct;
	private BrowserClickAct mBrowserClickAct;
	private BrowserFetchKVAct mBrowserFetchKVAct;
	private BrowserFetchKVsAct mBrowserFetchKVsAct;
	private BrowserFetchListAct mBrowserFetchListAct;
	
	//
	private StaticActContext mStaticActContext;
	private StaticOpenAct mStaticOpenAct;
	private StaticFetchKVAct mStaticFetchKVAct;
	private StaticFetchKVsAct mStaticFetchKVsAct;
	private StaticFetchListAct mStaticFetchListAct;
}
