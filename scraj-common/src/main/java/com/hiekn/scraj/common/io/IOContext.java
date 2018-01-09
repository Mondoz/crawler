package com.hiekn.scraj.common.io;

import java.util.HashMap;
import java.util.Map;

import com.hiekn.scraj.common.io.dao.DbcpSingleton;
import com.hiekn.scraj.common.io.dao.ESingleton;
import com.hiekn.scraj.common.io.dao.MongoSingleton;

public class IOContext {
	
	public final void writeDao(String key, Object dao) {
		daoInstanceMaps.put(key, dao);
		daoHeartbeatMaps.put(key, System.currentTimeMillis());
	}
	public final Object readDao(String key) {
		daoHeartbeatMaps.put(key, System.currentTimeMillis());
		return daoInstanceMaps.get(key);
	}
	public final Map<String, Object> daoInstanceMaps() {
		return daoInstanceMaps;
	}
	
	public final Map<String, Long> daoHeartbeatMaps() {
		return daoHeartbeatMaps;
	}
	
	public final void clear() {
		for (Map.Entry<String, Object> daoEntery : daoInstanceMaps.entrySet()) {
			Object dao = daoEntery.getValue();
			if (dao instanceof MongoSingleton) ((MongoSingleton) dao).close();
			else if (dao instanceof ESingleton) ((ESingleton) dao).close();
			else if (dao instanceof DbcpSingleton) ((DbcpSingleton) dao).close();
		}
		//
		daoInstanceMaps.clear();
		//
		daoHeartbeatMaps.clear();
	}
	
	{
		daoInstanceMaps = new HashMap<String, Object>();
		daoHeartbeatMaps = new HashMap<String, Long>();
	}
	private Map<String, Object> daoInstanceMaps;// dao实例对象的缓存
	private Map<String, Long> daoHeartbeatMaps;// dao实例的最后心跳
}
