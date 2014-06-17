package com.vmesteonline.be.data;

import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;

public class MySQLJDBCConnector extends JDBCConnector {

	private static Logger logger;
	// JDBC driver name and database URL
	static String JDBC_DRIVER;
	static String DB_URL;

	// Database credentials
	static final String USER = "vonline";
	static final String PASS = "";

	static {
		logger = Logger.getLogger(com.vmesteonline.be.data.MySQLJDBCConnector.class.getName());

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

			JDBC_DRIVER = "com.mysql.jdbc.GoogleDriver";
			DB_URL = "jdbc:google:mysql://vmesteonline:sqldb/vonline";
		} else {
			// Local MySQL instance to use during development.
			JDBC_DRIVER = "com.mysql.jdbc.Driver";
			DB_URL = "jdbc:mysql://localhost:3306/vonline";
		}

	}

	protected void connect() throws Exception {
		super.connect(JDBC_DRIVER, DB_URL, USER, PASS);
	}
}
