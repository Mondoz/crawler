package com.hiekn.scraj.uyint.common.act.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hiekn.scraj.uyint.common.act.StaticActContext;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVAct;
import com.hiekn.scraj.uyint.common.act.StaticFetchKVsAct;
import com.hiekn.scraj.uyint.common.act.domain.StaticKV;
import com.hiekn.sfe4j.util.StringUtils;

public class StaticFetchKVsActImpl implements StaticFetchKVsAct {

	@SuppressWarnings("unchecked")
	public Map<String, Object> execute(Map<String, Object> paramsMap, StaticActContext context) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		//
		String dependencySource = (String) paramsMap.get("dependencySource");
		if (!StringUtils.isNullOrEmpty(dependencySource)) {
			List<Map<String, Object>> fetchValueWithKeys = (List<Map<String, Object>>) paramsMap.get("fetchValueWithKeys");
			//
			int expectFieldSize = fetchValueWithKeys.size();// 待提取的字段个数
			int invalidFieldSize = 0;// 提取不正常的字段个数
			
			//
			for (Map<String, Object> fetchValue : fetchValueWithKeys) {
				
				// check field 
				StaticKV kvPair = null;
				String field = (String) fetchValue.get("field");
				if (!StringUtils.isNullOrEmpty(field)) {
					try {
						fetchValue.put("dependencySource", dependencySource);
						// TODO ??? title 传入
						kvPair = mStaticFetchKVAct.execute(fetchValue, context);
					} catch(RuntimeException e) {
						//
						e.printStackTrace();
						LOGGER.error("RuntimeException ... " + e);
					}
				}
				//
				if (null == kvPair) invalidFieldSize++;
				else if (resultMap.containsKey(kvPair.key)) {
					resultMap.put(kvPair.key, resultMap.get(kvPair.key) + "" + kvPair.value);
				} else {
					resultMap.put(kvPair.key, kvPair.value);
				}
			}// end of loop all props.
			
			// check field val
			if (expectFieldSize > invalidFieldSize) {// 有效字段大于无效字段
				boolean includeSource = null == paramsMap.get("includeSource") ? 
	    				false : ((Boolean) paramsMap.get("includeSource"));
				
				if (includeSource) resultMap.put("dependencySource", dependencySource);
			} else {// 字段全部无效  清空数据
				if (expectFieldSize > 1) resultMap.clear();
			}
		}
		return resultMap;
		
	}
	
	public void initial(StaticFetchKVAct mStaticFetchKVAct) {
		this.mStaticFetchKVAct = mStaticFetchKVAct;
	}
	
	private StaticFetchKVAct mStaticFetchKVAct;
	
}
