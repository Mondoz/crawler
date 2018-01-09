package com.hiekn.scraj.common.dedup.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * 
 * DedupService
 * 
 * @author pzn
 * @since 1.7
 * @version 1.0
 */
public interface DedupService extends Remote {
	
	// 
	Logger LOGGER = Logger.getLogger(DedupService.class);
	
	/**
	 * 
	 * @param paramsMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> dedup(Map<String, Object> paramsMap) throws Exception;
	
	/**
	 * 用于释放一些连接资源
	 */
	void clear() throws RemoteException;
}
