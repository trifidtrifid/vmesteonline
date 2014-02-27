package com.vmesteonline.be;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;

public class ServiceImpl {

	private static Cache cache;
	public static Logger logger;

	static {
		logger = Logger.getLogger(ServiceImpl.class);
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			logger.error("Failed to initialize chache." + e);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <T> T getObjectFromCache(Object key) {
		T rslt = null;
		if (null != cache && cache.containsKey(key)) {
			try {
				rslt = (T) cache.get(key);
			} catch (ClassCastException cce) {
				logger.error("CACHE:FAiled to get object by key " + key + ". " + cce);
			}
		}
		return rslt;
	}

	@SuppressWarnings("unchecked")
	protected static <T extends Serializable> void putObjectToCache(Object key, T value) {
		if (null != cache) {
			try {
				cache.put(key, value);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("CACHE:FAiled to PUT Object to cache." + e);
			}
		}
	}

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
	}

	protected long getCurrentUserId() throws InvalidOperation {
		return getCurrentUserId(null);
	}

	protected long getCurrentUserId(PersistenceManager _pm) throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = null == _pm ? PMF.get().getPersistenceManager() : _pm;
		try {
			try {
				VoSession sess = pm.getObjectById(VoSession.class, sessionStorage.getId());
				if (sess != null)
					return sess.getUserId();
			} catch (Exception e) {
				throw new InvalidOperation(VoError.NotAuthorized, "can't get current user id");
			}
		} finally {
			if (null == _pm)
				pm.close();
		}
		throw new InvalidOperation(VoError.NotAuthorized, "can't get current user id");
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
			if (sess != null && 0 != sess.getUserId()) {
				return pm.getObjectById(VoUser.class, sess.getUserId());
			}
			return null;
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	protected VoSession getCurrentSession() throws InvalidOperation {
		return getCurrentSession(null);
	}

	protected VoSession getCurrentSession(PersistenceManager _pm) throws InvalidOperation {

		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");
		PersistenceManager pm = null == _pm ? PMF.get().getPersistenceManager() : _pm;
		try {
			return pm.getObjectById(VoSession.class, sessionStorage.getId());
		} catch (JDOObjectNotFoundException e) {
			throw new InvalidOperation(VoError.NotAuthorized, "No session found");
		} finally {
			if (null == _pm)
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

	public void setCurrentAttribute(int key, long value) throws InvalidOperation {

		setCurrentAttribute(key, value, null);
	}

	public void setCurrentAttribute(int key, long value, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();

		VoSession currentSession = getCurrentSession(pm);
		currentSession.setSessionAttribute(key, value);

		try {
			pm.makePersistent(currentSession);
		} finally {
			pm.close();
		}
	}

	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		VoSession currentSession = getCurrentSession(pm);
		currentSession.setSessionAttributes(typeValueMap);
		try {
			pm.makePersistent(currentSession);
		} finally {
			pm.close();
		}
	}

	/*
	 * public Map<Integer, Long> getCurrentAttributes() throws InvalidOperation,
	 * TException { return getCurrentAttributes(null); } public Map<Integer, Long>
	 * getCurrentAttributes(PersistenceManager _pm) throws InvalidOperation,
	 * TException { PersistenceManager pm = null == _pm ?
	 * PMF.get().getPersistenceManager() : _pm; try { VoSession currentSession =
	 * getCurrentSession(pm); pm.retrieve(currentSession); return
	 * currentSession.getSessionAttributes(); } finally { if(null==_pm)
	 * pm.close(); } }
	 */

	public Long getSessionAttribute(CurrentAttributeType type) throws InvalidOperation {
		return getSessionAttribute(null);
	}

	public Long getSessionAttribute(CurrentAttributeType type, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = null == _pm ? PMF.get().getPersistenceManager() : _pm;
		try {
			VoSession currentSession = getCurrentSession(pm);
			return currentSession.getSessionAttribute(type);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}
}
