package com.hiekn.scraj.movert.act;

import java.util.List;
import java.util.Map;

import com.hiekn.scraj.common.io.IOContext;
import com.hiekn.scraj.common.io.service.DataService;
import com.hiekn.scraj.common.io.service.impl.DataServiceImpl;
import com.hiekn.scraj.movert.common.act.BrowserActContext;
import com.hiekn.scraj.movert.common.act.BrowserStoreDataAct;

public class BrowserStoreDataActImpl implements BrowserStoreDataAct {

	@SuppressWarnings("unchecked")
	public Integer execute(Map<String, Object> paramsMap, BrowserActContext context) throws Exception {
		//
		List<Map<String, Object>> kvList = (List<Map<String, Object>>) paramsMap.get("kvList");
		// 总记录数
    	int resultSize = kvList.size();
    	
    	LOGGER.info("storing data, size... " + resultSize);
    	
    	//
    	if (resultSize == 0) return 0;
    	
    	Map<String, Object> storeMeta = (Map<String, Object>) paramsMap.get("storeMeta");
    	
    	// actually store data
    	dataService.writeData(kvList, storeMeta, IO_CONTEXT);
    	
    	// clear list
    	kvList.clear();
    	
    	//
    	LOGGER.info("stored data, size... " + resultSize);
    	
    	return resultSize;
	}
	
	public void clear() {
		IO_CONTEXT.clear();
	}
	
	// static block
	static {
		IO_CONTEXT = new IOContext();
	}
	// instance block
	{
		dataService = new DataServiceImpl();
	}
	private static IOContext IO_CONTEXT;
	private DataService dataService;
	
}
