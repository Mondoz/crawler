package com.hiekn.scraj.movert.common.act;

import java.util.List;
import java.util.Map;

import com.hiekn.scraj.movert.common.core.BrowserPaginationListener;

public interface BrowserPaginationAct extends BrowserAct<Map<String, Object>, List<Map<String, Object>>> {
	
	/**
	 * 
	 * @param mBrowserClickAct
	 * @param mBrowserScrollAct
	 * @param mBrowserFetchKVAct
	 * @param mBrowserFetchKVsAct
	 * @param mBrowserFetchListAct
	 */
	void initial(BrowserClickAct mBrowserClickAct, 
			BrowserScrollAct mBrowserScrollAct, 
			BrowserFetchKVAct mBrowserFetchKVAct, 
			BrowserFetchKVsAct mBrowserFetchKVsAct, 
			BrowserFetchListAct mBrowserFetchListAct);
	
	/**
	 * 
	 * @param listener
	 */
	void addListener(BrowserPaginationListener listener);
}
