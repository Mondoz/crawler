package com.hiekn.scraj.common.io.dao;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * 
 * 
 * 
 * @author govert
 *
 */
public class DbcpSingleton implements Closeable {
	
	private static final Logger LOGGER = Logger.getLogger(DbcpSingleton.class);
	
	private PoolingDataSource<PoolableConnection> dataSource;
	
	public DbcpSingleton(String driver, String url, String user, String password) throws ClassNotFoundException {
		
		LOGGER.info("init dbcp pool ... start");
		
		//
        // First we load the underlying JDBC driver.
        // You need this if you don't use the jdbc.drivers
        // system property.
        //
		Class.forName(driver);
		
		
		//
        // Then, we set up the PoolingDataSource.
        // Normally this would be handled auto-magically by
        // an external configuration, but in this example we'll
        // do it manually.
        //
        initDataSource(url, user, password);
        
        LOGGER.info("init dbcp pool ... done");
	}
	
	public final void initDataSource(String url, String user, String password) {
		
		//
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
            new DriverManagerConnectionFactory(url, user, password);
        
        //
        // Next we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
            new PoolableConnectionFactory(connectionFactory, null);
        
        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<PoolableConnection>(poolableConnectionFactory);
        
        // Set the factory's pool property to the owning pool
        poolableConnectionFactory.setPool(connectionPool);
        
        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        dataSource = new PoolingDataSource<PoolableConnection>(connectionPool);
		
	}
	
	public final Connection getConnection() {
		Connection conn = null;
		try {
			LOGGER.info("get a connection from dbcp pool...");
			conn = dataSource.getConnection();
			LOGGER.info("Done...");
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e);
		}
		return conn;
	}
	
	public final void realse(Connection conn, Statement stmt, ResultSet res) {
		if (res != null) {
			try {
				res.close(); // throw new RuntimeException
			} catch (Exception e) {
				e.printStackTrace();
			}
			res = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public final void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			LOGGER.error("SQLException " + e);
		}
	}
	
	/**
	 * 
	 * close 连接池
	 * 
	 * @throws Exception
	 */
	public void close() {
		try {
			LOGGER.info("close dbcp datasource init.");
			dataSource.close();
			LOGGER.info("close dbcp datasource done.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
