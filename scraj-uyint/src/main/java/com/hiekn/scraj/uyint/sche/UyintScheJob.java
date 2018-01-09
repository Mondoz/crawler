package com.hiekn.scraj.uyint.sche;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.uyint.common.act.StaticAct;
import com.hiekn.scraj.uyint.common.core.ApacheActor;
import com.hiekn.scraj.uyint.factory.BeanFactory;
import com.hiekn.sfe4j.util.StringUtils;

@DisallowConcurrentExecution
public class UyintScheJob implements Job {
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jdm = context.getJobDetail().getJobDataMap();
		ApacheActor actor = (ApacheActor) jdm.get("actor");
		int id = jdm.getInt("id");
		String name = jdm.getString("name");
		String conf = jdm.getString("conf");
		String cookies = jdm.getString("cookies");
		long beginMillis = System.currentTimeMillis();
		try {
			
			// 循环处理单个爬虫任务
			LOGGER.info("开始处理爬虫任务 ... " + name);
			if (!StringUtils.isNullOrEmpty(cookies)) {
				List<String> cookieList = StaticAct.GSON.fromJson(cookies, new TypeToken<List<String>>() {}.getType());
				actor.actContext().writeCookies(cookieList);
				actor.actContext().resetCookie();
			}
			int resultSize = actor.perform(id, name, 0, conf);
			LOGGER.info("共抓取到 数据  size = " + resultSize);
			
			// 汇报抓取结果
			BeanFactory.monitorService().writeTaskResult(id, name, resultSize, beginMillis);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// any how reset
			actor.resetContextHttpReader();
		}
	}
	
	private static final Logger LOGGER = Logger.getLogger(UyintScheJob.class);
	
}
