package com.hiekn.sfe4j.dao;

import java.io.Closeable;

import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.hiekn.sfe4j.util.StringUtils;

/**
 * 
 * elastic search 
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 *
 */
public class ESingleton implements Closeable {
	
	static final Logger LOGGER = Logger.getLogger(ESingleton.class);
	
	private TransportClient client = null;
	
	public ESingleton(String host, int port, String clusterName) {
		LOGGER.info("init elastic search client ... start");
		
		if (StringUtils.isNullOrEmpty(clusterName)) clusterName = "elasticsearch";
		
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName).build();
		client = new TransportClient(settings);
		
		String[] hosts = host.split(",");

		for (String h : hosts) {
			client.addTransportAddress(new InetSocketTransportAddress(h, port));
		}
		LOGGER.info("init elastic search client ... done");
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	public TransportClient transportClient() {
		return client;
	}
	
	/**
	 */
	public void close(){
		LOGGER.info("close elastic search client ... start");
		if (client != null) {
			client.close();
		}
		LOGGER.info("close elastic search client ... done");
	}
	
}
