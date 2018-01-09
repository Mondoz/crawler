package com.hiekn.scraj.uyint.fix;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.common.meta.domain.Metadata;
import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.act.StaticDedupAct;
import com.hiekn.scraj.uyint.common.act.StaticStoreDataAct;
import com.hiekn.scraj.uyint.common.core.ApacheActor;
import com.hiekn.scraj.uyint.factory.BeanFactory;
import com.hiekn.sfe4j.util.StringUtils;

public class UyintFixTask implements Runnable {
	
	public UyintFixTask(StaticDedupAct mDedupAct, StaticStoreDataAct mStoreDataAct) {
		this.mStaticDedupAct = mDedupAct;
		this.mStaticStoreDataAct = mStoreDataAct;
	}
	
	public void acceptTask(boolean isAcceptTask) {
		this.isAcceptTask = isAcceptTask;
	}
	
	public void run() {
		// 每个线程一个独立的actor
		ApacheActor actor = new ApacheActor(mStaticDedupAct, mStaticStoreDataAct);
		//
		while (isAcceptTask) {
			try {
				// 1. take
				Metadata meta = BeanFactory.metaServiceProxy().getIntervalMillisMetaByConfType(0);
				if (null == meta) {
					LOGGER.info("there is no task need to run on this moment, while sleep 5 minutes.");
					// sleep
					TimeUnit.MINUTES.sleep(5);
					//
					continue;
				}
				
				// 2. execute
				long beginMillis = System.currentTimeMillis();
				int taskId = meta.id;
				String taskName = meta.name;
				int parallel = meta.confParallel;
				String commandJson = meta.conf;
				String cookies = meta.cookie;
				if (!StringUtils.isNullOrEmpty(cookies)) {
					List<String> cookieList = StaticAct.GSON.fromJson(cookies, new TypeToken<List<String>>() {}.getType());
					actor.actContext().writeCookies(cookieList);
					actor.actContext().resetCookie();
				}
				LOGGER.info("prepare execute task [" + taskName + "].");
				int resultSize = actor.perform(taskId, taskName, parallel, commandJson);
				LOGGER.info("post execute task [" + taskName + "].");
				
				// 3. report
				BeanFactory.monitorService().writeTaskResult(taskId, taskName, resultSize, beginMillis);
				
				// 延迟当前meta下次抓取时间
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
			} catch (InterruptedException e) {
				e.printStackTrace();
				LOGGER.error("InterruptedException in task core thread, exception msg... " + e + ", this task core thread will exit.");
				throw new RuntimeException(e);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("Exception in task core thread, exception msg... " + e);
			} finally {
				// 4. reset.
				actor.reset();
			}
		}// end of while true
	}
	
	
	private static final Logger LOGGER = Logger.getLogger(UyintFixTask.class);
	private volatile boolean isAcceptTask = true;
	private StaticDedupAct mStaticDedupAct;
	private StaticStoreDataAct mStaticStoreDataAct;
}
