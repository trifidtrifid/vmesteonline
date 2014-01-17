package com.vmesteonline.be;

import javax.servlet.http.HttpSession;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;

public class ServiceImpl {
    protected JDBCConnector con;
    protected HttpSession httpSession;

    
    public void setHttpSession(HttpSession session) {
		this.httpSession = session;
	}

	protected ServiceImpl(JDBCConnector con) {
        this.con = con;
    }

    protected ServiceImpl() {
        con = new MySQLJDBCConnector();
    }
}
