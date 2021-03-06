package com.hiekn.scraj.movert.act;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserDedupAct;
import com.hiekn.scraj.movert.factory.BeanFactory;

public class BrowserDedupActImpl implements BrowserDedupAct {

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		// 原始list大小
		List<Map<String, Object>> kvList = (List<Map<String, Object>>) paramsMap.get("kvList");
		//
		LOGGER.info("before dedup, size... " + kvList.size());
    	// 
    	if (kvList.size() == 0) return kvList;
    	// 排重后的list
    	List<Map<String, Object>> dedupedList = new ArrayList<>();
    	
    	// 数量过大问题  处理
		List<Map<String, Object>> tmpKvList = null;
		int loop = (kvList.size() + 499) / 500;
		int i = 0;
		while (i < loop) {
			int fromIndex = i * 500;
			if (i == loop - 1) tmpKvList = new ArrayList<>(kvList.subList(fromIndex, kvList.size()));
			else tmpKvList = new ArrayList<>(kvList.subList(fromIndex, fromIndex + 500));
			
			paramsMap.put("kvList", tmpKvList);
			dedupedList.addAll(BeanFactory.dedupServiceProxy().dedup(paramsMap));
			
			i++;
		}
		
    	LOGGER.info("after dedup, size... " + dedupedList.size());
    	
    	return dedupedList;
	}
	
}
