package com.vmesteonline.be.data;

import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

public class MySQLJDBCConnector extends JDBCConnector {

	private static Logger logger;
	// JDBC driver name and database URL
	static String JDBC_DRIVER;
	static String DB_URL;

	// Database credentials
	static String USER;
	static String PASS;

	static {
		logger = Logger.getLogger(com.vmesteonline.be.data.MySQLJDBCConnector.class.getName());

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

			JDBC_DRIVER = "com.mysql.jdbc.GoogleDriver";
			DB_URL = "jdbc:google:mysql://algebraic-depot-657:mysql/forum";
			USER = "forum";
			PASS = "redhat7.3";
		} else {
			// Local MySQL instance to use during development.
			JDBC_DRIVER = "com.mysql.jdbc.Driver";
			DB_URL = "jdbc:mysql://localhost:3306/vonline";
			USER = "root";
			PASS = "";
		}

	}

	protected void connect() throws Exception {
		try {
			super.connect(JDBC_DRIVER, DB_URL, USER, PASS);
			logger.finer("Connected to database '"+USER+"@"+DB_URL+"'");
		} catch (Exception e) {
			logger.severe("Failed to connect to database '"+USER+"@"+DB_URL+"' password '"+PASS+"' driver '"+JDBC_DRIVER+"'");
			e.printStackTrace();
		}
	}
}
