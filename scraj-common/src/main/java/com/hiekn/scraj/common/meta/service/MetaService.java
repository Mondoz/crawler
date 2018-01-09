package com.hiekn.scraj.common.meta.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.hiekn.scraj.common.meta.domain.Metadata;

/**
 * 
 * 
 * 
 * @author pzn
 * @version 1.0
 * @since 1.7
 * @date 2016/07/29
 */
public interface MetaService extends Remote {
	
	Logger LOGGER = Logger.getLogger(MetaService.class);
	
	Gson GSON = new Gson();
	
	// meta存储的介质
	String META_ENGINE_TYPE_MONGODB = "mongodb";
	String META_ENGINE_TYPE_MYSQL = "mongodb";
	String META_ENGINE_TYPE_FILE = "file";
	
	//
	String GET_META_WITH_INTERVAL_MILLIS_SQL = "SELECT id, name,"
			+ " interval_millis, conf, conf_parallel, cookie"
			+ " FROM ripper_config WHERE switch_flag = 1 AND completed = 1 AND interval_type = 0"
			+ " AND conf_type = ? AND next_time_millis < ?"
			+ " ORDER BY next_time_millis ASC , conf_priority DESC LIMIT 1";
	
	String GET_META_WITH_INTERVAL_EXP_SQL = "SELECT id, name,"
			+ " interval_exp, conf, conf_priority, conf_parallel, cookie"
			+ " FROM ripper_config where switch_flag = 1 AND completed = 1 AND interval_type = 1"
			+ " AND conf_type = ?"
			+ " ORDER BY id ASC , conf_priority DESC LIMIT ?, ?";
	
	/**
	 * 
	 * 根据采集任务类型获取需要运行的毫秒周期任务
	 * 
	 * @param confType
	 * @return
	 * @throws RemoteException
	 * @throws SQLException
	 * @throws CloneNotSupportedException
	 */
	Metadata getIntervalMillisMetaByConfType(int confType) throws RemoteException, SQLException, CloneNotSupportedException;
	
	/**
	 * 
	 * @param confType
	 * @param limit
	 * @return
	 * @throws RemoteException
	 * @throws SQLException
	 */
	List<Metadata> getIntervalExpMetaByConfType(int confType, int limit) throws  RemoteException, SQLException;
	
	/**
	 * 延迟爬虫下次抓取时间
	 * 
	 * @param id
	 * @param delay
	 * @throws RemoteException
	 * @throws SQLException
	 * @throws ClassNotFoundException 
	 */
	void delayMeta(int id, long delay) throws RemoteException, SQLException, ClassNotFoundException;
	
	/**
	 * meta node 销毁时  
	 * 
	 * 用于清除一些资源占用的方法
	 */
	void clear() throws RemoteException;
}
