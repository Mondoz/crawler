package com.hiekn.scraj.common.io.dao;

import java.io.Closeable;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * 
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 */
public class RedisSingleton implements Closeable {
	//
	private final String host;
	//
	private final int port;
	//
	private JedisPool jedisPool;
	
	public RedisSingleton(String host, int port) {
		this.host = host;
		this.port = port;
		
		init();
	}
	
	/**
	 * 获取redis连接
	 * @return
	 */
	public Jedis jedis() { return jedisPool.getResource(); }
	
	/**
	 * 
	 * 关闭redis连接池
	 */
	public void close() {
		if (!jedisPool.isClosed()) jedisPool.close();
	}
	
	private void init() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
    	poolConfig.setMinEvictableIdleTimeMillis(3000L);
    	
    	//
    	jedisPool = new JedisPool(poolConfig, host, port);
	}
}
