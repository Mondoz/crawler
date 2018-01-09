package com.hiekn.scraj.common.io.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.hiekn.scraj.common.io.IOContext;

public interface DataService {
	
	//
	Logger LOGGER = Logger.getLogger(DataService.class);
	
	//
	Gson GSON = new Gson();
	
	//
	String STORE_ENGINE_MONGODB = "mongodb";
	String STORE_ENGINE_ELASICSEARCH = "elasicsearch";
	String STORE_ENGINE_MYSQL = "jdbc";
	String STORE_ENGINE_API = "api";
	
	int writeData(List<Map<String, Object>> kvList, Map<String, Object> storeMeta, IOContext context) throws Exception;
	
}
