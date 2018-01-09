package com.hiekn.scraj.common.dedup.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;

import com.hiekn.scraj.common.dedup.service.DedupService;
import com.hiekn.scraj.common.io.IOContext;
import com.hiekn.scraj.common.io.dao.DbcpSingleton;
import com.hiekn.scraj.common.io.dao.MongoSingleton;
import com.hiekn.scraj.common.io.dao.RedisSingleton;
import com.mongodb.client.MongoCollection;

import redis.clients.jedis.Jedis;

@SuppressWarnings("unchecked")
public class DedupServiceImpl implements DedupService {
	
	/* static final variables */
	private static final String DEDUP_ENGINE_MONGODB = "mongodb"; 
	private static final String DEDUP_ENGINE_JDBC = "jdbc"; 
	private static final String DEDUP_ENGINE_REDIS = "redis"; 
	
	private static final String DEDUP_STRATEGY_MD5 = "md5"; 
	/* static final variables */
	
	//
	private Lock dedupLock = new ReentrantLock();
	
	//
	public DedupServiceImpl(IOContext context) {
		this.context = context;
	}
	
	public List<Map<String, Object>> dedup(Map<String, Object> paramsMap) throws Exception {
		// 待排重列表
		List<Map<String, Object>> kvList = (List<Map<String, Object>>) paramsMap.get("kvList");
    	// 进行排重的字段
    	List<String> fields = (List<String>) paramsMap.get("fields");
    	// 对应数据库的字段
    	List<String> mappingFields = (List<String>) paramsMap.get("mappingFields");
    	
    	String strategy = DEDUP_STRATEGY_MD5;
    	
    	Map<String, Object> meta = (Map<String, Object>) paramsMap.get("dedupMeta");
    	String dedupEngine = (String) meta.get("engine");
    	try {
    		dedupLock.lock();
    		
    		LOGGER.info("befor dedup ... size " + kvList.size());
    		if (DEDUP_ENGINE_MONGODB.equals(dedupEngine)) {
    			String host = (String) meta.get("host");
    			int port = ((Double) meta.get("port")).intValue();
    			
    			// 先取
    			String key = DigestUtils.md5Hex(host + port);
    			MongoSingleton mongoSingleton = (MongoSingleton) context.readDao(key);
    			if (null == mongoSingleton) {
    				mongoSingleton = new MongoSingleton(host, port);
    				context.writeDao(key, mongoSingleton);
    			}
    			
    			String database = (String) meta.get("database");
    			String collection = (String) meta.get("collection");
    			MongoCollection<Document> dupColl = mongoSingleton.mongoClient().getDatabase(database).getCollection(collection);
    			if (DEDUP_STRATEGY_MD5.equals(strategy)) {
    				//
    				if (null != mappingFields && mappingFields.size() > 0 && mappingFields.size() == fields.size()) {
    					for (int i = kvList.size() - 1; i > -1; i--) {
    						Map<String, Object> map = kvList.get(i);
        					Map<String, Object> dedupMap = new HashMap<>();
        					for (int j = 0, jsize = fields.size(); j < jsize; j++) {
        						dedupMap.put(mappingFields.get(j), DigestUtils.md5Hex(map.get(fields.get(j)).toString()));
        					}
        					
        					//
        					Document $doc = new Document(dedupMap);
        					
        					// 排重
        					if (null == dupColl.find($doc).first()) {// 不存在
        						dupColl.insertOne($doc);
        					} else {// 已经存在
        						kvList.remove(i);
        					}
    					}
    				} else {
    					for (int i = kvList.size() - 1; i > -1; i--) {
    						Map<String, Object> map = kvList.get(i);
        					Map<String, Object> dedupMap = new HashMap<>();
        					for (String field : fields) {
        						if (map.get(field) != null) {
        							dedupMap.put(field, DigestUtils.md5Hex(map.get(field).toString()));
        						}
        					}
        					
        					//
        					Document $doc = new Document(dedupMap);
        					
        					// 排重
        					if (null == dupColl.find($doc).first()) {// 不存在
        						dupColl.insertOne($doc);
        					} else {// 已经存在
        						kvList.remove(i);
        					}
    					}
    				}
    			}
    			
    		} else if (DEDUP_ENGINE_JDBC.equals(dedupEngine)) {
    			String driver = (String) meta.get("driver");
    			String url = (String) meta.get("url");
    			String user = (String) meta.get("user");
    			String password = (String) meta.get("password");
    			String table = (String) meta.get("table");
    			
    			String key = DigestUtils.md5Hex(url);
    			DbcpSingleton dbcpSingleton = (DbcpSingleton) context.readDao(key);
    			if (null == dbcpSingleton) {
    				dbcpSingleton = new DbcpSingleton(driver, url, user, password);
    				context.writeDao(key, dbcpSingleton);
    			}
    			
    			//
    			Connection conn = dbcpSingleton.getConnection();
    			if (DEDUP_STRATEGY_MD5.equals(strategy)) {
    				
    				String exsqlPrefix = "SELECT * FROM " + table + " WHERE ";
    				String sasqlPrefix = "INSERT INTO " + table + " ";
    				if (null != mappingFields && mappingFields.size() > 0 && mappingFields.size() == fields.size()) {
    					for (int i = kvList.size() - 1; i > -1; i--) {
    						//
    						String exsqlCond = "";
    						String sasqlCol = "";
    						String sasqlVal = "";
    						
    						Map<String, Object> map = kvList.get(i);
    						for (int j = 0, jsize = fields.size(); j < jsize; j++) {
    							exsqlCond += mappingFields.get(j) + " = '" + DigestUtils.md5Hex(map.get(fields.get(j)).toString()) + "' AND ";
    							
    							//
    							sasqlCol += mappingFields.get(j) + ",";
    							sasqlVal += "'" + DigestUtils.md5Hex(map.get(fields.get(j)).toString()) + "',";
    						}
    						
    						// 最后一个and直接加上一个条件
    						exsqlCond += " 1 = 1";
    						sasqlCol = " (" + sasqlCol.substring(0, sasqlCol.length() - 1) + ")";
    						sasqlVal = " VALUES (" + sasqlVal.substring(0, sasqlVal.length() - 1) + ")";
    						
    						// 先查询  如果存在  则删除  继续处理下一个
    						try (PreparedStatement queryPs = conn.prepareStatement(exsqlPrefix + exsqlCond);
    								ResultSet rs = queryPs.executeQuery()) {
    							if (rs.next()) {
    								kvList.remove(i);
    								continue;
    							}
    						};
    						
    						// 如果不存在
    						// 插入数据库
    						try (PreparedStatement savePs = conn.prepareStatement(sasqlPrefix + sasqlCol + sasqlVal)) {
    							savePs.executeUpdate();
    						};
    					}
    					
    				} else {
    					for (int i = kvList.size() - 1; i > -1; i--) {
    						//
    						String exsqlCond = "";
    						String sasqlCol = "";
    						String sasqlVal = "";
    						
    						Map<String, Object> map = kvList.get(i);
    						for (int j = 0, jsize = fields.size(); j < jsize; j++) {
    							exsqlCond += fields.get(j) + " = '" + DigestUtils.md5Hex(map.get(fields.get(j)).toString()) + "' AND ";
    							
    							//
    							sasqlCol += fields.get(j) + ",";
    							sasqlVal += "'" + DigestUtils.md5Hex(map.get(fields.get(j)).toString()) + "',";
    						}
    						
    						// 最后一个and直接加上一个条件
    						exsqlCond += " 1 = 1";
    						sasqlCol = " (" + sasqlCol.substring(0, sasqlCol.length() - 1) + ")";
    						sasqlVal = " VALUES (" + sasqlVal.substring(0, sasqlVal.length() - 1) + ")";
    						
    						// 先查询  如果存在  则删除  继续处理下一个
    						try (PreparedStatement queryPs = conn.prepareStatement(exsqlPrefix + exsqlCond);
    								ResultSet rs = queryPs.executeQuery()) {
    							if (rs.next()) {
    								kvList.remove(i);
    								continue;
    							}
    						};
    						
    						// 如果不存在
    						// 插入数据库
    						try (PreparedStatement savePs = conn.prepareStatement(sasqlPrefix + sasqlCol + sasqlVal)) {
    							savePs.executeUpdate();
    						};
    					}
    				}
    			}
    			
    			// 
    			conn.close();
    			
    		} else if (DEDUP_ENGINE_REDIS.equals(dedupEngine)) {
    			//
    			String host = (String) meta.get("host");
    			int port = null == meta.get("port") ? 6379 : ((Double) meta.get("port")).intValue();
    			int dbIndex = null == meta.get("dbIndex") ? 0 : ((Double) meta.get("dbIndex")).intValue();
    			String setKey = (String) meta.get("key");
    			// 先取
    			String key = DigestUtils.md5Hex(host + port);
    			//
    			RedisSingleton redisSingleton = (RedisSingleton) context.readDao(key);
    			if (null == redisSingleton) {
    				redisSingleton = new RedisSingleton(host, port);
    				context.writeDao(key, redisSingleton);
    			}
    			//
    			Jedis jedis = redisSingleton.jedis();
    			if (dbIndex > 0) jedis.select(dbIndex);
    			
    			if (DEDUP_STRATEGY_MD5.equals(strategy)) {
    				//
    				String dedupField = fields.get(0);
    				for (int i = kvList.size() - 1; i > -1; i--) {
    					Map<String, Object> map = kvList.get(i);
    					//
    					String val = DigestUtils.md5Hex(map.get(dedupField).toString());
    					//
    					if (jedis.sismember(setKey, val)) {
    						kvList.remove(i);
    					} else {
    						jedis.sadd(setKey, val);
    					}
    				}
    			}
    			
    			// close
    			jedis.close();
    		}
    		
    		//
    		LOGGER.info("after dedup ... size " + kvList.size());
    	} finally {
    		//
    		dedupLock.unlock();
    	}
    	
    	return kvList;
	}
	
	public void clear() {
		if (null != context) {
			context.clear();
			context = null;
		}
	}
	
	private IOContext context;
}
