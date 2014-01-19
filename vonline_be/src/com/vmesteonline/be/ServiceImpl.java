package com.vmesteonline.be;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;

public class ServiceImpl {
	protected HttpSession httpSession;

	public void setHttpSession(HttpSession session) {
		this.httpSession = session;
	}

	protected ServiceImpl() {
	}

	protected ServiceImpl(HttpSession session) {
		this.httpSession = session;
	}

	protected long getUserId(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		VoSession sess = pm.getObjectById(VoSession.class, httpSession.getId());
		if (sess != null)
			return sess.getUserId();
		return (long) 0;
	}
}
