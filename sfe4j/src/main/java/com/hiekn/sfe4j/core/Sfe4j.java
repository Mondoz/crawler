package com.hiekn.sfe4j.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hiekn.sfe4j.core.impl.EliverImpl;
import com.hiekn.sfe4j.core.impl.FliverImpl;
import com.hiekn.sfe4j.core.impl.SliverImpl;
import com.hiekn.sfe4j.util.ConstResource;


/**
 * 
 * 
 * 
 * 
 * 
 * @author pzn
 * @since 1.7
 * @version 2016/07/19
 *
 */
public class Sfe4j {
	
	public static void main(String[] args) throws Exception {
		Sfe4jContext context = new Sfe4jContext();
		
		Sfe4j sfe4j = new Sfe4j(context);
		
		String json = "{ 'sliver': { 'fields': [ 'invest_area' ], 'sliverMeta': { 'sliverType': 'mongodb', 'host': 'localhost', 'port': 27017, 'database': 'itjz', collection: 'itjz_tzjg', query: { } } }, 'fliver': [ { 'field': 'invest_area', 'formatter': { 'defaults': [ ], 'customs': [ { 'type': 'sfe4j.replacer', 'regex': '<li>', 'replacement': '<br>', useMulti: true } ] } } ], 'eliver': { operation: 'update', 'eliverMeta': { 'eliverType': 'mongodb', 'host': 'localhost', 'port': 27017, 'database': 'itjz', 'collection': 'itjz_tzjg' } } }";
		
		sfe4j.execute(json);
		
		context.clear();
	}
	
	
	public Sfe4j(Sfe4jContext context) {
		
		this.sliver = new SliverImpl();
		this.fliver = new FliverImpl();
		this.eliver = new EliverImpl();
		
		this.context = context;
	}
	
	public void execute(String taskJson) throws Exception {
		
		JSONObject task = JSON.parseObject(taskJson);
		
		String sliverJson = task.getString("sliver");
		String fliverJson = task.getString("fliver");
		String eliverJson = task.getString("eliver");
		
		List<Map<String, Object>> data = new ArrayList<>();
		
		while (true) {
			
			sliver.readData(data, sliverJson, context);
			
			if (data.size() == 0) break;
			
			// 这个
			fliver.clean(data, fliverJson);
			
			//
			eliver.writeData(data, eliverJson, context);
			
			data.clear();
			
			TimeUnit.MILLISECONDS.sleep(batchSleep);
		}
	}
	
	private final int batchSleep = ConstResource.TASK_THREAD_BATCH_SLEEP_MILLIS;
	
	private final SliverImpl sliver;
	private final FliverImpl fliver;
	private final EliverImpl eliver;
	private final Sfe4jContext context;
}
