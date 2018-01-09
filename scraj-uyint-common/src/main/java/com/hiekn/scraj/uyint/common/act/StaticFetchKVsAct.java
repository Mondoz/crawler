package com.hiekn.scraj.uyint.common.act;

import java.util.Map;

public interface StaticFetchKVsAct extends StaticAct<Map<String, Object>, Map<String, Object>> {
	
	void initial(StaticFetchKVAct mStaticFetchKVAct);
	
}
