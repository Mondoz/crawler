package com.hiekn.scraj.movert.common.act.domain;

public final class BrowserKV {
	
	/**
	 * key和value都有
	 * @param key
	 * @param value
	 */
	public BrowserKV(String key, Object value) {
		this.key = key;
		this.value = value;
	}
	
	/**
	 * 只有value
	 * @param value
	 */
	public BrowserKV(Object value) {
		this.key = null;
		this.value = value;
	}
	
	public final String key;
	public final Object value;
}
