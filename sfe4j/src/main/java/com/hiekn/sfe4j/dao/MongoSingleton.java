package com.hiekn.sfe4j.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * 
 * 里面维护一个MongoClient
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 *
 */
public class MongoSingleton {
	
	/**
	 * log
	 */
	private static final Logger LOGGER = Logger.getLogger(MongoSingleton.class);
	
	/**
	 * mongo url
	 */
	private final String host;

	/**
	 * mongo port
	 */
	private final int port;

	/**
	 * mongo 重连次数
	 */
	private static final int MONGO_RETRY_TIMES = 5;
	
	
	//
	private MongoClient mgClient = null;

	public MongoSingleton(String host, int port) throws Exception {
		
		this.host = host;
		this.port = port;
		
		if (!init(MONGO_RETRY_TIMES)) {
			LOGGER.error("尝试 " + MONGO_RETRY_TIMES + " 次不能连上mongodb...");
			throw new Exception("failed connect to mongodb...");
		}
	}
	
	/**
	 * 
	 * 初始化mgClient、MongoDatabase、MongoCollection
	 * 
	 * 当设置tryTimes 大于 0  时，表示最大的尝试重连次数。如果尝试连接的次数
	 * 
	 * 等于tryTimes，还是不能成功初始化mgClient、MongoDatabase、MongoCollection.
	 * 
	 * 则会返回false，表示初始化失败，否则，返回true。
	 * 
	 * @param tryTimes 连接失败时，尝试连接的最大次数.必须大于等于1，否则不会初始化mongo
	 * @return false 初始化失败 , true 初始化成功
	 */
	private boolean init(int tryTimes) {

		if (tryTimes <= 0) {
			return false;
		}

		try {
			LOGGER.info("init mongo client ... start");
			MongoClientOptions options = MongoClientOptions.builder()
					.connectionsPerHost(8).minConnectionsPerHost(1)
					.maxConnectionIdleTime(3000).maxConnectionLifeTime(180000)
					.connectTimeout(10000).socketTimeout(120000).build();
			
			String[] urls = host.split(",");
			List<ServerAddress> list = new ArrayList<ServerAddress>();
			for (String url : urls) {
				list.add(new ServerAddress(url, port));
			}
			
			mgClient = new MongoClient(list, options);
			LOGGER.info("init mongo client ... done");
			return true;
		} catch (Exception e) {
			LOGGER.error("Exception " + e);
			return init(--tryTimes);//尝试重新连接
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public MongoClient mongoClient() {
		return mgClient;
	}
	
	/**
	 * 
	 */
	public void close() {
		LOGGER.info("close mongo client ... start");
		if (null != mgClient) {
			mgClient.close();
			mgClient = null;
		}
		LOGGER.info("close mongo client ... done");
	}
	
	/**
	 * 
	 * 
	 * db.sim_hash.aggregate(
	 *  	[
     * 			{
     * 				$group : {
     *     				_id : null,
     *     				total: { $sum: "$simi_count"},
     *     			count: { $sum: 1 }
     *  			}
     *			}
   	 *		]
     *	)  
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
}
