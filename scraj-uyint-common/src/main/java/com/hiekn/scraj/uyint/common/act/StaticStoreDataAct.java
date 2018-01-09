package com.hiekn.scraj.uyint.common.act;

import java.util.Map;

public interface StaticStoreDataAct extends StaticAct<Map<String, Object>, Integer> {
	
	/**
	 * close/clear database connect resource.
	 */
	void clear();
}
