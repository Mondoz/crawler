package com.hiekn.scraj.common.dedup;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hiekn.scraj.common.dedup.service.DedupService;
import com.hiekn.scraj.common.dedup.service.impl.DedupServiceImpl;
import com.hiekn.scraj.common.io.IOContext;
import com.hiekn.scraj.common.io.dao.DbcpSingleton;
import com.hiekn.scraj.common.io.dao.ESingleton;
import com.hiekn.scraj.common.io.dao.MongoSingleton;
import com.hiekn.scraj.common.io.dao.RedisSingleton;
import com.hiekn.scraj.common.util.ConstResource;

/**
 * 
 * 排重rmi服务<br>
 * 
 * rmi:
 * 默认监听端口:27311<br>
 * 默认服务名称:DedupNode
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 */
public class DedupNode {
	
	static final Logger LOGGER = Logger.getLogger(DedupNode.class);
	
	private static final long DAO_EXPIRE_MILLIS = 60 * 60 * 1000;// 1 hour
	
	static DedupServiceImpl dedupService;
	
	public static void main(String[] args) throws Exception {
		LOGGER.info("start dedup node init.");
		// 
		System.setProperty("java.rmi.server.hostname", ConstResource.DEDUP_NODE_HOSTNAME);
		
		IOContext context = new IOContext();
		dedupService = new DedupServiceImpl(context);
		
		Registry aregistry = LocateRegistry.createRegistry(ConstResource.DEDUP_NODE_REGISTRY_PORT);
		DedupService aStub = (DedupService) UnicastRemoteObject.exportObject(dedupService, ConstResource.DEDUP_NODE_REGISTRY_PORT);
		// Bind the remote object's stub in the registry
		aregistry.bind(ConstResource.DEDUP_NODE_NAME, aStub);
		
		LOGGER.info("start dedup node done.");
		
		// add hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOGGER.info("stop dedup node init.");
				dedupService.clear();
				LOGGER.info("stop dedup node done.");
			}
		});
		
		// check context
		while (true) {
			// sleep for a while
			TimeUnit.MILLISECONDS.sleep(600000);
			//
			Map<String, Object> daos = context.daoInstanceMaps();
			Map<String, Long> hbs = context.daoHeartbeatMaps();
			Set<String> expiredDaoKeys = new HashSet<>();
			long now = System.currentTimeMillis();
			for (Map.Entry<String, Long> hb : hbs.entrySet()) {
				String key = hb.getKey();
				long lastActiveMillis = hb.getValue();
				// 超过最大闲置时间
				// 标记超时的key
				if (lastActiveMillis + DAO_EXPIRE_MILLIS < now) expiredDaoKeys.add(key);
			}//
			
			// 释放所有超时key的dao资源
			for (String key : expiredDaoKeys) {
				hbs.remove(key);
				Object dao = daos.remove(key);
				
				if (dao instanceof MongoSingleton) {
					((MongoSingleton) dao).close();
				} else if (dao instanceof ESingleton) {
					((ESingleton) dao).close();
				} else if (dao instanceof RedisSingleton) {
					((RedisSingleton) dao).close();
				} else if (dao instanceof DbcpSingleton) {
					((DbcpSingleton) dao).close();
				}
			}//
			
		}
	}
	
}
