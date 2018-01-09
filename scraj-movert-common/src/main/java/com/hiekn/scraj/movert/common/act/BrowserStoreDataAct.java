package com.hiekn.scraj.movert.common.act;

import java.util.Map;

public interface BrowserStoreDataAct extends BrowserAct<Map<String, Object>, Integer> {
	
	/**
	 * close/clear database connect resource.
	 */
	void clear();
}
