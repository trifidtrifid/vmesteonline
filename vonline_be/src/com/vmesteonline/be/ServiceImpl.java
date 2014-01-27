package com.vmesteonline.be;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;

public class ServiceImpl {
	protected SessionIdStorage sessionStorage;

	public void setSession(HttpSession session) {
		this.sessionStorage = new SessionIdStorage(session.getId());
	}

	public ServiceImpl() {
	}

	protected ServiceImpl(String sessId) {
		sessionStorage = new SessionIdStorage(sessId);
	}

	protected ServiceImpl(HttpSession session) {
		this.sessionStorage = new SessionIdStorage(session.getId());
		;
	}

	protected long getUserId() throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(Error.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			VoSession sess = pm.getObjectById(VoSession.class, sessionStorage.getId());
			if (sess != null)
				return sess.getUserId();
			return (long) 0;
		} finally {
			pm.close();
		}
	}

	protected VoSession getCurrentSession() throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(Error.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return pm.getObjectById(VoSession.class, sessionStorage.getId());
		} finally {
			pm.close();
		}
	}

	static class SessionIdStorage {
		String sessId;

		SessionIdStorage(String sessId) {
			this.sessId = sessId;
		}

		public String getId() {
			return sessId;
		};
	}
}
