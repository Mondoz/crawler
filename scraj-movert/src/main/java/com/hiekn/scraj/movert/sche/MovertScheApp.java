package com.hiekn.scraj.movert.sche;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.hiekn.scraj.common.meta.domain.Metadata;
import com.hiekn.scraj.movert.act.BrowserDedupActImpl;
import com.hiekn.scraj.movert.act.BrowserStoreDataActImpl;
import com.hiekn.scraj.movert.common.act.BrowserDedupAct;
import com.hiekn.scraj.movert.common.act.BrowserStoreDataAct;
import com.hiekn.scraj.movert.common.core.BrowserActor;
import com.hiekn.scraj.movert.factory.BeanFactory;
import com.hiekn.scraj.movert.util.ConstResource;
import com.hiekn.sfe4j.util.StringUtils;

public class MovertScheApp {
	
	/**
	 * 
	 * movert schedule main().
	 * 
	 * @param args
	 * @throws RemoteException
	 * @throws SQLException
	 * @throws SchedulerException
	 */
	public static void main(String[] args) throws RemoteException, SQLException, SchedulerException {
		final BrowserDedupAct browserDedupAct = new BrowserDedupActImpl();
		final BrowserStoreDataAct browserStoreDataAct = new BrowserStoreDataActImpl();
		
		int nJobs = ConstResource.SCHE_JOB_NUMS;
		MovertScheApp app = new MovertScheApp("quartz.properties", browserDedupAct, browserStoreDataAct);
		app.initScheduleJobs(nJobs);
	}
	
	/**
	 * 
	 * @param nJobs
	 * @throws RemoteException
	 * @throws SQLException
	 * @throws SchedulerException
	 */
	public void initScheduleJobs(int nJobs) throws RemoteException, SQLException, SchedulerException {
		List<Metadata> rsList = BeanFactory.metaServiceProxy().getIntervalExpMetaByConfType(1, nJobs);
		int size = rsList.size();
		LOGGER.info("add into scheduler jobs size " + size);
		if (size > 0) {
			for (Metadata meta : rsList) {
				BrowserActor actor = new BrowserActor(browserDedupAct, browserStoreDataAct);
				JobDataMap jdm = new JobDataMap();
				jdm.put("id", meta.id);
				jdm.put("name", meta.name);
				jdm.put("conf", meta.conf);
				jdm.put("actor", actor);
				
				int confPriority = meta.confPriority;
				String cronExpression = meta.intervalExp;
				
				JobDetail job = JobBuilder.newJob(MovertScheJob.class).withIdentity("myJob" + meta.id, SCHEDULER_GROUP_NAME)
						.setJobData(jdm).build();
				
				// Cron Trigger the job to run now
				Trigger trigger = TriggerBuilder.newTrigger()
						.withIdentity("trigger" + meta.id, SCHEDULER_GROUP_NAME)
						.withPriority(confPriority)
						.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
						.forJob(job)
						.build();
				
				// 把任务加入到Scheduler
				sched.scheduleJob(job, trigger);
			}
		}
	}
	
	/**
	 * 
	 * 关闭调度器
	 * 
	 * @param waitForJobsToComplete
	 * @throws SchedulerException
	 */
	public void shutdown(boolean waitForJobsToComplete) {
		try {
			LOGGER.info("shutdown SpiderScheduledExecutor ... start.");
			if (!sched.isShutdown()) sched.shutdown(waitForJobsToComplete);
			LOGGER.info("shutdown SpiderScheduledExecutor ... done.");
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public MovertScheApp(String confPath, BrowserDedupAct browserDedupAct, BrowserStoreDataAct browserStoreDataAct) throws SchedulerException {
		LOGGER.info("init quartz Scheduler ... start .");
		// 初始化SchedulerFactory
		if (!StringUtils.isNullOrEmpty(confPath)) {
			schedFact = new StdSchedulerFactory(confPath);
		} else {
			schedFact = new StdSchedulerFactory();
		}
		
		// 初始化Scheduler
		sched = schedFact.getScheduler();
		LOGGER.info("init quartz Scheduler ... done .");
		
		// init
		this.browserDedupAct = browserDedupAct;
		this.browserStoreDataAct = browserStoreDataAct;
	}
	
	private BrowserDedupAct browserDedupAct;
	private BrowserStoreDataAct browserStoreDataAct;
	
	//
	private SchedulerFactory schedFact;
	private Scheduler sched;
	
	//
	private static final String SCHEDULER_GROUP_NAME = "myGroup";
	
	private static final Logger LOGGER = Logger.getLogger(MovertScheApp.class);
}
