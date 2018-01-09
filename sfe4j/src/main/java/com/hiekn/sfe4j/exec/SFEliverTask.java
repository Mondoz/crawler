package com.hiekn.sfe4j.exec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.hiekn.sfe4j.core.Sfe4j;
import com.hiekn.sfe4j.core.Sfe4jContext;
import com.hiekn.sfe4j.util.ConstResource;
import com.hiekn.sfe4j.util.StringUtils;

public class SFEliverTask implements Runnable {
	
	public SFEliverTask(Sfe4jContext context) {
		this.context = context;
		this.sfe4j = new Sfe4j(context);
	}
	
	/**
	 * 是否接受新任务
	 * 
	 * true: 接受新任务
	 * false: 不接受新任务
	 * 
	 * @param isAcceptTask  
	 */
	public void accepTask(boolean isAcceptTask) {
		this.isAcceptTask = isAcceptTask;
	}
	
	public void run() {
		
		while (isAcceptTask) {
			
			String taskJson = null;
			taskSynLock.lock();
			try {
				taskJson = takeTask();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				taskSynLock.unlock();
			}
			
			// no task
			if (StringUtils.isNullOrEmpty(taskJson)) {
				try {
					TimeUnit.MILLISECONDS.sleep(idleMillis);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new RuntimeException(e);// throw
				}
			}
			
			//
			try {
				sfe4j.execute(taskJson);
				TimeUnit.MILLISECONDS.sleep(sleepMillis);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}// end of while
	}
	
	
	private String takeTask() throws SQLException {
		String task = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		long now = System.currentTimeMillis();
		try {
			conn = context.getConnection();
			ps = conn.prepareStatement(QUERY_SFE4J_TASK);
			ps.setLong(1, now);
			rs = ps.executeQuery();
			int id = -1;
			long nextSchduleMillis = -1L;
			long schduleIntervalMillis = -1L;
			if (rs.next()) {
				id = rs.getInt(1);
				nextSchduleMillis = rs.getLong(3);
				schduleIntervalMillis = rs.getLong(4);
				task = rs.getString(5);
			}
			
			// update
			if (id > 0) {
				ps.close();
				ps = conn.prepareStatement(UPDATE_SFE4J_TASK);
				ps.setLong(1, nextSchduleMillis);
				ps.setLong(2, nextSchduleMillis + schduleIntervalMillis);
				ps.setInt(3, id);
			}
		} finally {
			if (null != rs) rs.close();
			if (null != ps) ps.close();
			if (null != conn) conn.close();
		}
		
		LOGGER.info(" task ... " + task);
		
		return task;
	}
	
	private int idleMillis = ConstResource.TASK_THREAD_IDLE_SLEEP_MILLIS;
	private int sleepMillis = ConstResource.TASK_THREAD_NORMAL_SLEEP_MILLIS;
	
	private final Sfe4jContext context;
	private final Sfe4j sfe4j;
	
	//
	private volatile boolean isAcceptTask = true;
	
	//
	private final Lock taskSynLock = new ReentrantLock();

	//
	private static final String QUERY_SFE4J_TASK = "SELECT id, task_name, next_schedule_millis, schedule_interval_millis, task_conf FROM sfe4j_config"
			+ " WHERE next_schedule_millis < ? ORDER BY next_schedule_millis ASC LIMIT 1";
	private static final String UPDATE_SFE4J_TASK = "UPDATE sfe4j_config SET last_schedule_millis = ?, next_schedule_millis = ? WHERE id = ?";
	
	//
	private static final Logger LOGGER = Logger.getLogger(SFEliverTask.class);
	
}
