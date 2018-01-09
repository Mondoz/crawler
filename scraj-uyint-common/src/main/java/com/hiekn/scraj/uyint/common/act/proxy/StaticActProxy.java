package com.hiekn.scraj.uyint.common.act.proxy;

import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.act.StaticActContext;

public class StaticActProxy<VALUEIN, VALUEOUT> {
	
	public VALUEOUT execute(VALUEIN paramsMap, StaticActContext context) throws Exception {
		return mAct.execute(paramsMap, context);
	}
	
	public void act(StaticAct<VALUEIN, VALUEOUT> act) { 
		this.mAct = act;
	}
	
	private StaticAct<VALUEIN, VALUEOUT> mAct;
}
