package com.hiekn.sfe4j.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hiekn.sfe4j.core.Sfe4jContext;
import com.hiekn.sfe4j.core.Sliver;
import com.hiekn.sfe4j.dao.ESingleton;
import com.hiekn.sfe4j.dao.MongoSingleton;
import com.hiekn.sfe4j.util.ConstResource;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;

public class SliverImpl implements Sliver {
	
	public List<Map<String, Object>> readData(final List<Map<String, Object>> rsList, 
			String paramsJson, 
			Sfe4jContext context) throws Exception {
		
		LOGGER.info("sliver read data ... init. request size ... " + BATCH_SIZE);
		
		if (null == rsList) throw new NullPointerException("保存的数据的List集合 不能 为  null");
		
		// all _id list
		final List<String> _ids = new ArrayList<>();
		
		JSONObject params = JSON.parseObject(paramsJson);
		JSONArray fields = params.getJSONArray("fields");
		JSONObject sliverMeta = params.getJSONObject("sliverMeta");
		String type = sliverMeta.getString("sliverType");
		switch (type) {
		case MONGODB_TYPE:
			//
			String mongodbHost = sliverMeta.getString("host");
			int mongodbPort = sliverMeta.getIntValue("port");
			String mongodbDb = sliverMeta.getString("database");
			String mongodbColl = sliverMeta.getString("collection");
			JSONObject mongodbQuery = JSON.parseObject(sliverMeta.getString("query"));
			// 查询条件
			Document $query = new Document(mongodbQuery).append("shaver_sfe", new Document("$exists", false));
			// 限制返回字段
			Document $projection = new Document();
			for (int i = 0; i < fields.size(); i++) {
				String field = fields.getString(i);
				$projection.append(field, 1);
				
			}
			Block<Document> parseBlock = new Block<Document>() {
			     @Override
			     public void apply(final Document document) {
			    	 rsList.add(document);
			    	 _ids.add(document.getString("_id"));
			     }
			};
			
			// 
			String mognoKey = DigestUtils.md5Hex(mongodbHost + mongodbPort);//
			MongoSingleton mongoSingleton = (MongoSingleton) context.readDao(mognoKey);
			if (null == mongoSingleton) {
				mongoSingleton = new MongoSingleton(mongodbHost, mongodbPort);
				context.writeDao(mognoKey, mongoSingleton);
			}
			MongoCollection<Document> coll = mongoSingleton.mongoClient().getDatabase(mongodbDb).getCollection(mongodbColl);
			coll.createIndex(new Document("shaver_sfe", 1));
			
			// query
			coll.find($query).projection($projection).skip(0).limit(BATCH_SIZE).forEach(parseBlock);
			
			// update 
			// add a new filed: [shaver_sfe] indicate this document is handled
			coll.updateMany(new Document("_id", new Document("$in", _ids)), 
					new Document("$set", new Document("shaver_sfe", System.currentTimeMillis())));
			
			break;
		case ELASTICSEARCH_TYPE:
			String esHost = sliverMeta.getString("host");
			int esPort = sliverMeta.getIntValue("port");
			String esName = sliverMeta.getString("name");
			String esIndex = sliverMeta.getString("index");
			String esType = sliverMeta.getString("type");
			String esQuery = JSON.parseObject(sliverMeta.getString("query")).toJSONString();
			
			BoolFilterBuilder boolf = FilterBuilders.boolFilter()
					.mustNot(FilterBuilders.existsFilter("shaver_sfe"));
			
			String esKey = DigestUtils.md5Hex(esHost + esPort);//
			ESingleton esingleton = (ESingleton) context.readDao(esKey);
			if (null == esingleton) {
				esingleton = new ESingleton(esHost, esPort, esName);
				context.writeDao(esKey, esingleton);
			}
			
			String[] includeFields = new String[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				includeFields[i] = fields.getString(i);
			}
			
			TransportClient transportClient = esingleton.transportClient();
			SearchResponse sr = transportClient.prepareSearch(esIndex)
				.setTypes(esType)
				.setSource(esQuery)// 设置json query
				.setPostFilter(boolf)
				.addFields(includeFields)
				.setFrom(0)
				.setSize(BATCH_SIZE)
				.execute()
				.actionGet();
			
			// query
			SearchHits searchHits = sr.getHits();
			Iterator<SearchHit> iterator = searchHits.iterator();
			while (iterator.hasNext()) {
				SearchHit searchHit = iterator.next();
				Map<String, Object> doc = new HashMap<>();
				_ids.add(searchHit.getId());
				doc.put("_id", searchHit.getId());
				for (String ifield : includeFields) {
					doc.put(ifield, searchHit.field(ifield).getValue());
				}
				rsList.add(doc);
			}
			
			// update
			// add a new filed: [shaver_sfe] indicate this document is handled
			BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
			for (String _id : _ids) {
				bulkRequestBuilder.add(transportClient.prepareUpdate(esIndex, esType, _id)
						.setDoc(XContentFactory.jsonBuilder()
								.startObject()
								.field("shaver_sfe", System.currentTimeMillis())
								.endObject()));
			}
			if (bulkRequestBuilder.numberOfActions() > 0) bulkRequestBuilder.execute().actionGet();
			break;
		case MYSQL_TYPE:
			
			break;
		}
		
		LOGGER.info("sliver read data ... done. response size ... " + rsList.size());
		
		return rsList;
	}
	
	private static final String MONGODB_TYPE = "mongodb";
	private static final String ELASTICSEARCH_TYPE = "elasticsearch";
	private static final String MYSQL_TYPE = "mysql";
	private static final int BATCH_SIZE = ConstResource.TASK_BATCH_SIZE;
}
