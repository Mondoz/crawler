package com.hiekn.scraj.rest.service;

import org.bson.Document;

import com.hiekn.scraj.rest.factory.BeanFactory;
import com.hiekn.scraj.rest.util.ConstResource;
import com.mongodb.client.MongoCollection;

public class VisualService {
	
	
	public static String getHtml(String _id) throws Exception {
		String html = "";
		Document $q = new Document("_id", _id);
		MongoCollection<Document> coll = BeanFactory.mongoSingleton().mongoClient().getDatabase(ConstResource.VISUAL_MONGO_DATABASE).getCollection(ConstResource.VISUAL_MONGO_COLLECTION);
		Document first = coll.find($q).projection(new Document("html", 1)).first();
		if (null != first) {
			html = first.getString("html");
		}
		return html;
	}
	
	public static void saveHtml(String _id, String url, String html) throws Exception {
		Document $doc = new Document("_id", _id).append("html", html).append("persist_millis", System.currentTimeMillis());
		MongoCollection<Document> coll = BeanFactory.mongoSingleton().mongoClient().getDatabase(ConstResource.VISUAL_MONGO_DATABASE).getCollection(ConstResource.VISUAL_MONGO_COLLECTION);
		coll.insertOne($doc);
	}
	
}
