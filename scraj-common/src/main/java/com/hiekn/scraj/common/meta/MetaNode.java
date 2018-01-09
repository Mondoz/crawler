package com.hiekn.scraj.common.meta;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.log4j.Logger;

import com.hiekn.scraj.common.meta.service.MetaService;
import com.hiekn.scraj.common.meta.service.impl.MetaServiceImpl;
import com.hiekn.scraj.common.util.ConstResource;

/**
 * 
 * 
 * 
 * 
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 *
 */
public class MetaNode {
	
	static final Logger LOGGER = Logger.getLogger(MetaNode.class);
	
	static MetaServiceImpl metaService;
	
	public static void main(String[] args) throws RemoteException, AlreadyBoundException, ClassNotFoundException {
		//
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				LOGGER.info("stop meta node init.");
				metaService.clear();
				LOGGER.info("stop meta node done.");
			}
		});
		
		
		LOGGER.info("start meta node init.");
		// 
		System.setProperty("java.rmi.server.hostname", ConstResource.META_NODE_HOSTNAME);
		
		metaService = new MetaServiceImpl();
		
		Registry aregistry = LocateRegistry.createRegistry(ConstResource.META_NODE_REGISTRY_PORT);
		MetaService aStub = (MetaService) UnicastRemoteObject.exportObject(metaService, ConstResource.META_NODE_REGISTRY_PORT);
		// Bind the remote object's stub in the registry
		aregistry.bind(ConstResource.META_NODE_NAME, aStub);
		//
		LOGGER.info("start meta node done.");
	}
	
}
