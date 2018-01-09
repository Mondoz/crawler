package com.hiekn.scraj.uyint.fix;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hiekn.scraj.uyint.act.StaticDedupActImpl;
import com.hiekn.scraj.uyint.act.StaticStoreDataActImpl;
import com.hiekn.scraj.uyint.common.act.StaticDedupAct;
import com.hiekn.scraj.uyint.common.act.StaticStoreDataAct;
import com.hiekn.scraj.uyint.util.ConstResource;

/**
 * 
 * uyint 采集  
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/25
 *
 */
public class UyintFixApp {
	
	/**
	 * uyint app main().
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// 线程池
		int nThreads = ConstResource.SPIDER_THREAD_COUNT;
		final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
		
		// 提交任务到线程池
		final StaticDedupAct dedupAct = new StaticDedupActImpl();
		final StaticStoreDataAct storeDataAct = new StaticStoreDataActImpl();
		final UyintFixTask uyintFixTask = new UyintFixTask(dedupAct, storeDataAct);
		for (int i = 0; i < nThreads; i++) pool.submit(uyintFixTask);
		
		// shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOGGER.error("stop uyint app process init.");
				
				// do not take new task
				uyintFixTask.acceptTask(false);
				try {
					// don not start new thread
					pool.shutdown();
					pool.awaitTermination(60, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// clear
				storeDataAct.clear();
				
				LOGGER.error("stop uyint app process done.");
			}
		});
		
		// check thread pool
		while (true) {
			//
			// main thread do sleep with interval
			TimeUnit.MILLISECONDS.sleep(ConstResource.SPIDER_THREAD_CHECK_INTERVAL);
			
			LOGGER.info("before uyint app main thread check thread pool status, active thread count... " + pool.getActiveCount());
			// if active thread less than the thread pool size,
			// submit some task to pool.
			if (pool.getActiveCount() < nThreads) {
				for (int i = pool.getActiveCount(); i <= nThreads; i++) {
					LOGGER.info("uyint app main thread sumit a task to thread pool.");
					pool.submit(uyintFixTask);
				}
			}
			LOGGER.info("after uyint app main thread check thread pool status, active thread count... " + pool.getActiveCount());
			//
		}
	}
	
	
	private static final Logger LOGGER = Logger.getLogger(UyintFixApp.class);
	
}
