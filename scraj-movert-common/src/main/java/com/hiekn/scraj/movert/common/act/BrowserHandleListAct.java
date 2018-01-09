package com.hiekn.scraj.movert.common.act;

import java.util.List;
import java.util.Map;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchListAct;
import com.hiekn.scraj.uyint.common.act.StaticOpenAct;

public interface BrowserHandleListAct extends BrowserAct<Map<String, Object>, List<Map<String, Object>>> {
	
	void initial(BrowserOpenAct mBrowserOpenAct, BrowserClickAct mBrowserClickAct, 
			BrowserFetchKVAct mBrowserFetchKVAct, BrowserFetchKVsAct mBrowserFetchKVsAct, 
			BrowserFetchListAct mBrowserFetchListAct, StaticActContext mStaticActContext, 
			StaticOpenAct mStaticOpenAct, StaticFetchKVAct mStaticFetchKVAct, StaticFetchKVsAct mStaticFetchKVsAct, 
			StaticFetchListAct mStaticFetchListAct);
}
