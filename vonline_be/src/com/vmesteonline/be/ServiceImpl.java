package com.vmesteonline.be;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;

public class ServiceImpl {
    protected JDBCConnector con;

    protected ServiceImpl(JDBCConnector con) {
        this.con = con;
    }

    protected ServiceImpl() {
        con = new MySQLJDBCConnector();
    }
}
