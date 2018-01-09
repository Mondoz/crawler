package com.hiekn.scraj.common.meta.service.impl;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.reflect.TypeToken;
import com.hiekn.scraj.common.io.dao.DbcpSingleton;
import com.hiekn.scraj.common.meta.domain.Metadata;
import com.hiekn.scraj.common.meta.service.MetaService;
import com.hiekn.scraj.common.util.ConstResource;

public class MetaServiceImpl implements MetaService {
	
	
	public static void main(String[] args) throws ClassNotFoundException, CloneNotSupportedException {
		MetaServiceImpl impl = new MetaServiceImpl();
		
		String conf = "[ { 'staticOpen': { 'urls': ['http://news.163.com/domestic/', 'http://news.163.com/shehui/'], 'requestType': 'get' } }, { 'staticPagination': { 'currentPage': 1, 'maxPage': 10, 'batchPage': 4, 'splitPage': 4, 'sleepMillis': 1000, 'requestType': 'get', 'paginationMeta': {'strategy': rule, 'paginationRule': 'currentPage + 1', 'stdLength': 2, 'leftAlign': '0', 'paginationUrls': ['http://news.163.com/special/0001124J/guoneinews_(pn).html#headList', 'http://news.163.com/special/00011229/shehuinews_(pn).html#headList'] }, 'innerCommands': [ { 'staticFetchList': { 'byType': 'byCss', 'typeValue': 'div.area-left > div.list-item.clearfix', 'innerCommands': [ { 'staticFetchKVs': { 'fetchValueWithKeys': [ { 'field': 'title', 'byType': 'byCss', 'typeValue': 'h2 > a', 'byAttr': 'innerText' }, { 'field': 'url', 'byType': 'byCss', 'typeValue': 'h2 > a', 'byAttr': 'href' } ] } } ] } } ] } }, { 'dedup': { 'fields': ['url'], 'mappingFields': ['_id'], 'dedupMeta': { 'engine': 'mongodb', host: '127.0.0.1', port: 27017, database: 'dedup', collection: 'dedup_news' } } }, { 'staticHandleList': { 'sleepMillis': 1, innerCommands: [ { 'staticOpen': {'urls': ['$url']} }, { 'staticFetchKVs': { 'fetchValueWithKeys': [ { 'field': 'content', 'byType': 'byCss', 'typeValue': 'div#endText', 'byAttr': 'innerHTML' }, { 'field': 'timeSource', 'byType': 'byCss', 'typeValue': 'div.post_time_source', 'byAttr': 'innerText' } ] } } ] } }, { 'store': { 'storeMeta': { 'engine': 'mongodb', 'host': 'localhost', 'port': 27017, 'database': '163', 'collection': 'news' } } } ]";
		int parallel = 2;
		
		System.out.println(GSON.fromJson(conf, List.class));
		
		Metadata meta = new Metadata();
		meta.conf = conf;
		meta.confParallel = parallel;
		
		impl.splitParallelMeta(meta, 0, impl.staticMetas);
		
		for (Metadata m : impl.staticMetas) {
			System.out.println(GSON.toJson(m));
		}
		
		System.out.println((int) Double.parseDouble("6.02"));
		
	}
	
	
	// init dbcpSingleton
	public MetaServiceImpl() throws ClassNotFoundException {
		String driver = ConstResource.getString("meta.node.mysql.driver");
		String url = ConstResource.getString("meta.node.mysql.url");
		String user = ConstResource.getString("meta.node.mysql.user");
		String password = ConstResource.getString("meta.node.mysql.password");
		dbcpSingleton = new DbcpSingleton(driver, url, user, password);
	}
	
