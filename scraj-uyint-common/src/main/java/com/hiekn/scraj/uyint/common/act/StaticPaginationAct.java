package com.hiekn.scraj.uyint.common.act;

import java.util.List;
import java.util.Map;

import com.hiekn.scraj.uyint.common.core.StaticPaginationListener;

public interface StaticPaginationAct extends StaticAct<Map<String, Object>, List<Map<String, Object>>> {
	
	void initial(StaticFetchKVAct mStaticFetchKVAct, StaticFetchKVsAct mStaticFetchKVsAct,
			StaticFetchListAct mStaticFetchListAct);
	
	void addActListener(StaticPaginationListener listener);
}
