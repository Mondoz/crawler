package com.hiekn.scraj.uyint.common.core;

import java.util.List;
import java.util.Map;

public interface StaticPaginationListener {
	
	void performAfterPaginationCommands(List<Map<String, Object>> kvList) throws Exception;
	
}
