package com.vmesteonline.be.data;


public class MySQLJDBCConnector extends JDBCConnector {
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/vonline";

	//  Database credentials
	static final String USER = "vonline";
	static final String PASS = "";

	protected void connect() throws Exception {
		super.connect(JDBC_DRIVER, DB_URL, USER, PASS);
	}
}
