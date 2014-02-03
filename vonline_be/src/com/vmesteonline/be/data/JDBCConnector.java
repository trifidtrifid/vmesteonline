package com.vmesteonline.be.data;

import java.sql.*;

public abstract class JDBCConnector {
	
	public static class Exception extends java.lang.Exception {
		public Exception( java.lang.Exception e){super(e);}
	}
	
	private Connection conn = null;
	private Statement stmnt = null; 
	
	protected abstract void connect() throws Exception;
	
	protected void connect(String drvName, String dbURL, String usr, String pswd) throws Exception {
		try{
			Class.forName(drvName);
			conn = DriverManager.getConnection(dbURL,usr,pswd);
			stmnt = conn.createStatement();
		} catch (SQLException se){
			se.printStackTrace();
			throw new Exception( se );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception( e );
		}
	}
	
	public boolean execute( String query ) throws SQLException {
		return stmnt.execute(query);
	}
	
	public ResultSet executeQuery( String query ) throws SQLException {
		return stmnt.executeQuery(query);
	}
	
	public <T> T getResult( ResultCreator<T> rc, JDBCConnector con) throws java.lang.Exception {
		try {
			con.connect();
			return rc.createResult(conn);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(null!=con) con.close();
		}
	}
	
	public static interface ResultCreator<T> {
		public T createResult(Connection conn) throws java.lang.Exception ;
	} 
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public void close() {
		if( null!=stmnt)
			try {
				stmnt.close();
				stmnt = null;
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		if( null!=conn)
			try {
				conn.close();
				conn = null;
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
	}
	
	
	
}
