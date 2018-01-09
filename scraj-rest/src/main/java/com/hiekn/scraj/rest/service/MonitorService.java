package com.hiekn.scraj.rest.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchHits;

import com.google.gson.Gson;
import com.hiekn.scraj.rest.dao.ESingleton;
import com.hiekn.scraj.rest.factory.BeanFactory;
import com.hiekn.scraj.rest.util.ConstResource;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MonitorService {
	
	public static Map<String,Object> getMonitorInfo (int pageNo,int pageSize,String queryDate) throws Exception {
		final ArrayList<Document> infoList = new ArrayList<Document>();
		long size = 0;
		Map<String,Object> map = new HashMap<String, Object>();
		MongoClient client = BeanFactory.mongoSingleton().mongoClient();
		MongoDatabase db = client.getDatabase(ConstResource.MONITOR_MONGO_DATABASE);
		Gson gson = new Gson();
		List<Document> $agg = Arrays.asList(
				new Document("$match",new Document("report_date",queryDate)),
				new Document("$group",new Document("_id","$task_id").append("task_name", new Document("$first","$task_name"))
						.append("report_time", new Document("$max","$report_time")).append("size", new Document("$sum","$result_size"))),
				new Document("$sort",new Document("report_time",-1)),
				new Document("$limit",pageNo*pageSize),
				new Document("$skip",(pageNo-1)*pageSize));
		System.out.println(gson.toJson($agg));
		AggregateIterable<Document> iterable = db.getCollection(ConstResource.MONITOR_MONGO_COLLECTION).aggregate($agg);
		iterable.forEach(new Block<Document>(){
			public void apply(final Document document) {
				infoList.add(document);
			}
		});
		DistinctIterable<Integer> iter = db.getCollection(ConstResource.MONITOR_MONGO_COLLECTION).distinct("task_id", new Document("report_date",queryDate),Integer.class);
		MongoCursor<Integer> it = iter.iterator();
		while (it.hasNext()) {
			it.next();
			size++;
		}
		map.put("size",size);
		map.put("data",infoList);
		return map;
	}
	
	
	public static Map<String, Object> getMonitorData(String taskId, int pageNo, int pageSize) throws Exception {
		Map<String, Object> rsMap = new HashMap<>();
		
		//
		int rsCount = 0;
		List<Map<String, Object>> rsList = new ArrayList<>();
		
		int fromIndex = (pageNo - 1) * pageSize;
		ESingleton esingleton = BeanFactory.esingleton();
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (null == taskId || taskId.length() == 0) throw new RuntimeException("查询该任务的数据时，任务的id不能为空...");
		query.must(QueryBuilders.termQuery("portalId", taskId)).must(QueryBuilders.termQuery("presistDate", D_F.format(new Date())));
		TransportClient client = esingleton.getClient();
		
		SearchResponse rs = client.prepareSearch("news").setTypes("news_data")
				.setQuery(query)
				.addFields("title", "url", "source", "publishTime", "sentiment", "simhash")//
				.setFrom(fromIndex)
				.setSize(pageSize)
				.execute()
				.actionGet();
		SearchHits searchHits = rs.getHits();
		
		//
		rsCount = (int) searchHits.getTotalHits();
		SearchHit[] hits = searchHits.hits();
		for (SearchHit sh : hits) {
			Map<String, SearchHitField> fields = sh.getFields();
			
			Map<String, Object> smap = new HashMap<>();
			smap.put("id", sh.getId());
			smap.put("title", fields.get("title").value());
			smap.put("url", fields.get("url").value());
			smap.put("source", fields.get("source").value());
			smap.put("publishTime", fields.get("publishTime").value());
			smap.put("sentiment", fields.get("sentiment").value());
			smap.put("simhash", fields.get("simhash").value());
			
			rsList.add(smap);
		}
		
		rsMap.put("rsCount", rsCount);
		rsMap.put("rsData", rsList);
		
		return rsMap;
	}
	
	public static Map<String, Object> getMonitorDataInfo(String id) throws Exception {
		
		MongoClient client = BeanFactory.mongoSingleton().mongoClient();
		MongoCollection<Document> coll = client.getDatabase("data").getCollection("news_data");
		
		Document $doc = coll.find(new Document("_id", id)).first();
		if (null != $doc) $doc.put("id", $doc.get("_id"));
		
		return $doc;
	}
	
	static final SimpleDateFormat D_F = new SimpleDateFormat("yyyy-MM-dd");
	
}
