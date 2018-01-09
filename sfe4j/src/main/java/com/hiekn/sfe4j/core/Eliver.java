package com.hiekn.sfe4j.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 
 * 
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/19
 *
 */
public interface Eliver {
	
	/**
	 * 
	 */
	Logger LOGGER = Logger.getLogger(Eliver.class);
	
	/**
	 * 
	 * @param odata
	 * @param paramsJson
	 * @param context
	 * @return
	 * @throws Exception
	 */
	int writeData(List<Map<String, Object>> odata, 
			String paramsJson, 
			Sfe4jContext context) throws Exception;
}
