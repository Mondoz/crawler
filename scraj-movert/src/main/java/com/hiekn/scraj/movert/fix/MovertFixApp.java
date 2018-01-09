package com.hiekn.scraj.movert.fix;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hiekn.scraj.movert.act.BrowserDedupActImpl;
import com.hiekn.scraj.movert.act.BrowserStoreDataActImpl;
import com.hiekn.scraj.movert.common.act.BrowserDedupAct;
import com.hiekn.scraj.movert.common.act.BrowserStoreDataAct;
import com.hiekn.scraj.movert.util.ConstResource;

public class MovertFixApp {
	
	/**
	 * 
	 * movert app main().
	 * 
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		
		// init thread pool
		int nThreads = ConstResource.SPIDER_THREAD_COUNT;
		final ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
		
		// submit task
		final BrowserDedupAct browserDedupAct = new BrowserDedupActImpl();
		final BrowserStoreDataAct browserStoreDataAct = new BrowserStoreDataActImpl();
		final MovertFixTask movertFixTask = new MovertFixTask(browserDedupAct, browserStoreDataAct);
		for (int i = 0; i < nThreads; i++) pool.submit(movertFixTask);
		
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOGGER.error("stop movert app init.");
				
				// do not take new task
				movertFixTask.acceptTask(false);
				try {
					// don not start new thread
					pool.shutdown();
					pool.awaitTermination(60, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// clear
				browserStoreDataAct.clear();
				
				LOGGER.error("stop movert app done.");
			}
		});
		
		// check task
		while (true) {
			// main thread do sleep with interval
			TimeUnit.MILLISECONDS.sleep(ConstResource.SPIDER_THREAD_CHECK_INTERVAL);
			
			LOGGER.info("before movert app main thread check thread pool status, active thread count... " + pool.getActiveCount());
			// if active thread less than the thread pool size,
			// submit some task to pool.
			if (pool.getActiveCount() < nThreads) {
				for (int i = pool.getActiveCount(); i <= nThreads; i++) {
					LOGGER.info("movert app main thread sumit a task to thread pool.");
					pool.submit(movertFixTask);
				}
			}
			LOGGER.info("after movert app main thread check thread pool status, active thread count... " + pool.getActiveCount());
		}
		
	}
	
	private static final Logger LOGGER = Logger.getLogger(MovertFixApp.class);
	
}
