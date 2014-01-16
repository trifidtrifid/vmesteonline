package com.vmesteonline.be;

import javax.servlet.ServletContext;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;

public abstract class ServiceImpl {
	
	protected ServletContext cntx;
	
    protected JDBCConnector con;

    protected ServiceImpl(JDBCConnector con) {
        this.con = con;
    }

    protected ServiceImpl() {
        con = new MySQLJDBCConnector();
    }

	public ServletContext getCntx() {
		return cntx;
	}

	public void setCntx(ServletContext cntx) {
		this.cntx = cntx;
	}
}
