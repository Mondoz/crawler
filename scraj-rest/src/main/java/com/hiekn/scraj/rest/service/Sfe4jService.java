package com.hiekn.scraj.rest.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hiekn.sfe4j.core.FliverContext;

public class Sfe4jService {
	
	public static final List<Map<String, String>> getDefaultFormatters() {
		final List<Map<String, String>> rsList = new ArrayList<>();
		Map<String, String> registeredFormatterMappings = fliverContext.registeredFormatterMappings();
		Map<String, String> emap;
		for (Map.Entry<String, String> rfm : registeredFormatterMappings.entrySet()) {
			String fno = rfm.getKey();
			String fname = JSON.parseObject(rfm.getValue()).getString("name");
			
			emap = new HashMap<>();
			emap.put("title", fname);
			emap.put("value", fno);
			
			rsList.add(emap);
		}
		return rsList;
	}
	
	static {
		try {
			fliverContext = new FliverContext();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private static FliverContext fliverContext;
}
