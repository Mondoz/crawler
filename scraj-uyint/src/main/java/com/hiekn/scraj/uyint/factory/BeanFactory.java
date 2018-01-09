package com.hiekn.scraj.uyint.factory;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

import com.hiekn.scraj.common.dedup.service.DedupService;
import com.hiekn.scraj.common.meta.service.MetaService;
import com.hiekn.scraj.common.monitor.service.MonitorService;
import com.hiekn.scraj.uyint.util.ConstResource;

/**
 * 
 * 远程接口代理工厂
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 *
 */
public class BeanFactory {
	
	//
	static final Logger LOGGER = Logger.getLogger(BeanFactory.class);
	
	//
	static MetaService metaService;
	static DedupService dedupService;
	static MonitorService monitorService;
	
	public static final MetaService metaServiceProxy() {
		try {
			synchronized (BeanFactory.class) {
				if (null == metaService) {
					LOGGER.info("connect metadata rmi server ... start");
					
					Registry registry = LocateRegistry.getRegistry(ConstResource.META_NODE_HOSTNAME, ConstResource.META_NODE_REGISTRY_PORT);
					
					metaService = (MetaService) registry.lookup(ConstResource.META_NODE_NAME);
					
					LOGGER.info("connect metadata rmi server ... done");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("no rmi bind in port " + ConstResource.META_NODE_REGISTRY_PORT
					+ " or no rmi bind in name " + ConstResource.META_NODE_NAME);
		}
		return metaService;
	}
	
	public static final DedupService dedupServiceProxy() {
		try {
			synchronized (BeanFactory.class) {
				if (null == dedupService) {
					LOGGER.info("connect dedup rmi server ... start");
					
					Registry registry = LocateRegistry.getRegistry(ConstResource.DEDUP_NODE_HOSTNAME, ConstResource.DEDUP_NODE_REGISTRY_PORT);
					
					dedupService = (DedupService) registry.lookup(ConstResource.DEDUP_NODE_NAME);
					
					LOGGER.info("connect dedup rmi server ... done");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("no rmi bind in port " + ConstResource.DEDUP_NODE_REGISTRY_PORT
					+ " or no rmi bind in name " + ConstResource.DEDUP_NODE_NAME);
		}
		return dedupService;
	}
	
	
	public static final MonitorService monitorService() {
		try {
			synchronized (BeanFactory.class) {
				if (null == monitorService) {
					LOGGER.info("connect monitor rmi server ... start");
					
					Registry registry = LocateRegistry.getRegistry(ConstResource.MONITOR_NODE_HOSTNAME, ConstResource.MONITOR_NODE_REGISTRY_PORT);
					
					monitorService = (MonitorService) registry.lookup(ConstResource.MONITOR_NODE_NAME);
					
					LOGGER.info("connect monitor rmi server ... done");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("no rmi bind in port " + ConstResource.MONITOR_NODE_REGISTRY_PORT
					+ " or no rmi bind in name " + ConstResource.MONITOR_NODE_NAME);
		}
		return monitorService;
	}
	
}