	public Metadata getIntervalMillisMetaByConfType(int confType)
			throws RemoteException, SQLException, CloneNotSupportedException {
		Metadata meta = null;
		metaLock.lock();
		try {
			LOGGER.info(confType > 0 ? "动态采集器开始读取周期任务." : "静态采集器开始读取周期任务.");
			
			if (confType == 0) {
				if (staticMetas.size() > 0) meta = staticMetas.remove(staticMetas.size() - 1);;
			} else {
				if (dynamicMetas.size() > 0) meta = dynamicMetas.remove(dynamicMetas.size() - 1);;
			}
			
			if (null != meta) return meta;
			
			//
			// 重新从数据库读取任务
			//
			long now = System.currentTimeMillis();
			try (Connection conn = dbcpSingleton.getConnection();
				PreparedStatement qps = conn.prepareStatement(GET_META_WITH_INTERVAL_MILLIS_SQL)) {
				
				qps.setInt(1, confType);
				qps.setLong(2, now);
				
				int id = 0;
				try (ResultSet rs = qps.executeQuery()) {
					if (rs.next()) {
						meta = new Metadata();
						id = rs.getInt("id");
						
						meta.id = id;
						meta.name = rs.getString("name");
						meta.intervalMillis = rs.getLong("interval_millis");
						meta.conf = rs.getString("conf");
						meta.confParallel = rs.getInt("conf_parallel");
						meta.cookie = rs.getString("cookie");
					}
				}
				if (id > 0) {
					long next_time_millis = now + meta.intervalMillis;
					try (PreparedStatement ups = conn.prepareStatement("UPDATE ripper_config SET last_time_millis = ?, next_time_millis = ?, last_time = NOW() where id = ?")) {
						ups.setLong(1, now);
						ups.setLong(2, next_time_millis);
						ups.setInt(3, id);
						ups.executeUpdate();
					}
				}
			}
			
			//
			// 大任务切分
			//
			if (null != meta && meta.confParallel > 0) {
				if (confType == 0) {
					splitParallelMeta(meta, confType, staticMetas);
					meta = staticMetas.remove(staticMetas.size() - 1);
				}
				else {
					splitParallelMeta(meta, confType, dynamicMetas); 
					meta = dynamicMetas.remove(dynamicMetas.size() - 1);
				}
			}
		} finally {
			
			if (null == meta) LOGGER.info(confType > 0 ? "动态采集器完成读取周期任务, 当前没有需要运行的任务." : "静态采集器完成读取周期任务, 当前没有需要运行的任务.");
			else LOGGER.info(confType > 0 ? "动态采集器动态采集器完成读取周期任务, 读取到的任务名称... " + meta.name : "静态采集器完成读取周期任务, 读取到的任务名称... " + meta.name);
			
			metaLock.unlock();
		}
		
		return meta;
	}
	
	@SuppressWarnings("unchecked")
	private void splitParallelMeta(Metadata meta, int confType, List<Metadata> holders) throws CloneNotSupportedException {
		// 
		List<Map<String, Object>> commands = GSON.fromJson(meta.conf, new TypeToken<List<Map<String, Object>>>() {}.getType());
		// open 参数对象
		Map<String, Object> openParams;
		if (confType == 0) openParams = (Map<String, Object>) commands.get(0).get("staticOpen");
		else openParams = (Map<String, Object>) commands.get(0).get("browserOpen");
		// pagination 参数对象
		boolean hasPagination = false;
		Map<String, Object> paginationParams = null;
		for (Map<String, Object> command : commands) {
			if (command.containsKey("staticPagination") || command.containsKey("browserPagination")) {
				hasPagination = true;
				if (confType == 0) paginationParams = (Map<String, Object>) command.get("staticPagination");
				else paginationParams = (Map<String, Object>) command.get("browserPagination");
				break;
			}
		}
		
		//
		// 1. 第一级别任务并行  按照urls切分 : 即把urls切分成一个一个的url
		//
		List<Metadata> firstParallels = new ArrayList<Metadata>();
		List<String> urls = (List<String>) openParams.remove("urls");
		boolean paginationUseRule = false;// 是否使用规则翻页
		Map<String, Object> rule = null;
		List<String> paginationUrls = null;
		if (hasPagination) {
			Map<String, Object> paginationMeta = (Map<String, Object>) paginationParams.get("paginationMeta");
			String strategy = (String) paginationMeta.get("strategy");
			if ("rule".equalsIgnoreCase(strategy)) {
				paginationUseRule = true;
				rule = paginationMeta;
				paginationUrls = (List<String>) rule.remove("paginationUrls");
			}
		}
		for (int i = 0, len = urls.size(); i < len; i++) {
			Metadata fmeta = meta.clone();
			openParams.put("url", urls.get(i));
			if (paginationUseRule) rule.put("paginationUrl", paginationUrls.get(i));;
			fmeta.conf = GSON.toJson(commands);
			firstParallels.add(fmeta);
		}
		//
		holders.addAll(firstParallels);
		
		//
		// 2.第二级别的并行 ( note: 只有静态采集任务支持,并且要使用规则进行翻页 ) 按采集的最大页数进行拆分
		//
		if (confType == 0 && meta.confParallel > 1 && paginationUseRule) {
			int maxPage = (int) Double.parseDouble((null == paginationParams.get("maxPage") ? "5" : paginationParams.get("maxPage")).toString());
			int splitPage = (int) Double.parseDouble((null == paginationParams.get("splitPage") ? "20" : paginationParams.get("splitPage")).toString());
			
			if (maxPage > splitPage) {
				// clear
				holders.clear();
				
				int nSplits = maxPage / splitPage;// 分片总数量
				int lastSplitIdx = nSplits - 1;
				for (int i = 0, len = firstParallels.size(); i < len; i++) {
					for (int j = 0; j < nSplits; j++) {
						Metadata sMeta = firstParallels.get(i).clone();
						openParams.put("url", urls.get(i));
						paginationParams.put("currentPage", j * splitPage + 1);
						if (lastSplitIdx == j) paginationParams.put("maxPage", maxPage - lastSplitIdx * splitPage);
						else paginationParams.put("maxPage", splitPage);
						rule.put("paginationUrl", paginationUrls.get(i));
						
						sMeta.conf = GSON.toJson(commands);
						holders.add(sMeta);
					}
				}
			}
		}
	}
	
