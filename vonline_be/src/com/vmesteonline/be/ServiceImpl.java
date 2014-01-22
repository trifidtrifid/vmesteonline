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

	protected long getUserId() throws InvalidOperation {
		if(null==httpSession) 
			throw new InvalidOperation(Error.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			VoSession sess = pm.getObjectById(VoSession.class, httpSession.getId());
			if (sess != null)
				return sess.getUserId();
			return (long) 0;
		} finally {
			pm.close();
		}
	}

	protected VoSession getCurrentSession() throws InvalidOperation {
		if(null==httpSession) 
			throw new InvalidOperation(Error.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return pm.getObjectById(VoSession.class, httpSession.getId());
		} finally {
			pm.close();
		}
	}
}
