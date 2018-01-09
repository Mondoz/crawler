package com.hiekn.sfe4j.core;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/19
 *
 */
public interface Sliver {
	
	/**
	 * log4j
	 */
	Logger LOGGER = Logger.getLogger(Sliver.class);
	
	/**
	 * 
	 * 读取数据的接口
	 * 
	 * @param rsList 存储读取到的数据的list
	 * @param paramsJson 读取时需要的一些参数
	 * @param context 
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> readData(final List<Map<String, Object>> rsList, 
			String paramsJson, 
			Sfe4jContext context) throws Exception;
}
