package com.vmesteonline.be.data;

import java.sql.*;

public abstract class JDBCConnector {

	public boolean execute(String query) throws Exception {
		try {
			connect();
			return stmnt.execute(query);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close();
		}
	}

	public ResultSet executeQuery(String query) throws Exception {
		try {
			connect();
			return stmnt.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			close();
		}
	}

	public void close() {
		if (null != stmnt)
			try {
				stmnt.close();
				stmnt = null;
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		if (null != conn)
			try {
				conn.close();
				conn = null;
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}

	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	protected abstract void connect() throws Exception;

	protected void connect(String drvName, String dbURL, String usr, String pswd) throws Exception {
		try {
			Class.forName(drvName);
			conn = DriverManager.getConnection(dbURL, usr, pswd);
			stmnt = conn.createStatement();
		} catch (SQLException se) {
			se.printStackTrace();
			throw new Exception(se);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception(e);
		}
	}

	private Connection conn = null;
	private Statement stmnt = null;

}