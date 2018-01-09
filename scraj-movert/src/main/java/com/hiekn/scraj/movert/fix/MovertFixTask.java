package com.hiekn.scraj.movert.fix;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hiekn.scraj.common.meta.domain.Metadata;
import com.hiekn.scraj.movert.common.act.BrowserDedupAct;
import com.hiekn.scraj.movert.common.act.BrowserStoreDataAct;
import com.hiekn.scraj.movert.common.core.BrowserActor;
import com.hiekn.scraj.movert.factory.BeanFactory;

public class MovertFixTask implements Runnable {
	//
	public void acceptTask(boolean isAcceptTask) {
		this.isAcceptTask = isAcceptTask;
	}
	//
	public void run() {
		// 每个线程一个独立的actor
		BrowserActor actor = new BrowserActor(mBrowserDedupAct, mBrowserStoreDataAct);
		
		//
		while (isAcceptTask) {
			try {
				// take
				Metadata meta = BeanFactory.metaServiceProxy().getIntervalMillisMetaByConfType(1);
				if (null == meta) {
					LOGGER.info("there is no task need to run on this moment, while sleep 5 minutes.");
					// sleep
					TimeUnit.MINUTES.sleep(5);
					//
					continue;
				}
				
				// execute
				long beginMillis = System.currentTimeMillis();
				int taskId = meta.id;
				String taskName = meta.name;
				int parallel = meta.confParallel;
				String commandJson = meta.conf;
				LOGGER.info("prepare execute task [" + taskName + "].");
				int resultSize = actor.perform(taskId, taskName, parallel, commandJson);
				LOGGER.info("post execute task [" + taskName + "].");
				
				// report
				BeanFactory.monitorService().writeTaskResult(taskId, taskName, resultSize, beginMillis);
				
				//
				if (resultSize < 1) {
					LOGGER.info(taskName + "  这个任务没有新数据   延迟下次采集时间.");
					BeanFactory.metaServiceProxy().delayMeta(taskId, 30 * 60 * 1000L);
				}
				
				// sleep for a while
				TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000) + 1);
				
			} catch (RemoteException e) {
				e.printStackTrace();
				LOGGER.error("RemoteException in task core thread, exception msg... " + e);
			} catch (SQLException e) {
				e.printStackTrace();
				LOGGER.error("SQLException in task core thread, exception msg... " + e);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				LOGGER.error("CloneNotSupportedException in task core thread, exception msg... " + e);
			} catch (InterruptedException e) {
				e.printStackTrace();
				LOGGER.error("InterruptedException in task core thread, exception msg... " + e);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("Exception in task core thread, exception msg... " + e);
			} finally {
				// any how: reset browser && static context.
				actor.reset();
			}
		}
		
	}
	
	public MovertFixTask(BrowserDedupAct mBrowserDedupAct, BrowserStoreDataAct mBrowserStoreDataAct) {
		this.mBrowserDedupAct = mBrowserDedupAct;
		this.mBrowserStoreDataAct = mBrowserStoreDataAct;
		//
		isAcceptTask = true;
	}
	
	private volatile boolean isAcceptTask;
	private BrowserDedupAct mBrowserDedupAct;
	private BrowserStoreDataAct mBrowserStoreDataAct;
	
	private static final Logger LOGGER = Logger.getLogger(MovertFixTask.class);
	
}
