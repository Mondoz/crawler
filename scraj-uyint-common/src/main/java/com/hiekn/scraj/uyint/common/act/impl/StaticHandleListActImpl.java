package com.hiekn.scraj.uyint.common.act.impl;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticHandleListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.act.proxy.StaticActProxy;
import com.hiekn.sfe4j.util.StringUtils;

public class StaticHandleListActImpl implements StaticHandleListAct {
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> execute(Map<String, Object> paramsMap, StaticActContext context) throws Exception {
		//
		int sleepMillis = ((Double) paramsMap.get("sleepMillis")).intValue();
		List<Map<String, Object>> innerCommands = (List<Map<String, Object>>) paramsMap.get("innerCommands");
		
		// 子命令个数
		int numOfInnerCommands = innerCommands.size();
		
		// 待处理的列表
		List<Map<String, Object>> kvList = (List<Map<String, Object>>) paramsMap.get("kvList");
		
		String $refField = null;// open 引用的字段
		handleList:
		for (int i = 0, len = kvList.size(); i < len; i++) {
			Map<String, Object> kv = kvList.get(i);
			//
			String currentPageSource = null;
			int exCommands = 0;// 结果异常的 命令个数
			for (Map<String, Object> cmd : innerCommands) {
				String cmdKey = cmd.keySet().iterator().next();
				Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
				
				boolean useCustom = false;
				switch (cmdKey) {
				case STATIC_OPEN_ACT_ALIAS:
					if (StringUtils.isNullOrEmpty($refField)) {
						List<String> urls = (List<String>) params.get("urls");
						if (urls.size() > 0) {
							String refField = urls.get(0);
							$refField = refField.substring(refField.indexOf("$") + 1);
						}
					}//
					
					if (StringUtils.isNullOrEmpty($refField)) break handleList;
					
					params.put("url", kv.get($refField));
					
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
					currentPageSource = mStaticOpenActProxy.execute(params, context);
					//
					break;
				case STATIC_FETCH_KV_ACT_ALIAS:
					params.put("dependencySource", currentPageSource);
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
					StaticKV kvPair = mStaticFetchKVActProxy.execute(params, context);
					kv.put(kvPair.key, kvPair.value);
					// 不正常结果
					if (null == kvPair.value) exCommands++;
					break;
				case STATIC_FETCH_KVS_ACT_ALIAS:
					params.put("dependencySource", currentPageSource);
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
					if (multiKeyMap.size() > 0) kv.putAll(multiKeyMap);
					else exCommands++;
					break;
				case STATIC_FETCH_LIST_ACT_ALIAS:
					params.put("dependencySource", currentPageSource);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						context.loadJar("ext");
						Class<StaticFetchListAct> clazz = (Class<StaticFetchListAct>) context.loadClass(className);
						StaticFetchListAct customFetchListAct = clazz.newInstance();
						mStaticFetchListActProxy.act(customFetchListAct);
					} else {
						mStaticFetchListActProxy.act(mStaticFetchListAct);
					}
					
					StaticKV pair = mStaticFetchListActProxy.execute(params, context);
					if (null == pair.key) {
						kv.put(UUID.randomUUID().toString().replaceAll("-", "p"), pair.value);
					} else {
						kv.put(pair.key, pair.value);
					}
					
					// 不正常结果
					if (null == pair.value) exCommands++;
					break;
				}
				
			}// each item all inner command end.
			
			// 除了open以外
			// 其他子命令都有异常情况
			// 检查是否是因为cookie失效导致的
			if (exCommands == numOfInnerCommands - 1) {
				if (context.hasCookie()) {
					// 重置cookie以后  再次请求这个url
					context.resetCookie();
					// ***** 一定要-1  才能继续处理上次失败的url
					i--;
				}
			}
			
			// sleep
			if (sleepMillis > 0) TimeUnit.MILLISECONDS.sleep(new Random().nextInt(sleepMillis) + 1);;
			
		}// handle list loop end.
		
		return kvList;
	}
	
	public void initial(StaticOpenAct mStaticOpenAct, StaticFetchKVAct mStaticFetchKVAct, StaticFetchKVsAct mStaticFetchKVsAct,
			StaticFetchListAct mStaticFetchListAct) {
		this.mStaticOpenAct = mStaticOpenAct;
		this.mStaticFetchKVAct = mStaticFetchKVAct;
		this.mStaticFetchKVsAct = mStaticFetchKVsAct;
		this.mStaticFetchListAct = mStaticFetchListAct;
		
		//
		//
		//
		this.mStaticOpenActProxy = new StaticActProxy<>();
		this.mStaticFetchKVActProxy = new StaticActProxy<>();
		this.mStaticFetchKVsActProxy = new StaticActProxy<>();
		this.mStaticFetchListActProxy = new StaticActProxy<>();
	}
	
	private StaticOpenAct mStaticOpenAct;
	private StaticFetchKVAct mStaticFetchKVAct;
	private StaticFetchKVsAct mStaticFetchKVsAct;
	private StaticFetchListAct mStaticFetchListAct;
	
	
	private StaticActProxy<Map<String, Object>, String> mStaticOpenActProxy;
	private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchKVActProxy;
	private StaticActProxy<Map<String, Object>, Map<String, Object>> mStaticFetchKVsActProxy;
	private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchListActProxy;

}
