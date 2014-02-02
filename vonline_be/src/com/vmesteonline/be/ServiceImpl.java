package com.vmesteonline.be;

import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.apache.thrift.TException;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;

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

	protected long getCurrentUserId() throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");
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
	
	protected VoUser getCurrentUser() throws InvalidOperation {
		return getCurrentUser(null);
	}
	
	protected VoUser getCurrentUser(PersistenceManager _pm) throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = null == _pm ? PMF.get().getPersistenceManager() : _pm;
		try {
			VoSession sess = pm.getObjectById(VoSession.class, sessionStorage.getId());
			if (sess != null && 0!=sess.getUserId()){
				return pm.getObjectById(VoUser.class, sess.getUserId());
			}
			return null;
		} finally {
			if( null==_pm) pm.close();
		}
	}

	protected VoSession getCurrentSession() throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			VoSession session = pm.getObjectById(VoSession.class, sessionStorage.getId());
			if( null == session)
				throw new InvalidOperation(VoError.NotAuthorized, "No session found.");
			return session;
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
	
	public void setCurrentAttribute( int key, long value) throws InvalidOperation, TException {
		VoSession currentSession = getCurrentSession();
		currentSession.setSessionAttribute(key, value);
		PersistenceManager pm = PMF.getPm();
		try {
			pm.makePersistent(currentSession);
		} finally {
			pm.close();
		}
	}
	
	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation, TException {
		VoSession currentSession = getCurrentSession();
		currentSession.setSessionAttributes(typeValueMap);
		PersistenceManager pm = PMF.getPm();
		try {
			pm.makePersistent(currentSession);
		} finally {
			pm.close();
		}
	}

	public Map<Integer, Long> getCurrentAttributes() throws InvalidOperation, TException {
		return getCurrentSession().getSessionAttributes();
	}
}
