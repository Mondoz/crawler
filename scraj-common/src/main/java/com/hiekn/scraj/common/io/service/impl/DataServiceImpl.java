package com.hiekn.scraj.common.io.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;

import com.hiekn.scraj.common.io.IOContext;
import com.hiekn.scraj.common.io.dao.DbcpSingleton;
import com.hiekn.scraj.common.io.dao.ESingleton;
import com.hiekn.scraj.common.io.dao.MongoSingleton;
import com.hiekn.scraj.common.io.service.DataService;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;

public class DataServiceImpl implements DataService {
	
	
	
	public int writeData(List<Map<String, Object>> kvList, Map<String, Object> storeMeta, IOContext context) throws Exception {
		LOGGER.info("write data init.");
		
		if (null == kvList || kvList.size() == 0) return 0;
		
		int resultSize = kvList.size();
		//
		String storeEngine = (String) storeMeta.get("engine");
		switch (storeEngine) {
		case STORE_ENGINE_MONGODB:
			String mongoHost = (String) storeMeta.get("host");
    		int mongoPort = ((Double) storeMeta.get("port")).intValue();
    		String mongoKey = DigestUtils.md5Hex(mongoHost + mongoPort);
    		MongoSingleton mongoSingleton = (MongoSingleton) context.readDao(mongoKey);
			if (null == mongoSingleton) {
				mongoSingleton = new MongoSingleton(mongoHost, mongoPort);
				context.writeDao(mongoKey, mongoSingleton);
			}
    		
    		String mongoDb = (String) storeMeta.get("database");
    		String mongoColl = (String) storeMeta.get("collection");
    		MongoCollection<Document> dataColl = mongoSingleton.mongoClient().getDatabase(mongoDb).getCollection(mongoColl);
    		
    		if (1 == resultSize) { // 单条数据入库
    			Map<String, Object> map = kvList.get(0);
    			String _id = UUID.randomUUID().toString().replaceAll("-", "p");
    			Document $doc = new Document(map).append("_id", _id);
    			try {
    				dataColl.insertOne($doc);
    			} catch(MongoWriteException e) {
    				e.printStackTrace();
    			}
    		} else {// 批量入库
    			
    			List<Document> $docs = new ArrayList<>();
    			for (Map<String, Object> map : kvList) {
    				String _id = UUID.randomUUID().toString().replaceAll("-", "p");
    				$docs.add(new Document(map).append("_id", _id));
    				// 分批入库
    				if ($docs.size() > 100) {
    					try {
    						dataColl.insertMany($docs, new InsertManyOptions().ordered(false));
    						// clear
    						$docs.clear();
    					} catch(MongoBulkWriteException e) {
    						e.printStackTrace();
    					}
    				}
    			}
    			
    			// 最后一批入库
    			if ($docs.size() > 0) {
    				try {
    					dataColl.insertMany($docs, new InsertManyOptions().ordered(false));
    				} catch(MongoBulkWriteException e) {
    					e.printStackTrace();
    				}
    			}
    		}
			break;
		case STORE_ENGINE_ELASICSEARCH:
			String esName = (String) storeMeta.get("clusterName");
    		String esHost = (String) storeMeta.get("host");
    		int esPort = ((Double) storeMeta.get("port")).intValue();
    		String esIndex = (String) storeMeta.get("index");
    		String esType = (String) storeMeta.get("type");
    		
    		String esKey = DigestUtils.md5Hex(esHost + esPort);
    		ESingleton eSingleton = (ESingleton) context.readDao(esKey);
    		if (null == eSingleton) {
    			eSingleton = new ESingleton(esHost, esPort, esName);
    			context.writeDao(esKey, eSingleton);
    		}
    		
    		if (1 == resultSize) { // 单条数据入库
    			Map<String, Object> map = kvList.get(0);
    			String _id = UUID.randomUUID().toString().replaceAll("-", "p");
    			try {
    				eSingleton.getClient()
    				.prepareIndex(esIndex, esType, _id)
    				.setSource(map)
    				.execute()
    				.actionGet();
    			} catch(ElasticsearchException e) {
    				e.printStackTrace();
    			}
    		} else {// 批量入库
    			//
    			BulkRequestBuilder bulk = eSingleton.getClient().prepareBulk();
    			for (Map<String, Object> map : kvList) {
    				String _id = UUID.randomUUID().toString().replaceAll("-", "p");
    				
    				bulk.add(eSingleton.getClient().prepareIndex(esIndex, esType, _id).setSource(map));
    				
    				if (bulk.numberOfActions() > 100) {
    					try {
    						bulk.execute().actionGet();
    					} catch(ElasticsearchException e) {
    						e.printStackTrace();
    					} finally {
    						bulk = null;
    						bulk = eSingleton.getClient().prepareBulk();
    					}
    				}
    			}
    			
    			// 最后一批入库
    			if (bulk.numberOfActions() > 0) {
    				try {
						bulk.execute().actionGet();
					} catch(ElasticsearchException e) {
						e.printStackTrace();
					}
    			}
    		}
			break;
		case STORE_ENGINE_MYSQL:
			String jdbcDriver = (String) storeMeta.get("driver");
    		String jdbcUrl = (String) storeMeta.get("url");
    		String jdbcUser = (String) storeMeta.get("user");
    		String jdbcPassword = (String) storeMeta.get("password");
    		String jdbcTable = (String) storeMeta.get("table");
    		
    		String jdbcKey = DigestUtils.md5Hex(jdbcUrl);
    		DbcpSingleton dbcpSingleton = (DbcpSingleton) context.readDao(jdbcKey);
    		if (null == dbcpSingleton) {
    			dbcpSingleton = new DbcpSingleton(jdbcDriver, jdbcUrl, jdbcUser, jdbcPassword);
    			context.writeDao(jdbcKey, dbcpSingleton);
    		}
    		
    		//
    		String sasqlPrefix = "INSERT INTO " + jdbcTable;
    		String sasqlCol = "";
    		String sasqlVal = "";
    		List<String> keys = new ArrayList<>();
    		Map<String, Object> mm = kvList.get(0);
    		for (String k : mm.keySet()) {
    			sasqlCol += k + ",";
    			sasqlVal += "?,";
    			keys.add(k);
    		}
    		
    		sasqlCol = " (" + sasqlCol.substring(0, sasqlCol.length() - 1) + ")";
    		sasqlVal = " VALUES (" + sasqlVal.substring(0, sasqlVal.length() - 1) + ")";
    		String sasql = sasqlPrefix + sasqlCol + sasqlVal;
    		
    		try (Connection conn = dbcpSingleton.getConnection();
    			 PreparedStatement ps = conn.prepareStatement(sasql)) {
    			if (1 == resultSize) { // 单条数据入库
    				Map<String, Object> mone = kvList.get(0);
    				for (int k = 0, len = keys.size(); k < len; k++) {
    					ps.setString(k + 1, mone.get(keys.get(k)).toString());
    				}
    				
    				ps.executeUpdate();
    			} else {// 批量入库
    				int zi = 0;
    				for (Map<String, Object> mone : kvList) {
    					for (int k = 0, len = keys.size(); k < len; k++) {
    						ps.setString(k + 1, mone.get(keys.get(k)).toString());
    					}
    					
    					ps.addBatch();
    					
    					//
    					if (++zi > 100) {
    						ps.executeBatch();
    						ps.clearBatch();
    						zi = 0;
    					}
    				}
    				
    				if (zi > 0) {
    					ps.executeBatch();
    					ps.clearBatch();
    				}
    			}
    		};
			break;
		case STORE_ENGINE_API:
			String apiUrl = (String) storeMeta.get("url");
    		String apiParams = (String) storeMeta.get("params");
    		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
    			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
    			if (null != apiParams && apiParams.length() > 0) {
    				String[] pas = apiParams.split(";");
    				for (String ps : pas) {
    					formparams.add(new BasicNameValuePair(ps.split("=")[0], ps.split("=")[1]));
    				}
    			}
    			//
    			formparams.add(new BasicNameValuePair("kvList", GSON.toJson(kvList)));
    			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
    			HttpPost httppost = new HttpPost(apiUrl);
    			httppost.setEntity(entity);
    			httpClient.execute(httppost);
    		};
			break;
		}
		
		LOGGER.info("write data done, size: " + resultSize);
		
		return resultSize;
	}
	
}
