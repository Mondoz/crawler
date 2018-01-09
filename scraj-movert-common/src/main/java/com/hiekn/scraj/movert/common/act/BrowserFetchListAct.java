package com.hiekn.scraj.movert.common.act;

import java.util.Map;

import com.hiekn.scraj.movert.common.act.domain.BrowserKV;

public interface BrowserFetchListAct extends BrowserAct<Map<String, Object>, BrowserKV> {
	
	/**
	 * 
	 * @param mBrowserFetchKVAct
	 * @param mBrowserFetchKVsAct
	 */
	void initial(BrowserFetchKVAct mBrowserFetchKVAct, 
			BrowserFetchKVsAct mBrowserFetchKVsAct);
}
