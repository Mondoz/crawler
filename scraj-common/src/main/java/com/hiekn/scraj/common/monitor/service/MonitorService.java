package com.hiekn.scraj.common.monitor.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

public interface MonitorService extends Remote {
	
	//
	//
	Logger LOGGER = Logger.getLogger(MonitorService.class);
	
	/**
	 * 
	 * 汇报采集的任务数据量
	 * 
	 * 
	 * @param id 任务id
	 * @param name 任务名
	 * @param size 抓取记录数
	 */
	void writeTaskResult(int taskId, String taskName, int resultSize, long beginMillis) throws RemoteException;
	
	/**
	 * 
	 * @throws RemoteException
	 */
	void clear() throws RemoteException;
}
