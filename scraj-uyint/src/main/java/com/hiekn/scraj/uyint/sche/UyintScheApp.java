package com.hiekn.scraj.uyint.sche;

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
import com.hiekn.scraj.uyint.act.StaticDedupActImpl;
import com.hiekn.scraj.uyint.act.StaticStoreDataActImpl;
import com.hiekn.scraj.uyint.common.act.StaticDedupAct;
import com.hiekn.scraj.uyint.common.act.StaticStoreDataAct;
import com.hiekn.scraj.uyint.common.core.ApacheActor;
import com.hiekn.scraj.uyint.factory.BeanFactory;
import com.hiekn.scraj.uyint.util.ConstResource;
import com.hiekn.sfe4j.util.StringUtils;

public class UyintScheApp {
	
	public static void main(String[] args) throws SchedulerException, RemoteException, SQLException {
		final StaticDedupAct dedupAct = new StaticDedupActImpl();
		final StaticStoreDataAct storeDataAct = new StaticStoreDataActImpl();
		
		int nJobs = ConstResource.SCHE_JOB_NUMS;
		UyintScheApp app = new UyintScheApp("quartz.properties", dedupAct, storeDataAct);
		app.initScheduleJobs(nJobs);
	}
	
	
	public UyintScheApp(String confPath, StaticDedupAct mDedupAct, StaticStoreDataAct mStoreDataAct) throws SchedulerException {
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
		
		
		// 初始化act
		this.mDedupAct = mDedupAct;
		this.mStoreDataAct = mStoreDataAct;
	}
	
	
	public void initScheduleJobs(int nJobs) throws RemoteException, SQLException, SchedulerException {
		
		List<Metadata> rsList = BeanFactory.metaServiceProxy().getIntervalExpMetaByConfType(0, nJobs);
		
		int size = rsList.size();
		LOGGER.info("add into scheduler jobs size " + size);
		if (size > 0) {
			if (!sched.isStarted()) sched.start();
			for (Metadata meta : rsList) {
				// 
				ApacheActor actor = new ApacheActor(mDedupAct, mStoreDataAct);
				
				//
				JobDataMap jdm = new JobDataMap();
				jdm.put("id", meta.id);
				jdm.put("name", meta.name);
				jdm.put("conf", meta.conf);
				jdm.put("cookies", meta.cookie);
				jdm.put("actor", actor);
				
				int confPriority = meta.confPriority;
				String cronExpression = meta.intervalExp;
				
				JobDetail job = JobBuilder.newJob(UyintScheJob.class).withIdentity("myJob" + meta.id, SCHEDULER_GROUP_NAME)
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
	
	private StaticDedupAct mDedupAct;
	private StaticStoreDataAct mStoreDataAct; 
	
	private SchedulerFactory schedFact;
	private Scheduler sched;
	
	private static final String SCHEDULER_GROUP_NAME = "myGroup";
	
	private static final Logger LOGGER = Logger.getLogger(UyintScheApp.class);
	
}
