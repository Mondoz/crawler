package com.hiekn.scraj.movert.common.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.movert.common.act.BrowserAct;
import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserClickAct;
import com.hiekn.scraj.movert.common.act.BrowserDedupAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVsAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchListAct;
import com.hiekn.scraj.movert.common.act.BrowserHandleListAct;
import com.hiekn.scraj.movert.common.act.BrowserOpenAct;
import com.hiekn.scraj.movert.common.act.BrowserPaginationAct;
import com.hiekn.scraj.movert.common.act.BrowserScrollAct;
import com.hiekn.scraj.movert.common.act.BrowserStoreDataAct;
import com.hiekn.scraj.movert.common.act.BrowserTypeTextAct;
import com.hiekn.scraj.movert.common.act.domain.BrowserKV;
import com.hiekn.scraj.movert.common.act.impl.BrowserClickActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserFetchKVActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserFetchKVsActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserFetchListActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserHandleListActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserOpenActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserPaginationActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserScrollActImpl;
import com.hiekn.scraj.movert.common.act.impl.BrowserTypeTextActImpl;
import com.hiekn.scraj.movert.common.http.FirefoxDriverProxy;
import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVsActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchListActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticOpenActImpl;
import com.hiekn.scraj.uyint.common.http.impl.ApacheHttpReader;
import com.hiekn.sfe4j.core.impl.FliverImpl;

@SuppressWarnings("unchecked")
public class BrowserActor implements BrowserPaginationListener {
	
