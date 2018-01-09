package com.hiekn.scraj.rest.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.hiekn.scraj.rest.bean.Metadata;
import com.hiekn.scraj.rest.factory.BeanFactory;

public class MetadataService {
	public static String save(String name, long intervalTime, String intervalExp, int intervalType, String cookie,
			String conf, int confType, String confGraph, int confPriority, int confParallel, int completed, int groupId)
			throws Exception {
		Metadata bean = new Metadata();
		int id = 0;
		int switchFlag = 0;
		String query = "select id,name,interval_millis,interval_exp,interval_type,cookie,conf,conf_graph,conf_type,conf_priority,completed,switch_flag,group_id from ripper_config where id = ?";
		String insert = "insert into ripper_config (name,interval_millis,interval_exp,interval_type,cookie,conf,conf_graph,conf_type,conf_priority,conf_parallel,completed,switch_flag,group_id)"
				+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		PreparedStatement istat = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
		istat.setString(1, name);
		istat.setLong(2, intervalTime);
		istat.setString(3, intervalExp);
		istat.setInt(4, intervalType);
		istat.setString(5, cookie);
		istat.setString(6, conf);
		istat.setString(7, confGraph);
		istat.setInt(8, confType);
		istat.setInt(9, confPriority);
		istat.setInt(10, confParallel);
		istat.setInt(11, completed);
		istat.setInt(12, switchFlag);
		istat.setInt(13, groupId);
		id = istat.executeUpdate();
		ResultSet rs = istat.getGeneratedKeys();
		if (rs.next())
			id = rs.getInt(1);
		System.out.println(id);
		System.out.println("finish connection save successfully");
		pstat.setInt(1, id);
		ResultSet rdata = pstat.executeQuery();
		while (rdata.next()) {
			id = rdata.getInt("id");
			name = rdata.getString("name");
			intervalTime = rdata.getLong("interval_millis");
			intervalExp = rdata.getString("interval_exp");
			intervalType = rdata.getInt("interval_type");
			cookie = rdata.getString("cookie");
			conf = rdata.getString("conf");
			confGraph = rdata.getString("conf_graph");
			confType = rdata.getInt("conf_type");
			confPriority = rdata.getInt("conf_priority");
			completed = rdata.getInt("completed");
			switchFlag = rdata.getInt("switch_flag");
			groupId = rdata.getInt("group_id");
			bean.setId(id);
			bean.setName(name);
			bean.setIntervalTime(intervalTime);
			bean.setIntervalExp(intervalExp);
			bean.setIntervalType(intervalType);
			bean.setCookie(cookie);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
			bean.setConfPriority(confPriority);
			bean.setConfParallel(confParallel);
			bean.setCompleted(completed);
			bean.setSwitchFlag(switchFlag);
			bean.setGroupId(groupId);
		}
		Gson gson = new Gson();
		String info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String getInfoList(int pageNo, int pageSize) throws Exception {
		String info = "";
		int count = 0;
		String query = "select id,name,interval_millis,interval_exp,interval_type,cookie,conf_type,conf_priority,conf_parallel,completed,switch_flag,group_id from ripper_config order by id desc LIMIT ?,?";
		String countQuery = "select count(*) from ripper_config";
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Metadata> infoList = new ArrayList<Metadata>();
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		PreparedStatement qstat = conn.prepareStatement(countQuery);
		ResultSet qrs = qstat.executeQuery(countQuery);
		while (qrs.next()) {
			count = qrs.getInt(1);
		}
		pstat.setInt(1, pageNo * pageSize - pageSize);
		pstat.setInt(2, pageSize);
		ResultSet rs = pstat.executeQuery();
		while (rs.next()) {
			Metadata bean = new Metadata();
			int id = rs.getInt("id");
			String name = rs.getString("name");
			long intervalTime = rs.getLong("interval_millis");
			String intervalExp = rs.getString("interval_exp");
			int intervalType = rs.getInt("interval_type");
			String cookie = rs.getString("cookie");
			int confType = rs.getInt("conf_type");
			int confPriority = rs.getInt("conf_priority");
			int confParallel = rs.getInt("conf_parallel");
			int completed = rs.getInt("completed");
			int switchFlag = rs.getInt("switch_flag");
			int groupId = rs.getInt("group_id");
			bean.setId(id);
			bean.setName(name);
			bean.setIntervalTime(intervalTime);
			bean.setIntervalExp(intervalExp);
			bean.setIntervalType(intervalType);
			bean.setCookie(cookie);
			bean.setConfType(confType);
			bean.setConfPriority(confPriority);
			bean.setConfParallel(confParallel);
			bean.setCompleted(completed);
			bean.setSwitchFlag(switchFlag);
			bean.setGroupId(groupId);
			infoList.add(bean);
		}
		map.put("size", count);
		map.put("data", infoList);
		Gson gson = new Gson();
		info = gson.toJson(map);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String getConf(int id) throws Exception {
		String info = "";
		String query = "select conf,conf_graph from ripper_config where id = ?";
		Metadata bean = new Metadata();
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		pstat.setInt(1, id);
		ResultSet rs = pstat.executeQuery();
		while (rs.next()) {
			bean.setConf(rs.getString("conf"));
			bean.setConfGraph(rs.getString("conf_graph"));
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String modifyInfo(int id, String name, long intervalTime, String intervalExp, int intervalType,
			String cookie, int confType, int confPriority, int confParallel, int completed, int groupId)
			throws Exception {
		Metadata bean = new Metadata();
		String info = "";
		String update = "Update ripper_config SET name = ?,interval_millis = ?, interval_exp = ?, interval_type = ?, cookie = ?,"
				+ "conf_type = ?, conf_priority = ?, conf_parallel = ?, completed = ?, group_id = ? where id = ?";
		String query = "select id,name,interval_millis,interval_exp,interval_type,cookie,conf,conf_graph,conf_type,conf_priority,conf_parallel,completed,switch_flag,group_id from ripper_config where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(update);
		PreparedStatement qstat = conn.prepareStatement(query);
		pstat.setString(1, name);
		pstat.setLong(2, intervalTime);
		pstat.setString(3, intervalExp);
		pstat.setInt(4, intervalType);
		pstat.setString(5, cookie);
		pstat.setInt(6, confType);
		pstat.setInt(7, confPriority);
		pstat.setInt(8, confParallel);
		pstat.setInt(9, completed);
		pstat.setInt(10, groupId);
		pstat.setInt(11, id);
		pstat.executeUpdate();
		qstat.setInt(1, id);
		ResultSet rs = qstat.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
			name = rs.getString("name");
			intervalTime = rs.getLong("interval_millis");
			intervalExp = rs.getString("interval_exp");
			intervalType = rs.getInt("interval_type");
			cookie = rs.getString("cookie");
			String conf = rs.getString("conf");
			String confGraph = rs.getString("conf_graph");
			confType = rs.getInt("conf_type");
			confPriority = rs.getInt("conf_priority");
			confParallel = rs.getInt("conf_parallel");
			completed = rs.getInt("completed");
			int switchFlag = rs.getInt("switch_flag");
			groupId = rs.getInt("group_id");
			bean.setId(id);
			bean.setName(name);
			bean.setIntervalTime(intervalTime);
			bean.setIntervalExp(intervalExp);
			bean.setIntervalType(intervalType);
			bean.setCookie(cookie);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
			bean.setConfPriority(confPriority);
			bean.setConfParallel(confParallel);
			bean.setCompleted(completed);
			bean.setSwitchFlag(switchFlag);
			bean.setGroupId(groupId);
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String modifyConf(int id, String conf, String confGraph, int completed) throws Exception {
		Metadata bean = new Metadata();
		String info = "";
		String update = "Update ripper_config SET conf = ?,conf_graph = ?,completed = ? where id = ?";
		String query = "select id,name,interval_millis,interval_exp,interval_type,cookie,conf,conf_graph,conf_type,conf_priority,conf_parallel, completed,switch_flag,group_id from ripper_config where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(update);
		PreparedStatement qstat = conn.prepareStatement(query);
		pstat.setString(1, conf);
		pstat.setString(2, confGraph);
		pstat.setInt(3, completed);
		pstat.setInt(4, id);
		pstat.executeUpdate();
		qstat.setInt(1, id);
		System.out.println("update success");
		ResultSet rs = qstat.executeQuery();
		System.out.println("query success");
		while (rs.next()) {
			id = rs.getInt("id");
			String name = rs.getString("name");
			long intervalTime = rs.getLong("interval_millis");
			String intervalExp = rs.getString("interval_exp");
			int intervalType = rs.getInt("interval_type");
			String cookie = rs.getString("cookie");
			conf = rs.getString("conf");
			confGraph = rs.getString("conf_graph");
			int confType = rs.getInt("conf_type");
			int confPriority = rs.getInt("conf_priority");
			int confParallel = rs.getInt("conf_parallel");
			completed = rs.getInt("completed");
			int switchFlag = rs.getInt("switch_flag");
			int groupId = rs.getInt("group_id");
			bean.setId(id);
			bean.setName(name);
			bean.setIntervalTime(intervalTime);
			bean.setIntervalExp(intervalExp);
			bean.setIntervalType(intervalType);
			bean.setCookie(cookie);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
			bean.setConfPriority(confPriority);
			bean.setConfParallel(confParallel);
			bean.setCompleted(completed);
			bean.setSwitchFlag(switchFlag);
			bean.setGroupId(groupId);
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String modifySwitchFlag(int id, int switchFlag) throws Exception {
		Metadata bean = new Metadata();
		String update = "Update ripper_config SET switch_flag = ? where id = ?";
		String query = "select id,name,interval_millis,interval_exp,interval_type,cookie,conf,conf_graph,conf_type,conf_priority,conf_parallel,completed,switch_flag,group_id from ripper_config where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(update);
		PreparedStatement qstat = conn.prepareStatement(query);
		qstat.setInt(1, id);
		pstat.setInt(1, switchFlag);
		pstat.setInt(2, id);
		ResultSet rs = qstat.executeQuery();
		System.out.println("query success");
		while (rs.next()) {
			id = rs.getInt("id");
			String name = rs.getString("name");
			long intervalTime = rs.getLong("interval_millis");
			String intervalExp = rs.getString("interval_exp");
			int intervalType = rs.getInt("interval_type");
			String cookie = rs.getString("cookie");
			String conf = rs.getString("conf");
			String confGraph = rs.getString("conf_graph");
			int confType = rs.getInt("conf_type");
			int confPriority = rs.getInt("conf_priority");
			int confParallel = rs.getInt("conf_parallel");
			int completed = rs.getInt("completed");
			int dbSwitchFlag = rs.getInt("switch_flag");
			int groupId = rs.getInt("group_id");
			bean.setId(id);
			bean.setName(name);
			bean.setIntervalTime(intervalTime);
			bean.setIntervalExp(intervalExp);
			bean.setIntervalType(intervalType);
			bean.setCookie(cookie);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
			bean.setConfPriority(confPriority);
			bean.setConfParallel(confParallel);
			bean.setCompleted(completed);
			bean.setSwitchFlag(dbSwitchFlag);
			bean.setGroupId(groupId);
		}
		if (bean.getCompleted() == 1) {
			pstat.executeUpdate();
			bean.setSwitchFlag(switchFlag);
		}
		Gson gson = new Gson();
		String info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String getConfigTemplate(int pageNo, int pageSize, int confType) throws Exception {
		String info = "";
		int count = 0;
		String query = "select id,name,conf,conf_graph,conf_type from ripper_config_template where conf_type = ? LIMIT ?,?";
		String countQuery = "select count(*) from ripper_config_template";
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Metadata> infoList = new ArrayList<Metadata>();
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		PreparedStatement qstat = conn.prepareStatement(countQuery);
		ResultSet qrs = qstat.executeQuery(countQuery);
		while (qrs.next()) {
			count = qrs.getInt(1);
		}
		pstat.setInt(1, confType);
		pstat.setInt(2, pageNo * pageSize - pageSize);
		pstat.setInt(3, pageSize);
		ResultSet rs = pstat.executeQuery();
		while (rs.next()) {
			Metadata bean = new Metadata();
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String conf = rs.getString("conf");
			String confGraph = rs.getString("conf_graph");
			confType = rs.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
			infoList.add(bean);
		}
		map.put("size", count);
		map.put("data", infoList);
		Gson gson = new Gson();
		info = gson.toJson(map);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String getFlowTemplate(int pageNo, int pageSize, int confType) throws Exception {
		String info = "";
		int count = 0;
		String query = "select id,name,conf,conf_graph,conf_type from ripper_flow_template where conf_type = ? LIMIT ?,?";
		String countQuery = "select count(*) from ripper_flow_template";
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<Metadata> infoList = new ArrayList<Metadata>();
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		PreparedStatement qstat = conn.prepareStatement(countQuery);
		ResultSet qrs = qstat.executeQuery(countQuery);
		while (qrs.next()) {
			count = qrs.getInt(1);
		}
		pstat.setInt(1, confType);
		pstat.setInt(2, pageNo * pageSize - pageSize);
		pstat.setInt(3, pageSize);
		ResultSet rs = pstat.executeQuery();
		while (rs.next()) {
			Metadata bean = new Metadata();
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String conf = rs.getString("conf");
			String confGraph = rs.getString("conf_graph");
			confType = rs.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
			infoList.add(bean);
		}
		map.put("size", count);
		map.put("data", infoList);
		Gson gson = new Gson();
		info = gson.toJson(map);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String getIdConfigTemplate(int id) throws Exception {
		Metadata bean = new Metadata();
		String info = "";
		String query = "select id,name,conf,conf_graph,conf_type from ripper_config_template where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		pstat.setInt(1, id);
		ResultSet rs = pstat.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
			String name = rs.getString("name");
			String conf = rs.getString("conf");
			String confGraph = rs.getString("conf_graph");
			int confType = rs.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String getIdFlowTemplate(int id) throws Exception {
		Metadata bean = new Metadata();
		String info = "";
		String query = "select id,name,conf,conf_graph,conf_type from ripper_flow_template where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		pstat.setInt(1, id);
		ResultSet rs = pstat.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
			String name = rs.getString("name");
			String conf = rs.getString("conf");
			String confGraph = rs.getString("conf_graph");
			int confType = rs.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String saveConfigTemplate(String name, String conf, String confGraph, int confType) throws Exception {
		Metadata bean = new Metadata();
		int id = 0;
		String query = "select id,name,conf,conf_graph,conf_type from ripper_config_template where id = ?";
		String insert = "insert into ripper_config_template (name,conf,conf_graph,conf_type) values (?,?,?,?)";
		Connection conn = getConnection();
		PreparedStatement istat = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
		istat.setString(1, name);
		istat.setString(2, conf);
		istat.setString(3, confGraph);
		istat.setInt(4, confType);
		istat.executeUpdate();
		ResultSet rs = istat.getGeneratedKeys();
		if (rs.next())
			id = rs.getInt(1);
		System.out.println(id);
		System.out.println("finish connection save successfully");
		PreparedStatement pstat = conn.prepareStatement(query);
		pstat.setInt(1, id);
		ResultSet rdata = pstat.executeQuery();
		while (rdata.next()) {
			id = rdata.getInt("id");
			name = rdata.getString("name");
			conf = rdata.getString("conf");
			confGraph = rdata.getString("conf_graph");
			confType = rdata.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
		}
		Gson gson = new Gson();
		String info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String saveFlowTemplate(String name, String conf, String confGraph, int confType) throws Exception {
		Metadata bean = new Metadata();
		int id = 0;
		String query = "select id,name,conf,conf_graph,conf_type from ripper_flow_template where id = ?";
		String insert = "insert into ripper_flow_template (name,conf,conf_graph,conf_type) values (?,?,?,?)";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(query);
		PreparedStatement istat = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
		istat.setString(1, name);
		istat.setString(2, conf);
		istat.setString(3, confGraph);
		istat.setInt(4, confType);
		id = istat.executeUpdate();
		ResultSet rs = istat.getGeneratedKeys();
		if (rs.next())
			id = rs.getInt(1);
		System.out.println(id);
		System.out.println("finish connection save successfully");
		pstat.setInt(1, id);
		ResultSet rdata = pstat.executeQuery();
		while (rdata.next()) {
			id = rdata.getInt("id");
			name = rdata.getString("name");
			conf = rdata.getString("conf");
			confGraph = rdata.getString("conf_graph");
			confType = rdata.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
		}
		Gson gson = new Gson();
		String info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String deleteConfigTemplate(int id) throws Exception {
		Metadata bean = new Metadata();
		String delete = "DELETE from ripper_config_template where id = ?";
		String query = "SELECT id, name from ripper_config_template where id = ?";
		Connection conn = getConnection();
		PreparedStatement qstat = conn.prepareStatement(query);
		qstat.setInt(1, id);
		ResultSet rs = qstat.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
			String name = rs.getString("name");
			bean.setId(id);
			bean.setName(name);
		}
		Gson gson = new Gson();
		String info = gson.toJson(bean);
		PreparedStatement pstat = conn.prepareStatement(delete);
		pstat.setInt(1, id);
		pstat.executeUpdate();
		pstat.close();
		conn.close();
		return info;
	}

	public static String deleteFlowTemplate(int id) throws Exception {
		Metadata bean = new Metadata();
		String delete = "DELETE from ripper_flow_template where id = ?";
		String query = "SELECT id, name from ripper_flow_template where id = ?";
		Connection conn = getConnection();
		PreparedStatement qstat = conn.prepareStatement(query);
		qstat.setInt(1, id);
		ResultSet rs = qstat.executeQuery();
		while (rs.next()) {
			id = rs.getInt("id");
			String name = rs.getString("name");
			bean.setId(id);
			bean.setName(name);
		}
		Gson gson = new Gson();
		String info = gson.toJson(bean);
		PreparedStatement pstat = conn.prepareStatement(delete);
		pstat.setInt(1, id);
		pstat.executeUpdate();
		pstat.close();
		conn.close();
		return info;
	}

	public static String modifyConfigTemplate(int id, String name, String conf, String confGraph, int confType)
			throws Exception {
		Metadata bean = new Metadata();
		String info = "";
		String update = "Update ripper_config_template SET conf = ?,conf_graph = ?,name = ?,conf_type = ? where id = ?";
		String query = "select id,name,conf,conf_graph,conf_type from ripper_config_template where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(update);
		PreparedStatement qstat = conn.prepareStatement(query);
		pstat.setString(1, conf);
		pstat.setString(2, confGraph);
		pstat.setString(3, name);
		pstat.setInt(4, confType);
		pstat.setInt(5, id);
		pstat.executeUpdate();
		qstat.setInt(1, id);
		System.out.println("update success");
		ResultSet rs = qstat.executeQuery();
		System.out.println("query success");
		while (rs.next()) {
			id = rs.getInt("id");
			name = rs.getString("name");
			conf = rs.getString("conf");
			confGraph = rs.getString("conf_graph");
			confType = rs.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String modifyFlowTemplate(int id, String name, String conf, String confGraph, int confType)
			throws Exception {
		Metadata bean = new Metadata();
		String info = "";
		String update = "Update ripper_flow_template SET conf = ?,conf_graph = ?,name = ?,conf_type = ? where id = ?";
		String query = "select id,name,conf,conf_graph,conf_type from ripper_flow_template where id = ?";
		Connection conn = getConnection();
		PreparedStatement pstat = conn.prepareStatement(update);
		PreparedStatement qstat = conn.prepareStatement(query);
		pstat.setString(1, conf);
		pstat.setString(2, confGraph);
		pstat.setString(3, name);
		pstat.setInt(4, confType);
		pstat.setInt(5, id);
		pstat.executeUpdate();
		qstat.setInt(1, id);
		System.out.println("update success");
		ResultSet rs = qstat.executeQuery();
		System.out.println("query success");
		while (rs.next()) {
			id = rs.getInt("id");
			name = rs.getString("name");
			conf = rs.getString("conf");
			confGraph = rs.getString("conf_graph");
			confType = rs.getInt("conf_type");
			bean.setId(id);
			bean.setName(name);
			bean.setConf(conf);
			bean.setConfGraph(confGraph);
			bean.setConfType(confType);
		}
		Gson gson = new Gson();
		info = gson.toJson(bean);
		rs.close();
		pstat.close();
		conn.close();
		return info;
	}

	public static String deleteMetadata(int id) {
		String query = "DELETE FROM ripper_config WHERE id = ?";
		try (Connection conn = getConnection(); PreparedStatement del = conn.prepareStatement(query)) {
			del.setInt(1, id);
			del.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> rs = new HashMap<>();
		rs.put("code", 200);
		rs.put("msg", "success.");
		rs.put("data", id);
		return new Gson().toJson(rs);
	}
	
	public static Connection getConnection() throws Exception {
		return BeanFactory.dbcpSingleton().getConnection();
	}

}
