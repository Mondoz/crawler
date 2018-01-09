package com.hiekn.scraj.uyint.common.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticDedupAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticHandleListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.StaticPaginationAct;
import com.hiekn.scraj.uyint.common.act.StaticStoreDataAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVsActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchListActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticHandleListActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticOpenActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticPaginationActImpl;
import com.hiekn.scraj.uyint.common.act.proxy.StaticActProxy;
import com.hiekn.scraj.uyint.common.act.util.PaginationUtils;
import com.hiekn.scraj.uyint.common.http.impl.ApacheHttpReader;
import com.hiekn.sfe4j.core.impl.FliverImpl;

@SuppressWarnings({"unchecked" })
public class ApacheActor implements StaticPaginationListener {
	
	//
	public ApacheActor(StaticDedupAct mStaticDedupAct, StaticStoreDataAct mStaticStoreDataAct) {
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
		mStaticDedupActProxy = new StaticActProxy<>();
		mStaticStoreDataActProxy = new StaticActProxy<>();
		//
		//
		
		// pagination 后面的act
		afterPaginationCommands = new ArrayList<Map<String, Object>>();
		
		// 内层act
		initInnerAct();
		
		// 初始化成员act  最外层的act
		intialAct();
		
		// 
		mStaticActContext.writeStaticDedupAct(mStaticDedupAct);;
		mStaticActContext.writeStaticStoreDataAct(mStaticStoreDataAct);
		mStaticActContext.writeHttpReader(new ApacheHttpReader());
		mStaticActContext.writeFliver(new FliverImpl());
		
	}
	
	/**
	 * 获取该actor相关的actContext
	 * @return
	 */
	public final StaticActContext actContext() {
		return mStaticActContext;
	}
	
	/**
	 * 1. 关闭HttpReader 清空相关cookies<br>
	 * 
	 * 2. 重新初始化一个新的HttpReader
	 */
	public final void reset() {
		mStaticActContext.cleanup();
		mStaticActContext.writeHttpReader(new ApacheHttpReader());
	}
	
	public final void resetContextHttpReader() {
		mStaticActContext.resetHttpReader(new ApacheHttpReader());
	}
	
	
	/**
	 * 
	 * @param taskId
	 * @param taskName
	 * @param parallel 0 不并行  1url级别并行  2翻页级别并行
	 * @param url
	 * @param paginationUrl
	 * @param paginationRule
	 * @param currentPage
	 * @param commandJson
	 * @return
	 * @throws Exception
	 */
	public final int perform(int taskId, 
			String taskName, 
			int parallel,
			String commandJson) throws Exception {
		
		long beginMillis = System.currentTimeMillis();
		// 写入taskId
		mStaticActContext.writeTaskId(taskId);
		//
		LOGGER.info("execute task init... taskInfo: {taskId: " + taskId + ", taskName: " + taskName + "},  begin timestamp: " + beginMillis);
		List<Map<String, Object>> commands = StaticAct.GSON.fromJson(commandJson, new TypeToken<List<Map<String, Object>>>() {}.getType());
		
		// 检查并设置afterPaginationCommands
		boolean hasPagination = false;
		Map<String, Object> paginationParams = null;// 翻页参数
		afterPaginationCommands.clear();
		for (Map<String, Object> command : commands) {
			if (hasPagination) afterPaginationCommands.add(command);;
			if (command.containsKey("staticPagination")) {
				paginationParams = (Map<String, Object>) command.get("staticPagination");
				hasPagination = true;;
			}
		}
		
		//
		// 并行大于1  并且 有翻页参数可能需要重新计算url
		//
		if (parallel > 1 && hasPagination) {// 第二级别并行
			if (hasPagination) {
				boolean paginationUseRule = false;// 是否使用规则翻页
				Map<String, Object> rule = null;
				Map<String, Object> paginationMeta = (Map<String, Object>) paginationParams.get("paginationMeta");
				String strategy = (String) paginationMeta.get("strategy");
				if ("rule".equalsIgnoreCase(strategy)) {
					paginationUseRule = true;
					rule = paginationMeta;
				}
				
				if (paginationUseRule) {
					int currentPage = (int) Double.parseDouble((null == paginationParams.get("currentPage") ? "1" : paginationParams.get("currentPage")).toString());
					if (currentPage > 1) {
						
						String paginationUrl = (String) rule.get("paginationUrl");
						String paginationRule = (String) rule.get("paginationRule");
						int stdLength = (int) ((double) rule.get("stdLength"));
						String leftAlign = (String) rule.get("leftAlign");
						String rightAlign = (String) rule.get("rightAlign");
						
						String url = PaginationUtils.generateNextPageUrl(paginationUrl, paginationRule, --currentPage + "", "", stdLength, leftAlign, rightAlign);
						
						// replace open url
						((Map<String, Object>) commands.get(0).get("staticOpen")).put("url", url);
					}
				}
			}
			
			//
			performCommands(commands);
			
		} else if (parallel < 1) {// 不并行
			Map<String, Object> openParam = (Map<String, Object>) commands.get(0).get("staticOpen");
			List<String> urls = (List<String>) openParam.remove("urls");
			boolean paginationUseRule = false;// 是否使用规则翻页
			List<String> paginationUrls = null;
			Map<String, Object> rule = null;
			if (hasPagination) {
				Map<String, Object> paginationMeta = (Map<String, Object>) paginationParams.get("paginationMeta");
				String strategy = (String) paginationMeta.get("strategy");
				if ("rule".equalsIgnoreCase(strategy)) {
					paginationUseRule = true;
					rule = paginationMeta;
					paginationUrls = (List<String>) rule.remove("paginationUrls");
				}
			}
			
			for (int i = 0, len = urls.size(); i < len; i++) {
				openParam.put("url", urls.get(i));
				if (paginationUseRule) rule.put("paginationUrl", paginationUrls.get(i));;
				performCommands(commands);
			}
			
		} else {// 第一级别并行
			// just execute.
			performCommands(commands);
		}
		
		long endMillis = System.currentTimeMillis();
		LOGGER.info("execute task done... taskInfo: {taskId: " + taskId + ", taskName: " + taskName + "},"
				+ "  end timestamp: " + endMillis + ", cost milliseconds: " + (endMillis - beginMillis)
				+ ", acquisite size: " + mStaticActContext.readAcquisiteSize());
		
		return mStaticActContext.readAcquisiteSize();
	}
	