	public final int perform(int taskId, String taskName, int parallel, String commandJson) throws Exception {
		
		long beginMillis = System.currentTimeMillis();
		LOGGER.info("execute task init... taskInfo: {taskId: " + taskId + ", taskName: " + taskName + "},  begin timestamp: " + beginMillis);
		
		List<Map<String, Object>> commands = BrowserAct.GSON.fromJson(commandJson, new TypeToken<List<Map<String, Object>>>() {}.getType());
		
		// 检查并设置afterPaginationCommands
		boolean hasPagination = false;
		afterPaginationCommands.clear();
		for (Map<String, Object> command : commands) {
			if (hasPagination) afterPaginationCommands.add(command);;
			if (command.containsKey("browserPagination")) hasPagination = true;;
		}
		
		//
		// 不并行 
		//
		if (parallel < 1) {
			Map<String, Object> openParam = (Map<String, Object>) commands.get(0).get("browserOpen");
			List<String> urls = (List<String>) openParam.remove("urls");
			
			//
			for (int i = 0, len = urls.size(); i < len; i++) {
				// open -- > url
				openParam.put("url", urls.get(i));
				// 
				performCommands(commands);
			}
		} else {
			// just execute.
			performCommands(commands);
		}
		
		long endMillis = System.currentTimeMillis();
		LOGGER.info("execute task done... taskInfo: {taskId: " + taskId + ", taskName: " + taskName + "},"
				+ "  end timestamp: " + endMillis + ", cost milliseconds: " + (endMillis - beginMillis)
				+ ", acquisite size: " + mBrowserActContext.readAcquisiteSize());
		
		return mBrowserActContext.readAcquisiteSize();
	}
	
	
	public final void performCommands(List<Map<String, Object>> commands) throws Exception {
		int size = 0;
		List<Map<String, Object>> kvList = new ArrayList<Map<String, Object>>();
		// execute commands
		for (Map<String, Object> cmd : commands) {
    		String cmdKey = cmd.keySet().iterator().next();
			Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
			
			switch (cmdKey) {
			case BrowserAct.BROWSER_OPEN_ACT_ALIAS:
				mBrowserOpenAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_CLICK_ACT_ALIAS:
				mBrowserClickAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_SCROLL_ACT_ALIAS:
				mBrowserScrollAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_TYPE_TEXT_ACT_ALIAS:
				mBrowserTypeTextAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_PAGINATION_ACT_ALIAS:
				kvList = mBrowserPaginationAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_FETCH_KV_ACT_ALIAS:
				BrowserKV kvPair = mBrowserFetchKVAct.execute(params, mBrowserActContext);
				// 单个值
				Map<String, Object> singleKV = new HashMap<String, Object>();
				if (null == kvPair.key) {
					singleKV.put(UUID.randomUUID().toString().replaceAll("-", "p"), kvPair.value);
				} else {
					singleKV.put(kvPair.key, kvPair.value);
				}
				kvList.add(singleKV);
				break;
			case BrowserAct.BROWSER_FETCH_KVS_ACT_ALIAS:
				Map<String, Object> fetchKVs = mBrowserFetchKVsAct.execute(params, mBrowserActContext);
				if (fetchKVs.size() > 0) kvList.add(fetchKVs);;
				break;
			case BrowserAct.BROWSER_FETCH_LIST_ACT_ALIAS:
				BrowserKV kv = mBrowserFetchListAct.execute(params, mBrowserActContext);
				if (null == kv.key) {
					kvList.addAll((List<Map<String,Object>>) kv.value);
				} else {
					Map<String, Object> hm = new HashMap<>();
					hm.put(kv.key, kv.value);
					kvList.add(hm);
				}
				break;
			case BrowserAct.BROWSER_HANDLE_LIST_ACT_ALIAS:
				params.put("kvList", kvList);
				kvList = mBrowserHandleListAct.execute(params, mBrowserActContext);
//	
//				// write to local disk.
//				Files.write(new File("news.txt").toPath(), BrowserAct.GSON.toJson(kvList).getBytes("utf-8"), 
//						StandardOpenOption.CREATE, 
//						StandardOpenOption.TRUNCATE_EXISTING);
				
				break;
			case BrowserAct.BROWSER_DEDUP_ACT_ALIAS:
				params.put("kvList", kvList);
				kvList = mBrowserActContext.readBrowserDedupAct().execute(params, null);
				break;
			case BrowserAct.BROWSER_STORE_ACT_ALIAS:
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
    			params.put("kvList", kvList);
    			size = mBrowserActContext.readBrowserStoreDataAct().execute(params, null);
				break;
			}
		}
		
		//
		if (size > 0) mBrowserActContext.writeAcquisiteSize(size);
	}
	
	
	public final void performAfterPaginationCommands(List<Map<String, Object>> kvList) throws Exception {
		int size = 0;
		String dependencySource = null;
		
		// execute commands
		for (Map<String, Object> cmd : afterPaginationCommands) {
			String cmdKey = cmd.keySet().iterator().next();
			Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
			
			switch (cmdKey) {
			case BrowserAct.STATIC_OPEN_ACT_ALIAS:
				dependencySource = mStaticOpenAct.execute(params, mStaticActContext);
				break;
			case BrowserAct.STATIC_FETCH_KV_ACT_ALIAS:
				params.put("dependencySource", dependencySource);
				StaticKV kvPair = mStaticFetchKVAct.execute(params, mStaticActContext);
				Map<String, Object> singleKeyMap = new HashMap<String, Object>();
				singleKeyMap.put(kvPair.key, kvPair.value);
				kvList.add(singleKeyMap);
				break;
			case BrowserAct.STATIC_FETCH_KVS_ACT_ALIAS:
				params.put("dependencySource", dependencySource);
				Map<String, Object> multiKeyMap = mStaticFetchKVsAct.execute(params, mStaticActContext);
				if (multiKeyMap.size() > 0) kvList.add(multiKeyMap);
				break;
			case BrowserAct.STATIC_FETCH_LIST_ACT_ALIAS:
				params.put("dependencySource", dependencySource);
				StaticKV kv = mStaticFetchListAct.execute(params, mStaticActContext);
				if (null == kv.key) {
					kvList.addAll((List<Map<String,Object>>) kv.value);
				} else {
					Map<String, Object> hm = new HashMap<>();
					hm.put(kv.key, kv.value);
					kvList.add(hm);
				}
				break;
			case BrowserAct.BROWSER_OPEN_ACT_ALIAS:
				mBrowserOpenAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_CLICK_ACT_ALIAS:
				mBrowserClickAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_SCROLL_ACT_ALIAS:
				mBrowserScrollAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_TYPE_TEXT_ACT_ALIAS:
				mBrowserTypeTextAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_PAGINATION_ACT_ALIAS:
				kvList = mBrowserPaginationAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_FETCH_KVS_ACT_ALIAS:
				Map<String, Object> fetchKVs = mBrowserFetchKVsAct.execute(params, mBrowserActContext);
				if (fetchKVs.size() > 0) kvList.add(fetchKVs);;
				break;
			case BrowserAct.BROWSER_HANDLE_LIST_ACT_ALIAS:
				params.put("kvList", kvList);
				kvList = mBrowserHandleListAct.execute(params, mBrowserActContext);
				break;
			case BrowserAct.BROWSER_DEDUP_ACT_ALIAS:
				params.put("kvList", kvList);
				kvList = mBrowserActContext.readBrowserDedupAct().execute(params, null);
				break;
			case BrowserAct.BROWSER_STORE_ACT_ALIAS:
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
				params.put("kvList", kvList);
				size = mBrowserActContext.readBrowserStoreDataAct().execute(params, null);
				break;
			}
		}
		
		// 
//		Files.write(new File("samples/" + UUID.randomUUID().toString()).toPath(), 
//				JSON.toJSONString(kvList, true).getBytes("utf-8"), 
//				StandardOpenOption.CREATE, 
//				StandardOpenOption.TRUNCATE_EXISTING);
		
		//
		if (size > 0) mBrowserActContext.writeAcquisiteSize(size);
	}
	
