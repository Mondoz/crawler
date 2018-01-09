package com.hiekn.scraj.uyint.common.util;

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
	
    static Properties props = new Properties();
    
    static {
        try (InputStream in = ConstResource.class.getClassLoader().getResourceAsStream("scrapy-uyint-common.properties")) {
        	 props.load(in);
        } catch (IOException e) {
        	 e.printStackTrace();
             throw new RuntimeException(e);
		}
        
    }
    
    // 操作系统换行符
    public static final String NEW_LINE_CHARACTER = get("os").equals("unix") ? "\n" : "\r\n";
    
    // parser
    public static final int CONTENT_TITLE_MIN_LENGTH = Integer.parseInt(get("content.title.min.length"));
    public static final int CONTENT_MIN_LENGTH = Integer.parseInt(get("content.min.length"));
    public static final int CONTENT_MIN_CONFIRM_LENGTH = Integer.parseInt(get("content.min.confirm.length"));
    
    // get property
    public static String get(String key) { return props.getProperty(key); }

}
