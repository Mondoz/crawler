package com.hiekn.scraj.common.monitor;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

import com.hiekn.scraj.common.monitor.service.MonitorService;
import com.hiekn.scraj.common.monitor.service.impl.MonitorServiceImpl;
import com.hiekn.scraj.common.util.ConstResource;

public class MonitorNode {
	
	static final Logger LOGGER = Logger.getLogger(MonitorNode.class);
	
	static MonitorServiceImpl monitorService;
	
	public static void main(String[] args) throws Exception {
		
		//
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOGGER.info("stop monitor node init.");
				monitorService.clear();
				LOGGER.info("stop monitor node done.");
			}
		});
		
		
		//
		LOGGER.info("start monitor node init.");
		
		// 
		System.setProperty("java.rmi.server.hostname", ConstResource.MONITOR_NODE_HOSTNAME);
				
		monitorService = new MonitorServiceImpl();
		
		Registry aregistry = LocateRegistry.createRegistry(ConstResource.MONITOR_NODE_REGISTRY_PORT);
		MonitorService aStub = (MonitorService) UnicastRemoteObject.exportObject(monitorService, ConstResource.MONITOR_NODE_REGISTRY_PORT);
		aregistry.bind(ConstResource.MONITOR_NODE_NAME, aStub);
		
		LOGGER.info("start monitor node done.");
	}
}
