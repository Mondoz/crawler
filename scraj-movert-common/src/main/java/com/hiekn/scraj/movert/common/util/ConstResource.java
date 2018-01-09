package com.hiekn.scraj.movert.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 
 * 
 * @author pzn
 *
 */
public class ConstResource {
	
	static Properties props = new Properties();
    static {
        try (InputStream in = ConstResource.class.getClassLoader().getResourceAsStream("scrapy-movert-common.properties")) {
        	 props.load(in);
        } catch (IOException e) {
        	 e.printStackTrace();
             throw new RuntimeException(e);
		}
    }
    
    //
    // firefox driver.
    //
    public static final String FIREFOX_PATH = get("firefox.path");
    
    //
    // chrome driver.
    //
    public static final String CHROME_PATH = get("chrome.path");
    public static final String CHROME_DRIVER_PATH = get("chrome.driver.path");
    
    
    //
    public static final String get(String key) {
    	return props.getProperty(key);
    }
    
}
