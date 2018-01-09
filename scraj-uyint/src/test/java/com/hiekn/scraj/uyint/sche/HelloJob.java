package com.hiekn.scraj.uyint.sche;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {
	
	private static final Logger LOGGER = Logger.getLogger(HelloJob.class);
	
	private String name;
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		JobDataMap jdm = context.getJobDetail().getJobDataMap();
		
		name = jdm.getString("name");
		
		LOGGER.info(" name ... " + name);
		
		try {
			TimeUnit.MILLISECONDS.sleep(500);
			
			LOGGER.info(" sleep name ... " + name);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
