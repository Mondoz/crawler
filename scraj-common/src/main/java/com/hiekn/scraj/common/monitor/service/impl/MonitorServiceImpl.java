package com.hiekn.scraj.common.monitor.service.impl;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bson.Document;

import com.hiekn.scraj.common.io.dao.MongoSingleton;
import com.hiekn.scraj.common.monitor.service.MonitorService;
import com.hiekn.scraj.common.util.ConstResource;

public class MonitorServiceImpl implements MonitorService {
	
	public MonitorServiceImpl() throws Exception {
		mongoSingleton = new MongoSingleton(host, port);
	}
	
	public void writeTaskResult(int taskId, String taskName, int resultSize, long beginMillis) throws RemoteException {
		//
		LOGGER.info("write task result init. taskId... " + taskId + ", taskName... " + taskName + ", resultSize... " + resultSize);
		
		long now = System.currentTimeMillis();
		String _id = UUID.randomUUID().toString().replaceAll("-", "p");
		Document $doc = new Document("_id", _id).append("task_id", taskId)
				.append("task_name", taskName)
				.append("result_size", resultSize)
				.append("begin_date", STD_DATE_FORMATE.format(beginMillis))
				.append("begin_time", STD_TIME_FORMATE.format(beginMillis))
				.append("begin_time_millis", beginMillis)
				.append("report_date", STD_DATE_FORMATE.format(now))
				.append("report_time", STD_TIME_FORMATE.format(now))
				.append("report_time_millis", now);
		//
		mongoSingleton.mongoClient()
		.getDatabase(SCRAPY_MONITOR_DATABASE)
		.getCollection(SCRAPY_RESULT_COLLECTION)
		.insertOne($doc);
		
		LOGGER.info("write task result done.");
	}
	
	/**
	 * 结束时  释放资源
	 */
	public final void clear() {
		if (null != mongoSingleton) {
			mongoSingleton.close();
			mongoSingleton = null;
		}
	}
	
	
	//
	final String host = ConstResource.getString("monitor.node.mongodb.host", "localhost");
	final int port = ConstResource.getInteger("monitor.node.mongodb.port", 27017);
	final String SCRAPY_MONITOR_DATABASE = ConstResource.getString("monitor.node.mongodb.database", "monitor");
	final String SCRAPY_RESULT_COLLECTION = ConstResource.getString("monitor.node.result.collection", "result");
	
	final DateFormat STD_DATE_FORMATE = new SimpleDateFormat("yyyy-MM-dd");
	final DateFormat STD_TIME_FORMATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//
	//
	private MongoSingleton mongoSingleton;
	
}
