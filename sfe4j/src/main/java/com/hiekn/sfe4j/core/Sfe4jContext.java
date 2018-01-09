package com.hiekn.sfe4j.core;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import com.hiekn.sfe4j.dao.DbcpSingleton;
import com.hiekn.sfe4j.dao.ESingleton;
import com.hiekn.sfe4j.dao.MongoSingleton;
import com.hiekn.sfe4j.util.ConstResource;

/**
 * 
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/19
 *
 */
public class Sfe4jContext {
	
	public Sfe4jContext() throws ClassNotFoundException {
		persistDaos = new HashMap<String, Object>();
		daoHeartbeats = new HashMap<String, Long>();
		
		dbcpSingleton = new DbcpSingleton(ConstResource.DRIVER_CLASS, ConstResource.JDBC_URL, 
				ConstResource.JDBC_USER, ConstResource.JDBC_PASSWORD);
	}
	
	/**
	 * 
	 * 持久化dao
	 * 
	 * 供下次使用
	 * 
	 * 当闲置时间操作阈值  则会自动关闭dao  释放资源
	 * 
	 * @param key
	 * @param dao
	 */
	public void writeDao(String key, Object dao) {
		if (!persistDaos.containsKey(key)) {
			persistDaos.put(key, dao);
			daoHeartbeats.put(key, System.currentTimeMillis());
		}
	}
	
	public Object readDao(String key) {
		daoHeartbeats.put(key, System.currentTimeMillis());
		return persistDaos.get(key);
	}
	
	public Map<String, Object> persistDaos() {
		return persistDaos;
	}
	
	public Map<String, Long> daoHeartbeats() {
		return daoHeartbeats;
	}
	
	public DbcpSingleton dbcpSingleton() {
		return dbcpSingleton;
	}
	
	public Connection getConnection() {
		return dbcpSingleton.getConnection();
	}
	
	/**
	 * 清除数据和资源
	 */
	public void clear() {
		//
		dbcpSingleton.close();
		//
		for (Map.Entry<String, Object> en : persistDaos.entrySet()) {
			Object dao = en.getValue();
			if (dao instanceof MongoSingleton) {
				((MongoSingleton) dao).close();
			} else if (dao instanceof ESingleton) {
				((ESingleton) dao).close();
			}
		}
		persistDaos.clear();
		//
		daoHeartbeats.clear();
	}
	
	private final Map<String, Object> persistDaos;
	private final Map<String, Long> daoHeartbeats;
	private final DbcpSingleton dbcpSingleton;
	
}
