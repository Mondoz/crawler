package com.hiekn.scraj.common.meta.domain;

import java.io.Serializable;

public class Metadata implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3133018525436674571L;
	
	public int id;// 任务id
	public String name;// 任务名称
	
	public int intervalType;// 周期类型
	public long intervalMillis;// 周期毫秒数
	public String intervalExp;// 周期表达式
	
	public String conf;// 配置信息
	public int confPriority;// 配置优先级
	public int confParallel;// 配置并行化
	
	public String cookie;// cookeis --> json array
	
	public Metadata clone() throws CloneNotSupportedException {
		return (Metadata) super.clone();
	}

	public String toString() {
		return "Metadata [id=" + id + ", name=" + name + ", intervalType=" + intervalType + ", intervalMillis="
				+ intervalMillis + ", intervalExp=" + intervalExp + ", conf=" + conf + ", confPriority=" + confPriority
				+ ", confParallel=" + confParallel + ", cookie=" + cookie + "]";
	}
}
