package com.hiekn.scraj.movert.common.act;

import java.util.Map;

public interface BrowserFetchKVsAct extends BrowserAct<Map<String, Object>, Map<String, Object>> {
	
	/**
	 * 
	 * @param mBrowserFetchKVAct
	 */
	void initial(BrowserFetchKVAct mBrowserFetchKVAct);
}
