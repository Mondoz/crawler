package com.hiekn.scraj.uyint.common.act.domain;

/**
 * 
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 1.0
 */
public class StaticKV {
	
	public StaticKV(Object value) {
		this.key = null;
		this.value = value;
	}
	
	public StaticKV(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	public final String key;
	public final Object value;
}
