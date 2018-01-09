package com.hiekn.sfe4j.exec;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hiekn.sfe4j.core.Sfe4jContext;
import com.hiekn.sfe4j.dao.ESingleton;
import com.hiekn.sfe4j.dao.MongoSingleton;
import com.hiekn.sfe4j.util.ConstResource;

public class SFEliver {
	
	public static void main(String[] args) throws Exception {
		
		int nThreads = ConstResource.TASK_THREAD_NUMS;
		final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
		final Sfe4jContext context = new Sfe4jContext();
		final SFEliverTask task = new SFEliverTask(context);
		for (int i = 0; i < nThreads; i++) {
			pool.submit(task);
		}
		
		// 正常结束进程  释放资源
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				
				LOGGER.info("destroying sfeliver process , clear data ... init");
				
				// 不要接受新任务
				task.accepTask(false);
				
				// 关闭线程池
				try {
					pool.shutdown();
					pool.awaitTermination(10, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// 清除一些残留数据
				// 关闭打开的资源等
				context.clear();
				
				LOGGER.info("destroyed sfeliver process , clear data ... done");
			}
		});
		
		while (true) {
			//
			TimeUnit.MINUTES.sleep(5);
			
			// 检查ThreadPool
			if (pool.getActiveCount() < nThreads) {
				for (int i = pool.getActiveCount(); i <= nThreads; i++) {
					pool.submit(task);
				}
			}
			
			// 检查dao
			Map<String, Long> daoHeartbeats = context.daoHeartbeats();
			Map<String, Object> persistDaos = context.persistDaos();
			Set<String> expiredDaoKeys = new HashSet<>();
			long now = System.currentTimeMillis();
			for (Map.Entry<String, Long> hb : daoHeartbeats.entrySet()) {
				String key = hb.getKey();
				long last = hb.getValue();
				// 超过最大闲置时间
				// 标记超时的key
				if (last + DAO_IDLE_MILLIS < now) expiredDaoKeys.add(key);
			}
			
			// 释放所有超时key的dao资源
			for (String key : expiredDaoKeys) {
				daoHeartbeats.remove(key);
				Object dao = persistDaos.remove(key);
				
				if (dao instanceof MongoSingleton) {
					((MongoSingleton) dao).close();
				} else if (dao instanceof ESingleton) {
					((ESingleton) dao).close();
				}
				
			}
		}
		
	}
	
	private static final long DAO_IDLE_MILLIS = 60 * 60 * 1000;// 1 hour
	private static final Logger LOGGER = Logger.getLogger(SFEliver.class);
	
}
