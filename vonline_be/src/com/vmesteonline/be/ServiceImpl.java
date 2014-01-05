package com.vmesteonline.be;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;

public class ServiceImpl {
	protected JDBCConnector con = new MySQLJDBCConnector();
}
