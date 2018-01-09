package com.hiekn.scraj.movert.common.act;

import java.util.Map;

public interface BrowserClickAct extends BrowserAct<Map<String, Object>, String> {
	
	int MAX_TRIES = 3;
}
