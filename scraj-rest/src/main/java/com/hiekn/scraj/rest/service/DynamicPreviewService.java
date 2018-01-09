package com.hiekn.scraj.rest.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.movert.common.act.BrowserAct;
import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserClickAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchKVsAct;
import com.hiekn.scraj.movert.common.act.BrowserFetchListAct;
import com.hiekn.scraj.movert.common.act.BrowserHandleListAct;
import com.hiekn.scraj.movert.common.act.BrowserOpenAct;
import com.hiekn.scraj.movert.common.act.BrowserPaginationAct;
import com.hiekn.scraj.movert.common.act.BrowserScrollAct;
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
import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchKVsActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticFetchListActImpl;
import com.hiekn.scraj.uyint.common.act.impl.StaticOpenActImpl;
import com.hiekn.scraj.uyint.common.http.impl.ApacheHttpReader;
import com.hiekn.sfe4j.core.impl.FliverImpl;

public class DynamicPreviewService {
	
	public static final Object preview(String conf) throws Exception {
		
		Object result = null;
		PreviewActor previewActor = new PreviewActor();
		try {
			result = previewActor.perform(conf);
		} finally {
			previewActor.browserActContext().cleanup();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	static class PreviewActor{
		
		public final Object perform(String commandJson) throws Exception {
			
			Object result = null;
			List<Map<String, Object>> commands = StaticAct.GSON.fromJson(commandJson, new TypeToken<List<Map<String, Object>>>() {}.getType());
			//
			// 不并行 
			//
			Map<String, Object> openParam = (Map<String, Object>) commands.get(0).get("browserOpen");
			List<String> urls = (List<String>) openParam.remove("urls");
			
			//
			// open -- > url
			openParam.put("url", urls.get(0));
			// 
			result = performCommands(commands);
			
			return result;
		}
		
		public final Object performCommands(List<Map<String, Object>> commands) throws Exception {
			Object result = null;
			List<Map<String, Object>> kvList = new ArrayList<Map<String, Object>>();
			// execute commands
			for (Map<String, Object> cmd : commands) {
	    		String cmdKey = cmd.keySet().iterator().next();
				Map<String, Object> params = (Map<String, Object>) cmd.get(cmdKey);
				
				switch (cmdKey) {
				case BrowserAct.BROWSER_OPEN_ACT_ALIAS:
					result = mBrowserOpenAct.execute(params, mBrowserActContext);
					break;
				case BrowserAct.BROWSER_CLICK_ACT_ALIAS:
					result = mBrowserClickAct.execute(params, mBrowserActContext);
					break;
				case BrowserAct.BROWSER_SCROLL_ACT_ALIAS:
					result = mBrowserScrollAct.execute(params, mBrowserActContext);
					break;
				case BrowserAct.BROWSER_TYPE_TEXT_ACT_ALIAS:
					result = mBrowserTypeTextAct.execute(params, mBrowserActContext);
					break;
				case BrowserAct.BROWSER_PAGINATION_ACT_ALIAS:
					int currentPage = params.get("currentPage") == null ? 1 : ((Double) params.get("currentPage")).intValue();// 起始页
			    	int maxPage = ((Double) params.get("maxPage")).intValue() + currentPage - 1;
			    	if (maxPage > 3 || maxPage < 1) params.put("maxPage", 3.0);
					kvList = mBrowserPaginationAct.execute(params, mBrowserActContext);
					result = kvList;
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
					result = kvList;
					break;
				case BrowserAct.BROWSER_FETCH_KVS_ACT_ALIAS:
					Map<String, Object> fetchKVs = mBrowserFetchKVsAct.execute(params, mBrowserActContext);
					if (fetchKVs.size() > 0) kvList.add(fetchKVs);;
					result = kvList;
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
					result = kvList;
					break;
				case BrowserAct.BROWSER_HANDLE_LIST_ACT_ALIAS:
					// 取5个测试
					List<Map<String, Object>> testList = null;
					if (kvList.size() > 5) testList = kvList.subList(0, 5);
					else testList = kvList;
					params.put("kvList", testList);
					kvList = mBrowserHandleListAct.execute(params, mBrowserActContext);
					result = testList;
					break;
				}
			}
			
			//
			return result;
		}
		
		public BrowserActContext browserActContext() {
			return mBrowserActContext;
		}
		
		public PreviewActor() {
			
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
	}
}
