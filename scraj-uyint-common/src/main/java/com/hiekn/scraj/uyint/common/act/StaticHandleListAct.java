package com.hiekn.scraj.uyint.common.act;

import java.util.List;
import java.util.Map;

public interface StaticHandleListAct extends StaticAct<Map<String, Object>, List<Map<String, Object>>> {
	
	 void initial(StaticOpenAct mStaticOpenAct, StaticFetchKVAct mStaticFetchKVAct, StaticFetchKVsAct mStaticFetchKVsAct,
				StaticFetchListAct mStaticFetchListAct);
	
}
