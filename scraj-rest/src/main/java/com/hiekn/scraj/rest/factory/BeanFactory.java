package com.hiekn.scraj.rest.factory;

import org.apache.log4j.Logger;

import com.hiekn.scraj.rest.dao.DbcpSingleton;
import com.hiekn.scraj.rest.dao.ESingleton;
import com.hiekn.scraj.rest.dao.MongoSingleton;
import com.hiekn.scraj.rest.util.ConstResource;

public class BeanFactory {
	
	static Logger LOGGER = Logger.getLogger(BeanFactory.class);
	
	static DbcpSingleton dbcpSingleton;
	static MongoSingleton mongoSingleton;
	static ESingleton esingleton;
	
	public static final DbcpSingleton dbcpSingleton() throws Exception {
		if (null == dbcpSingleton) {
			synchronized (BeanFactory.class) {
				if (null == dbcpSingleton) {
					String driver = ConstResource.MYSQL_DRIVER;
					String url = ConstResource.MYSQL_URL;
					String user = ConstResource.MYSQL_USER;
					String password = ConstResource.MYSQL_PASSWORD;
					
					dbcpSingleton = new DbcpSingleton(driver, url, user, password);
				}
			}
		}
		return dbcpSingleton;
	}
	
	public static final MongoSingleton mongoSingleton() throws Exception {
		if (null == mongoSingleton) {
			synchronized (BeanFactory.class) {
				if (null == mongoSingleton) {
					String host = ConstResource.MONGO_HOST;
					int port = ConstResource.MONGO_PORT;
					mongoSingleton = new MongoSingleton(host, port);
				}
			}
		}
		return mongoSingleton;
	}
	
	public static final ESingleton esingleton() throws Exception {
		if (null == esingleton) {
			synchronized (BeanFactory.class) {
				if (null == esingleton) {
					String clusterName = ConstResource.ES_NAME;
					String host = ConstResource.ES_HOST;
					int port = ConstResource.ES_PORT;
					esingleton = new ESingleton(host, port, clusterName);
				}
			}
		}
		return esingleton;
	}
	
}
