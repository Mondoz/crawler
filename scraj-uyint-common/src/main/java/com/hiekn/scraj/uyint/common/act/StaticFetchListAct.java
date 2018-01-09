package com.hiekn.scraj.uyint.common.act;

import java.util.Map;

import com.hiekn.scraj.uyint.common.act.domain.StaticKV;

/**
 * 
 * 
 * 
 * 
 * 抓取列表：
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * @author pzn
 *
 */
public interface StaticFetchListAct extends StaticAct<Map<String, Object>, StaticKV> {
	
	void initial(StaticPaginationAct mStaticPaginationAct, StaticFetchKVsAct mStaticFetchKVsAct,
			StaticFetchKVAct mStaticFetchKVAct, StaticOpenAct mStaticOpenAct);
	
}
