package com.hiekn.scraj.rest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticHandleListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.StaticPaginationAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVsActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchListActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticHandleListActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticOpenActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticPaginationActImpl;
import com.hiekn.scraj.uyint.common.act.proxy.StaticActProxy;
import com.hiekn.scraj.uyint.common.core.ApacheActor;
import com.hiekn.scraj.uyint.common.http.impl.ApacheHttpReader;
import com.hiekn.sfe4j.core.impl.FliverImpl;

public class StaticPreviewService {
	
	public static final Object preview(String conf) throws Exception {
		Object result = null;
		PreviewActor previewActor = new PreviewActor();
		try {
			result = previewActor.perform(conf);
		} finally {
			previewActor.actContext().cleanup();;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	static class PreviewActor {
		
		public final Object perform(String commandJson) throws Exception {
			//
			Object result = null;
			//
			List<Map<String, Object>> commands = StaticAct.GSON.fromJson(commandJson, new TypeToken<List<Map<String, Object>>>() {}.getType());
			// 检查并设置afterPaginationCommands
			boolean hasPagination = false;
			Map<String, Object> paginationCommandParam = null;// 翻页参数
			for (Map<String, Object> command : commands) {
				if (command.containsKey("staticPagination")) {
					paginationCommandParam = (Map<String, Object>) command.get("staticPagination");
					hasPagination = true;;
				}
			}
			
			Map<String, Object> openParam = (Map<String, Object>) commands.get(0).get("staticOpen");
			List<String> urls = (List<String>) openParam.remove("urls");
			boolean paginationUseRule = false;// 是否使用规则翻页
			Map<String, Object> rule = null;
			List<String> paginationUrls = null;
			if (hasPagination) {
				Map<String, Object> paginationMeta = (Map<String, Object>) paginationCommandParam.get("paginationMeta");
				String strategy = (String) paginationMeta.get("strategy");
				if ("rule".equalsIgnoreCase(strategy)) {
					paginationUseRule = true;
					rule = paginationMeta;
				}
				
				if (paginationUseRule) {
					paginationUrls = (List<String>) rule.remove("paginationUrls");
				}
			}
			
			openParam.put("url", urls.get(0));
			if (paginationUseRule) rule.put("paginationUrl", paginationUrls.get(0));
			result = performCommands(commands);
			
			return result;
		}
		
		public final Object performCommands(List<Map<String, Object>> commands) throws Exception {
			
			Object result = null;
			
			String dependencySource = null;
			List<Map<String, Object>> kvList = new ArrayList<Map<String, Object>>();
			
			for (Map<String, Object> cmd : commands) {
				String cmdKey = cmd.keySet().iterator().next();
				Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
				
				boolean useCustom = false;
				switch (cmdKey) {
				case StaticAct.STATIC_OPEN_ACT_ALIAS:
					LOGGER.info("#############open act init#############");
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						mStaticActContext.loadJar("ext");
						Class<StaticOpenAct> clazz = (Class<StaticOpenAct>) mStaticActContext.loadClass(className);
						StaticOpenAct customOpen = clazz.newInstance();
						mStaticOpenActProxy.act(customOpen);
					} else {
						mStaticOpenActProxy.act(mStaticOpenAct);
					}
					dependencySource = mStaticOpenActProxy.execute(params, mStaticActContext);
					result = dependencySource;
					LOGGER.info("#############open act done#############");
					break;
				case StaticAct.STATIC_PAGINATION_ACT_ALIAS:
					LOGGER.info("#############pagination act init#############");
					params.put("dependencySource", dependencySource);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						mStaticActContext.loadJar("ext");
						Class<StaticPaginationAct> clazz = (Class<StaticPaginationAct>) mStaticActContext.loadClass(className);
						StaticPaginationAct customPagination = clazz.newInstance();
						mStaticPaginationActProxy.act(customPagination);
					} else {
						mStaticPaginationActProxy.act(mStaticPaginationAct);
					}
					
					int currentPage = params.get("currentPage") == null ? 1 : ((Double) params.get("currentPage")).intValue();// 起始页
			    	int maxPage = ((Double) params.get("maxPage")).intValue() + currentPage - 1;
			    	if (maxPage > 3 || maxPage < 1) params.put("maxPage", 3.0);
					
					kvList = mStaticPaginationActProxy.execute(params, mStaticActContext);
					
					result = kvList;
					LOGGER.info("#############pagination act init#############");
					break;
				case StaticAct.STATIC_FETCH_KV_ACT_ALIAS:
					LOGGER.info("#############fetchValue act init#############");
					params.put("dependencySource", dependencySource);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						mStaticActContext.loadJar("ext");
						Class<StaticFetchKVAct> clazz = (Class<StaticFetchKVAct>) mStaticActContext.loadClass(className);
						StaticFetchKVAct customFetchValueAct = clazz.newInstance();
						mStaticFetchKVActProxy.act(customFetchValueAct);
					} else {
						mStaticFetchKVActProxy.act(mStaticFetchKVAct);
					}
					StaticKV kvPair = mStaticFetchKVActProxy.execute(params, mStaticActContext);
					Map<String, Object> singleKeyMap = new HashMap<String, Object>();
					singleKeyMap.put(kvPair.key, kvPair.value);
					kvList.add(singleKeyMap);
					
					result = kvList;
					LOGGER.info("#############fetchValue act init#############");
					break;
				case StaticAct.STATIC_FETCH_KVS_ACT_ALIAS:
					LOGGER.info("#############fetchObject act init#############");
					params.put("dependencySource", dependencySource);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						mStaticActContext.loadJar("ext");
						Class<StaticFetchKVsAct> clazz = (Class<StaticFetchKVsAct>) mStaticActContext.loadClass(className);
						StaticFetchKVsAct customFetchObjectAct = clazz.newInstance();
						mStaticFetchKVsActProxy.act(customFetchObjectAct);
					} else {
						mStaticFetchKVsActProxy.act(mStaticFetchKVsAct);
					}
					Map<String, Object> multiKeyMap = mStaticFetchKVsActProxy.execute(params, mStaticActContext);
					if (multiKeyMap.size() > 0) kvList.add(multiKeyMap);;
					
					result = kvList;
					LOGGER.info("#############fetchObject act init#############");
					break;
				case StaticAct.STATIC_FETCH_LIST_ACT_ALIAS:
					LOGGER.info("#############fetchList act init#############");
					params.put("dependencySource", dependencySource);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						mStaticActContext.loadJar("ext");
						Class<StaticFetchListAct> clazz = (Class<StaticFetchListAct>) mStaticActContext.loadClass(className);
						StaticFetchListAct customFetchListAct = clazz.newInstance();
						mStaticFetchListActProxy.act(customFetchListAct);
					} else {
						mStaticFetchListActProxy.act(mStaticFetchListAct);
					}
					StaticKV kv = mStaticFetchListActProxy.execute(params, mStaticActContext);
					if (null == kv.key) {
						kvList.addAll((List<Map<String,Object>>) kv.value);
					} else {
						Map<String, Object> hm = new HashMap<>();
						hm.put(kv.key, kv.value);
						kvList.add(hm);
					}
					
					result = kvList;
					LOGGER.info("#############fetchList act init#############");
					break;
				case StaticAct.STATIC_HANDLE_LIST_ACT_ALIAS:
					LOGGER.info("#############handleList act init#############");
					params.put("kvList", kvList);
					useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
					if (useCustom) {
						String className = (String) params.get("className");
						mStaticActContext.loadJar("ext");
						Class<StaticHandleListAct> clazz = (Class<StaticHandleListAct>) mStaticActContext.loadClass(className);
						StaticHandleListAct customHandleListAct = clazz.newInstance();
						mStaticHandleListActProxy.act(customHandleListAct);
					} else {
						mStaticHandleListActProxy.act(mStaticHandleListAct);
					}
					
					// 取5个测试
					List<Map<String, Object>> testList = null;
					if (kvList.size() > 5) testList = kvList.subList(0, 5);
					else testList = kvList;
					//
					params.put("kvList", testList);
					
					kvList = mStaticHandleListActProxy.execute(params, mStaticActContext);
					
					result = testList;
					LOGGER.info("#############handleList act init#############");
					break;
				}
				
			}
			
			// 写入采集数量
			return result;
		}
		
		public final StaticActContext actContext() {
			return mStaticActContext;
		}
		
		public PreviewActor() {
			this.mStaticActContext = new StaticActContext();
			
			this.mStaticOpenAct = new StaticOpenActImpl();
			this.mStaticPaginationAct = new StaticPaginationActImpl();
			this.mStaticFetchKVAct = new StaticFetchKVActImpl();
			this.mStaticFetchKVsAct = new StaticFetchKVsActImpl();
			this.mStaticFetchListAct = new StaticFetchListActImpl();
			this.mStaticHandleListAct = new StaticHandleListActImpl();
			
			// 内部循环翻页
			this.innerStaticPaginationAct = new StaticPaginationActImpl();
			
			//
			//
			//
			mStaticOpenActProxy = new StaticActProxy<>();
			mStaticPaginationActProxy = new StaticActProxy<>();
			mStaticFetchKVActProxy = new StaticActProxy<>();
			mStaticFetchKVsActProxy = new StaticActProxy<>();
			mStaticFetchListActProxy = new StaticActProxy<>();
			mStaticHandleListActProxy = new StaticActProxy<>();
			//
			//
			
			
			// 内层act
			initInnerAct();
			
			// 初始化成员act  最外层的act
			intialAct();
			
			// 
			mStaticActContext.writeHttpReader(new ApacheHttpReader());
			mStaticActContext.writeFliver(new FliverImpl());
			
		}
		
		private void initInnerAct() {
			innerStaticPaginationAct.initial(mStaticFetchKVAct, mStaticFetchKVsAct, mStaticFetchListAct);
		}
		
		private void intialAct() {
			mStaticFetchKVsAct.initial(mStaticFetchKVAct);
			mStaticFetchListAct.initial(innerStaticPaginationAct, mStaticFetchKVsAct, mStaticFetchKVAct, mStaticOpenAct);
			mStaticPaginationAct.initial(mStaticFetchKVAct, mStaticFetchKVsAct, mStaticFetchListAct);
			mStaticHandleListAct.initial(mStaticOpenAct, mStaticFetchKVAct, mStaticFetchKVsAct, mStaticFetchListAct);
			
			// add listener
		}
		
		
		private StaticActContext mStaticActContext;
		
		private StaticOpenAct mStaticOpenAct;
		private StaticPaginationAct mStaticPaginationAct;
		private StaticFetchKVAct mStaticFetchKVAct;
		private StaticFetchKVsAct mStaticFetchKVsAct;
		private StaticFetchListAct mStaticFetchListAct;
		private StaticHandleListAct mStaticHandleListAct;
		
		// 内部有循环分页
		// 最好新建一个对象   因为这里的循环翻页已经绑定了一个监听器   也会作用到内部  最好用个不带监听器的循环翻页
		private StaticPaginationAct innerStaticPaginationAct;
		
		//
		//
		private StaticActProxy<Map<String, Object>, String> mStaticOpenActProxy;
		private StaticActProxy<Map<String, Object>, List<Map<String, Object>>> mStaticPaginationActProxy;
		private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchKVActProxy;
		private StaticActProxy<Map<String, Object>, Map<String, Object>> mStaticFetchKVsActProxy;
		private StaticActProxy<Map<String, Object>, StaticKV> mStaticFetchListActProxy;
		private StaticActProxy<Map<String, Object>, List<Map<String, Object>>> mStaticHandleListActProxy;
		//
		//
		static final Logger LOGGER = Logger.getLogger(ApacheActor.class);
	}
}