	public List<Metadata> getIntervalExpMetaByConfType(int confType, int limit) throws RemoteException, SQLException {
		List<Metadata> metaList = new ArrayList<Metadata>();
		metasLock.lock();
		try {
			try (Connection conn = dbcpSingleton.getConnection();
				PreparedStatement qps = conn.prepareStatement(GET_META_WITH_INTERVAL_EXP_SQL)) {
				
				qps.setInt(1, confType);
				if (confType == 0) qps.setInt(2, scheduleStaticFromIndex);
				else qps.setInt(2, scheduleDynamicFromIndex);
				qps.setInt(3, limit);
				
				int id = 0;
				try (ResultSet rs = qps.executeQuery()) {
					while (rs.next()) {
						Metadata meta = new Metadata();
						id = rs.getInt("id");
						
						meta.id = id;
						meta.name = rs.getString("name");
						meta.intervalExp = rs.getString("interval_exp");
						meta.conf = rs.getString("conf");
						meta.confPriority = rs.getInt("conf_priority");
						meta.confParallel = rs.getInt("conf_parallel");
						meta.cookie = rs.getString("cookie");
						
						metaList.add(meta);
						
						if (confType == 0) scheduleStaticFromIndex++;
						else scheduleDynamicFromIndex++;
					}
				}
			}
		} finally {
			metasLock.unlock();
		}
		return metaList;
	}
	
	public void delayMeta(int id, long delay) throws RemoteException, SQLException, ClassNotFoundException {
		try (Connection conn = dbcpSingleton.getConnection();
			PreparedStatement ps = conn.prepareStatement("UPDATE ripper_config SET next_time_millis = next_time_millis + ? where id = ?")) {
			ps.setLong(1, delay);
			ps.setInt(2, id);
			ps.executeUpdate();
		}
	}
	
	public void clear() {
		if (null == dbcpSingleton) {
			dbcpSingleton.close();
			dbcpSingleton = null;
		}
 	}
	
	//
	private DbcpSingleton dbcpSingleton;
	
	//
	private List<Metadata> staticMetas = new ArrayList<Metadata>();
	private List<Metadata> dynamicMetas = new ArrayList<Metadata>();
	
	private int scheduleStaticFromIndex = 0;
	private int scheduleDynamicFromIndex = 0;
	
	// lock
	private Lock metaLock = new ReentrantLock();
	private Lock metasLock = new ReentrantLock();
	
	//
}
