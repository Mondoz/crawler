package com.hiekn.sfe4j.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/19
 *
 */
public interface Fliver {
	
	/**
	 * 
	 */
	Logger LOGGER = Logger.getLogger(Fliver.class);
	
	/**
	 * 
	 * @param odata
	 * @param cleanTaskJson
	 * @return
	 */
	List<Map<String, Object>> clean(List<Map<String, Object>> odata, String cleanTaskJson);
	
	
	String doFormat(String src, String mixs);
	 
	 
	String doTimeFormat(String src, String timeType);
}
