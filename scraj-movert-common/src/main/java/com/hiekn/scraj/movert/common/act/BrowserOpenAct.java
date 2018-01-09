package com.hiekn.scraj.movert.common.act;

import java.util.Map;

public interface BrowserOpenAct extends BrowserAct<Map<String, Object>, String> {
	
	String OPEN_ENGINE_STATIC = "static";
	String OPEN_ENGINE_BROWSER = "browser";
	
	int MAX_TRIES = 3;
	
}
