package com.hiekn.sfe4j.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hiekn.sfe4j.core.Eliver;
import com.hiekn.sfe4j.core.Sfe4jContext;
import com.hiekn.sfe4j.dao.ESingleton;
import com.hiekn.sfe4j.dao.MongoSingleton;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOneModel;

public class EliverImpl implements Eliver {
	
	public int writeData(List<Map<String, Object>> odata, String paramsJson, Sfe4jContext context) throws Exception {
		
		LOGGER.info("eliver write data ... init. size ... " + odata.size());
		
		JSONObject params = JSON.parseObject(paramsJson);
		String operation = params.getString("operation");
		JSONObject eliverMeta = params.getJSONObject("eliverMeta");
		String type = eliverMeta.getString("eliverType");
		switch (operation) {
		case UPDATE_OPERATION:
			switch (type) {
			case MONGODB_TYPE:
				//
				String mongodbHost = eliverMeta.getString("host");
				int mongodbPort = eliverMeta.getIntValue("port");
				String mongodbDb = eliverMeta.getString("database");
				String mongodbColl = eliverMeta.getString("collection");
				
				// 
				List<UpdateOneModel<Document>> $bulkDocs = new ArrayList<>();
				for (Map<String, Object> data : odata) {
					String _id = (String) data.remove("_id");
					Document $doc = new Document(data);
					$bulkDocs.add(new UpdateOneModel<Document>(new Document("_id", _id), new Document("$set", $doc)));
				}
				
				String key = DigestUtils.md5Hex(mongodbHost + mongodbPort);//
				MongoSingleton mongoSingleton = (MongoSingleton) context.readDao(key);
				if (null == mongoSingleton) {
					mongoSingleton = new MongoSingleton(mongodbHost, mongodbPort);
					context.writeDao(key, mongoSingleton);
				}
				MongoCollection<Document> coll = mongoSingleton.mongoClient()
						.getDatabase(mongodbDb)
						.getCollection(mongodbColl);
				coll.bulkWrite($bulkDocs,  new BulkWriteOptions().ordered(false));
				break;
			case ELASTICSEARCH_TYPE:
				String esHost = eliverMeta.getString("host");
				int esPort = eliverMeta.getIntValue("port");
				String esName = eliverMeta.getString("name");
				String esIndex = eliverMeta.getString("index");
				String esType = eliverMeta.getString("type");
				
				String esKey = DigestUtils.md5Hex(esHost + esPort);//
				ESingleton esingleton = (ESingleton) context.readDao(esKey);
				if (null == esingleton) {
					esingleton = new ESingleton(esHost, esPort, esName);
					context.writeDao(esKey, esingleton);
				}
				
				BulkRequestBuilder bulkRequestBuilder = esingleton.transportClient().prepareBulk();
				for (Map<String, Object> data : odata) {
					String _id = (String) data.remove("_id");
					bulkRequestBuilder.add(new UpdateRequest(esIndex, esType, _id).doc(data));
				}
				if (bulkRequestBuilder.numberOfActions() > 0) bulkRequestBuilder.execute().actionGet();
				break;
			case MYSQL_TYPE:
				break;
			}
			break;
		case INSERT_OPERATION:
			switch (type) {
			case MONGODB_TYPE:
				//
				String mongodbHost = eliverMeta.getString("host");
				int mongodbPort = eliverMeta.getIntValue("port");
				String mongodbDb = eliverMeta.getString("database");
				String mongodbColl = eliverMeta.getString("collection");
				
				// 
				List<Document> $bulkDocs = new ArrayList<>();
				for (Map<String, Object> data : odata) {
					data.remove("_id");
					$bulkDocs.add(new Document(data).append("_id", UUID.randomUUID().toString().replaceAll("-", "p")));
				}
				
				String key = DigestUtils.md5Hex(mongodbHost + mongodbPort);//
				MongoSingleton mongoSingleton = (MongoSingleton) context.readDao(key);
				if (null == mongoSingleton) {
					mongoSingleton = new MongoSingleton(mongodbHost, mongodbPort);
					context.writeDao(key, mongoSingleton);
				}
				MongoCollection<Document> coll = mongoSingleton.mongoClient()
						.getDatabase(mongodbDb)
						.getCollection(mongodbColl);
				coll.insertMany($bulkDocs, new InsertManyOptions().ordered(false));
				break;
			case ELASTICSEARCH_TYPE:
				String esHost = eliverMeta.getString("host");
				int esPort = eliverMeta.getIntValue("port");
				String esName = eliverMeta.getString("name");
				String esIndex = eliverMeta.getString("index");
				String esType = eliverMeta.getString("type");
				
				String esKey = DigestUtils.md5Hex(esHost + esPort);//
				ESingleton esingleton = (ESingleton) context.readDao(esKey);
				if (null == esingleton) {
					esingleton = new ESingleton(esHost, esPort, esName);
					context.writeDao(esKey, esingleton);
				}
				
				BulkRequestBuilder bulkRequestBuilder = esingleton.transportClient().prepareBulk();
				for (Map<String, Object> data : odata) {
					data.remove("_id");
					bulkRequestBuilder.add(new IndexRequest(esIndex, esType, UUID.randomUUID().toString().replaceAll("-", "p")).source(data));
				}
				if (bulkRequestBuilder.numberOfActions() > 0) bulkRequestBuilder.execute().actionGet();
				break;
			case MYSQL_TYPE:
				
				break;
			}
			break;
		}
		
		
		LOGGER.info("eliver write data ... done. size ... " + odata.size());
		
		return 0;
	}
	
	private static final String MONGODB_TYPE = "mongodb";
	private static final String ELASTICSEARCH_TYPE = "elasticsearch";
	private static final String MYSQL_TYPE = "mysql";
	
	private static final String INSERT_OPERATION = "insert";
	private static final String UPDATE_OPERATION = "update";
	
}