	public final void performCommands(List<Map<String, Object>> commands) throws Exception {
		
		int size = 0;
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
				kvList = mStaticPaginationActProxy.execute(params, mStaticActContext);
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
				if (multiKeyMap.size() > 0) kvList.add(multiKeyMap);
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
				
				StaticKV pair = mStaticFetchListActProxy.execute(params, mStaticActContext);
				if (null == pair.key) {
					kvList.addAll((List<Map<String, Object>>) pair.value);
				} else {
					Map<String, Object> temp = new HashMap<>();
					temp.put(pair.key, pair.value);
					kvList.add(temp);
				}
				LOGGER.info("#############fetchList act init#############");
				break;
			case StaticAct.STATIC_DEDUP_ACT_ALIAS:
				LOGGER.info("#############dedup act init#############");
				params.put("kvList", kvList);
				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
				if (useCustom) {
					String className = (String) params.get("className");
					mStaticActContext.loadJar("ext");
					Class<StaticDedupAct> clazz = (Class<StaticDedupAct>) mStaticActContext.loadClass(className);
					StaticDedupAct customDedupAct = clazz.newInstance();
					mStaticDedupActProxy.act(customDedupAct);
				} else {
					mStaticDedupActProxy.act(mStaticActContext.readStaticDedupAct());
				}
				kvList = mStaticDedupActProxy.execute(params, mStaticActContext);
				LOGGER.info("#############dedup act init#############");
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
				kvList = mStaticHandleListActProxy.execute(params, mStaticActContext);
				LOGGER.info("#############handleList act init#############");
				break;
			case StaticAct.STATIC_STORE_ACT_ALIAS:
				LOGGER.info("#############storeData act init#############");
				// 转mapping
				List<String> fields = (List<String>) params.get("fields");
    			List<String> mappingFields = (List<String>) params.get("mappingFields");
    			if (null != fields && null != mappingFields && mappingFields.size() > 0 && fields.size() == mappingFields.size()) {
    				for (Map<String, Object> map : kvList) {
    					for (int i = 0, len = fields.size(); i < len; i++) {
    						map.put(mappingFields.get(i), map.remove(fields.get(i)));
    					}
    				}
    			}
    			// 添加常量字段
    			int taskId = mStaticActContext.readTaskId();
    			for (Map<String, Object> map : kvList) {
    				map.put("task_id", taskId);
    				map.put("channel", "");
    				map.put("source_group", "1000100000000");
    				map.put("persist_time_mills", System.currentTimeMillis());
    				map.put("persist_date", SM_DAY_FORMAT.format(new Date()));
    				map.put("crawl_time", SM_DATE_FORMAT.format(new Date()));
//    				map.put("author", "");
//    				map.put("search_word", "");
    				map.put("sentiment", -1);
    				map.put("simhash", -1L);
    				map.put("upsert", -1);
    				map.put("indexed", 0);
    			}
				params.put("kvList", kvList);
				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
				if (useCustom) {
					String className = (String) params.get("className");
					mStaticActContext.loadJar("ext");
					Class<StaticStoreDataAct> clazz = (Class<StaticStoreDataAct>) mStaticActContext.loadClass(className);
					StaticStoreDataAct customStoreDataAct = clazz.newInstance();
					mStaticStoreDataActProxy.act(customStoreDataAct);
				} else {
					mStaticStoreDataActProxy.act(mStaticActContext.readStaticStoreDataAct());
				}
				size = mStaticStoreDataActProxy.execute(params, mStaticActContext);
				LOGGER.info("#############storeData act init#############");
				break;
			}
			
    	}
    	 
    	// 写入采集数量
    	if (size > 0) mStaticActContext.writeAcquisiteSize(size);
	}
	
	
	public final void performAfterPaginationCommands(List<Map<String, Object>> kvList) throws Exception {
		
		int size = 0;
		String dependencySource = null;
		
		for (Map<String, Object> cmd : afterPaginationCommands) {
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
				kvList = mStaticPaginationActProxy.execute(params, mStaticActContext);
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
				if (multiKeyMap.size() > 0) kvList.add(multiKeyMap);
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
				LOGGER.info("#############fetchList act init#############");
				break;
			case StaticAct.STATIC_DEDUP_ACT_ALIAS:
				LOGGER.info("#############dedup act init#############");
				params.put("kvList", kvList);
				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
				if (useCustom) {
					String className = (String) params.get("className");
					mStaticActContext.loadJar("ext");
					Class<StaticDedupAct> clazz = (Class<StaticDedupAct>) mStaticActContext.loadClass(className);
					StaticDedupAct customDedupAct = clazz.newInstance();
					mStaticDedupActProxy.act(customDedupAct);
				} else {
					mStaticDedupActProxy.act(mStaticActContext.readStaticDedupAct());
				}
				kvList = mStaticDedupActProxy.execute(params, mStaticActContext);
				LOGGER.info("#############dedup act init#############");
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
				kvList = mStaticHandleListActProxy.execute(params, mStaticActContext);
				LOGGER.info("#############handleList act init#############");
				break;
			case StaticAct.STATIC_STORE_ACT_ALIAS:
				LOGGER.info("#############storeData act init#############");
				// 转mapping
				List<String> fields = (List<String>) params.get("fields");
    			List<String> mappingFields = (List<String>) params.get("mappingFields");
    			if (null != fields && null != mappingFields && mappingFields.size() > 0 && fields.size() == mappingFields.size()) {
    				for (Map<String, Object> map : kvList) {
    					for (int i = 0, len = fields.size(); i < len; i++) {
    						map.put(mappingFields.get(i), map.remove(fields.get(i)));
    					}
    				}
    			}
    			// 添加常量字段
    			int taskId = mStaticActContext.readTaskId();
    			for (Map<String, Object> map : kvList) {
    				map.put("task_id", taskId);
    				map.put("channel", "");
    				map.put("source_group", "1000100000000");
    				map.put("persist_time_mills", System.currentTimeMillis());
    				map.put("persist_date", SM_DAY_FORMAT.format(new Date()));
    				map.put("crawl_time", SM_DATE_FORMAT.format(new Date()));
//    				map.put("author", "");
//    				map.put("search_word", "");
    				map.put("sentiment", -1);
    				map.put("simhash", -1L);
    				map.put("upsert", -1);
    				map.put("indexed", 0);
    			}
				params.put("kvList", kvList);
				useCustom = (boolean) (null == params.get("useCustom") ? false : params.get("useCustom"));
				if (useCustom) {
					String className = (String) params.get("className");
					mStaticActContext.loadJar("ext");
					Class<StaticStoreDataAct> clazz = (Class<StaticStoreDataAct>) mStaticActContext.loadClass(className);
					StaticStoreDataAct customStoreDataAct = clazz.newInstance();
					mStaticStoreDataActProxy.act(customStoreDataAct);
				} else {
					mStaticStoreDataActProxy.act(mStaticActContext.readStaticStoreDataAct());
				}
				size = mStaticStoreDataActProxy.execute(params, mStaticActContext);
				LOGGER.info("#############storeData act init#############");
				break;
			}
			
		}
		
		// 写入采集数量
		if (size > 0) mStaticActContext.writeAcquisiteSize(size);
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
		mStaticPaginationAct.addActListener(this);
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
	private StaticActProxy<Map<String, Object>, List<Map<String, Object>>> mStaticDedupActProxy;
	private StaticActProxy<Map<String, Object>, Integer> mStaticStoreDataActProxy;
	//
	//
	
	//
	private List<Map<String, Object>> afterPaginationCommands;
	
	static final Logger LOGGER = Logger.getLogger(ApacheActor.class);
	final DateFormat SM_DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	final DateFormat SM_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
}
