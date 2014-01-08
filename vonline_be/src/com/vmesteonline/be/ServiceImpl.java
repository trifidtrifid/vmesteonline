package com.vmesteonline.be;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;

public class ServiceImpl {
    protected JDBCConnector con;

    ServiceImpl(JDBCConnector con) {
        this.con = con;
    }

    ServiceImpl() {
        con = new MySQLJDBCConnector();
    }
}
