package com.hiekn.spider.sfe4j.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.alibaba.fastjson.JSON;
import com.hiekn.sfe4j.dao.ESingleton;

public class EsTest {
	
	public static void main(String[] args) throws IOException {
		
		String queryJson = "{ 'query': { 'bool': { 'must': [], 'must_not': [], 'should': [ { 'match_all': {} } ] } } }";
		queryJson = JSON.parseObject(queryJson).toJSONString();
		
		BoolFilterBuilder boolf = FilterBuilders.boolFilter();
		boolf.mustNot(FilterBuilders.existsFilter("shaver_sfe"));
		
		
		ESingleton esingleton = new ESingleton("localhost", 9300, "elasticsearch");
		TransportClient transportClient = esingleton.transportClient();
		SearchResponse sr = transportClient.prepareSearch("news")
				.setTypes("news_data")
				.setSource(queryJson)
				.setPostFilter(boolf)
				.addFields("title")
				.setPostFilter(boolf)
				.execute()
				.actionGet();
		
		SearchHits searchHits = sr.getHits();
		
		System.out.println(searchHits.totalHits());
		
		final List<String> _ids = new ArrayList<>();
		
		SearchHit[] hits = searchHits.hits();
		for (SearchHit hit : hits) {
			_ids.add(hit.getId());
			System.out.println(hit.field("title").getValue());
		}
		
		
		BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
		for (String _id : _ids) {
			bulkRequestBuilder.add(transportClient.prepareUpdate("news", "news_data", _id)
					.setDoc(XContentFactory.jsonBuilder()
							.startObject()
							.field("shaver_sfe", System.currentTimeMillis())
							.endObject()));
		}
		if (bulkRequestBuilder.numberOfActions() > 0)
			bulkRequestBuilder.execute().actionGet();
		
		
		esingleton.close();
	}
	
	
}
