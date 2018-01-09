package com.hiekn.scraj.movert.common.core;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author pzn
 */
public interface BrowserPaginationListener {
	
	/**
	 * 
	 * @param kvList
	 * @throws Exception 
	 */
	void performAfterPaginationCommands(List<Map<String, Object>> kvList) throws Exception;
}
