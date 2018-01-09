package com.hiekn.scraj.rest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConstResource {
	
	static Properties props = new Properties();
    static {
    	try (InputStream in = ConstResource.class.getClassLoader().getResourceAsStream("rest.properties");) {
    		props.load(in);
    	} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    }
	
    public static String getProperty(String key) { return props.getProperty(key, ""); }
    
	public static final String MYSQL_DRIVER = getProperty("mysql_driver");
	public static final String MYSQL_URL = getProperty("mysql_url");
	public static final String MYSQL_USER = getProperty("mysql_user");
	public static final String MYSQL_PASSWORD = getProperty("mysql_password");
	
	public static final String MONGO_HOST = getProperty("mongo_host");
	public static final int MONGO_PORT = Integer.valueOf(getProperty("mongo_port"));
	public static final String MONITOR_MONGO_DATABASE = getProperty("monitor_mongo_database");
	public static final String MONITOR_MONGO_COLLECTION = getProperty("monitor_mongo_collection");
	public static final String VISUAL_MONGO_DATABASE = getProperty("visual_mongo_database");
	public static final String VISUAL_MONGO_COLLECTION = getProperty("visual_mongo_collection");
	
	public static final String ES_NAME = getProperty("es_name");
	public static final String ES_HOST = getProperty("es_host");
	public static final int ES_PORT = Integer.valueOf(getProperty("es_port"));
	
	public static final String FIREFOX_BIN_PATH = getProperty("firefox_bin_path");
	
}