	//
	public BrowserActContext browserActContext() {
		return mBrowserActContext;
	}
	
	public StaticActContext staticActContext() {
		return mStaticActContext;
	}
	
	public void reset() {
		// reset BrowserActContext
		mBrowserActContext.resetAcquisiteSize();
		
		// reset ActContext
		mStaticActContext.cleanup();
		mStaticActContext.writeHttpReader(new ApacheHttpReader());
	}
	
	public BrowserActor(BrowserDedupAct mBrowserDedupAct, BrowserStoreDataAct mBrowserStoreDataAct) {
		
		//
		// instance browser components.
		//
		mWebDriverProxy = new FirefoxDriverProxy();
		mBrowserActContext = new BrowserActContext();
		mBrowserOpenAct = new BrowserOpenActImpl();
		mBrowserClickAct = new BrowserClickActImpl();
		mBrowserTypeTextAct = new BrowserTypeTextActImpl();
		mBrowserScrollAct = new BrowserScrollActImpl();
		mBrowserFetchKVAct = new BrowserFetchKVActImpl();
		mBrowserFetchKVsAct = new BrowserFetchKVsActImpl();
		mBrowserFetchListAct = new BrowserFetchListActImpl();
		mBrowserPaginationAct = new BrowserPaginationActImpl();
		mBrowserHandleListAct = new BrowserHandleListActImpl();
		
		//
		// instance static components.
		//
		mStaticActContext = new StaticActContext();
		mStaticOpenAct = new StaticOpenActImpl();
		mStaticFetchKVAct = new StaticFetchKVActImpl();
		mStaticFetchKVsAct = new StaticFetchKVsActImpl();
		mStaticFetchListAct = new StaticFetchListActImpl();
		
		//
		// write webDriverProxy/dedupAct/storeDataAct.
		//
		mBrowserActContext.writeWebDriverProxy(mWebDriverProxy);
		mBrowserActContext.writeBrowserDedupAct(mBrowserDedupAct);
		mBrowserActContext.writeBrowserStoreDataAct(mBrowserStoreDataAct);
		mStaticActContext.writeHttpReader(new ApacheHttpReader());
		mBrowserActContext.writeFliver(new FliverImpl());
		
		//		
		// initial browser components.
		//		
		mBrowserFetchKVsAct.initial(mBrowserFetchKVAct);
		mBrowserFetchListAct.initial(mBrowserFetchKVAct, mBrowserFetchKVsAct);
		mBrowserPaginationAct.initial(mBrowserClickAct, mBrowserScrollAct, 
				mBrowserFetchKVAct, mBrowserFetchKVsAct, mBrowserFetchListAct);
		mBrowserHandleListAct.initial(mBrowserOpenAct, mBrowserClickAct, 
				mBrowserFetchKVAct, mBrowserFetchKVsAct, mBrowserFetchListAct, 
				mStaticActContext, mStaticOpenAct, mStaticFetchKVAct, 
				mStaticFetchKVsAct, mStaticFetchListAct);
		
		//
		// initial static components.
		//
		mStaticFetchListAct.initial(null, mStaticFetchKVsAct, mStaticFetchKVAct, mStaticOpenAct);
		mStaticFetchKVsAct.initial(mStaticFetchKVAct);
		
		//
		afterPaginationCommands = new ArrayList<Map<String, Object>>();
		
		// add
		mBrowserPaginationAct.addListener(this);
		
	}
	
	// 浏览器组件
	private FirefoxDriverProxy mWebDriverProxy;
	private BrowserActContext mBrowserActContext;
	private BrowserOpenAct mBrowserOpenAct;
	private BrowserClickAct mBrowserClickAct;
	private BrowserTypeTextAct mBrowserTypeTextAct;
	private BrowserScrollAct mBrowserScrollAct;
	private BrowserFetchKVAct mBrowserFetchKVAct;
	private BrowserFetchKVsAct mBrowserFetchKVsAct;
	private BrowserFetchListAct mBrowserFetchListAct;
	private BrowserPaginationAct mBrowserPaginationAct;
	private BrowserHandleListAct mBrowserHandleListAct;
	
	// 静态组件
	private StaticActContext mStaticActContext;
	private StaticOpenAct mStaticOpenAct;
	private StaticFetchKVAct mStaticFetchKVAct;
	private StaticFetchKVsAct mStaticFetchKVsAct;
	private StaticFetchListAct mStaticFetchListAct;
	
	//
	private List<Map<String, Object>> afterPaginationCommands;
	
	//
	//
	private static final Logger LOGGER = Logger.getLogger(BrowserActor.class);
}
