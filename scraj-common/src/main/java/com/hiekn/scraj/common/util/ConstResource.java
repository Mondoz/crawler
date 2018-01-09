package com.hiekn.scraj.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 配置信息
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 */
public class ConstResource {
	
	//
	// load properties file.
	//
    static Properties props = new Properties();
    static {
    	try (InputStream in = ConstResource.class.getClassLoader().getResourceAsStream("scrapy-common.properties")) {
    		 props.load(in);
    	} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
    
    
    
    //
    // meta properties.
    //
    public static final String META_NODE_HOSTNAME = getString("meta.node.hostname");
    public static final String META_NODE_NAME = getString("meta.node.name");
    public static final int META_NODE_REGISTRY_PORT = getInteger("meta.node.registry.port");
    
    
    
    //
    // dedup properties.
    //
    public static final String DEDUP_NODE_HOSTNAME = getString("dedup.node.hostname");
    public static final String DEDUP_NODE_NAME = getString("dedup.node.name");
    public static final int DEDUP_NODE_REGISTRY_PORT = getInteger("dedup.node.registry.port");
    
    
    //
    // monitor properties.
    //
    public static final String MONITOR_NODE_HOSTNAME = getString("monitor.node.hostname");
    public static final String MONITOR_NODE_NAME = getString("monitor.node.name");
    public static final int MONITOR_NODE_REGISTRY_PORT = getInteger("monitor.node.registry.port");
    
    //
    // get property
    //
	public static final String getString(String key) {
		return getString(key, "");
	}
	public static final String getString(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	public static final int getInteger(String key) {
		return getInteger(key, -1);
	}
	public static final int getInteger(String key, int defaultValue) {
		return Integer.parseInt(props.getProperty(key, defaultValue + ""));
	}
	public static final long getLong(String key) {
		return getLong(key, -1L);
	}
	public static final long getLong(String key, long defaultValue) {
		return Long.parseLong(props.getProperty(key, defaultValue + ""));
	}
	
}
