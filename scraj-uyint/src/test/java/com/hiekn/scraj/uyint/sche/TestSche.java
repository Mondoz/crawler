package com.hiekn.scraj.uyint.sche;

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

public class TestSche {
		
	 
//	 cronSchedule
//	  
//Seconds	YES	0-59	, - * /
//Minutes	YES	0-59	, - * /
//Hours	YES	0-23	, - * /
//Day of month	YES	1-31	, - * ? / L W
//Month	YES	1-12 or JAN-DEC	, - * /
//Day of week	YES	1-7 or SUN-SAT	, - * ? / L #
//Year	NO	empty, 1970-2099	, - * /
//
//
//	* */5 * ? * *  在每五分钟内   只要有满足条件的任务就会执行     比如： 一个任务只需要执行1秒    那么这个任务最多会被执行60次   直到5分钟的下一分钟结束
//	0 */5 * ? * *  每五分钟开始的时候执行  比如：一个任务只需要执行1秒 那么执行结束就结束了，不会重复执行
//  注意这两个表达式 区分






	
	
	public static void main(String[] args) throws SchedulerException {
		SchedulerFactory sf = new StdSchedulerFactory("quartz.properties");
		Scheduler scheduler = sf.getScheduler();

		// Scheduler will not execute jobs until it has been started (though they can be scheduled before start())
		scheduler.start();
		
		JobDataMap jdm1 = new JobDataMap();
		jdm1.put("name", "job1");
		// Define job instance
		JobDetail job1 = JobBuilder.newJob(HelloJob.class)
		    .withIdentity("job1", "group")
		    .setJobData(jdm1)
		    .storeDurably()
		    .build();
		
		
		JobDataMap jdm2 = new JobDataMap();
		jdm2.put("name", "job2");
		JobDetail job2 = JobBuilder.newJob(HelloJob.class)
			    .withIdentity("job2", "group")
			    .setJobData(jdm2)
			    .build();
		
		
		Trigger trigger1 = TriggerBuilder.newTrigger()
			    .withIdentity("trigger1", "group")
			    .withSchedule(CronScheduleBuilder.cronSchedule("* */5 * ? * *"))
			    .startNow()
			    .build();
		
		Trigger trigger2 = TriggerBuilder.newTrigger()
			    .withIdentity("trigger2", "group")// withMisfireHandlingInstructionNowWithExistingCount
			    .withSchedule(CronScheduleBuilder.cronSchedule("* */2 * * * ?"))
			    .startNow()
			    .build();
		
		// Schedule the job with the trigger
		scheduler.scheduleJob(job1, trigger1);
		scheduler.scheduleJob(job2, trigger2);
		
	}
}
