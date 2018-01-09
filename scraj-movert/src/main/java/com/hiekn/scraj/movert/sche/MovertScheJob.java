package com.hiekn.scraj.movert.sche;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.hiekn.scraj.movert.common.core.BrowserActor;
import com.hiekn.scraj.movert.factory.BeanFactory;

public class MovertScheJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jdm = context.getJobDetail().getJobDataMap();
		int id = jdm.getInt("id");
		String name = jdm.getString("name");
		String conf = jdm.getString("conf");
		BrowserActor actor = (BrowserActor) jdm.get("actor");
		long beginMillis = System.currentTimeMillis();
		try {
			LOGGER.info("execute movert scheduled job init, name... [" + name + "]");
			
			//
			int resultSize = actor.perform(id, name, 0, conf);
			
			//
			// 汇报抓取结果
			BeanFactory.monitorService().writeTaskResult(id, name, resultSize, beginMillis);
			
			LOGGER.info("execute movert scheduled job done, name... [" + name + "], size... " + resultSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static final Logger LOGGER = Logger.getLogger(MovertScheJob.class);
	
}
