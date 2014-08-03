package com.vmesteonline.be;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;



import com.vmesteonline.be.access.VoUserAccessBase;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;

public class ServiceImpl {

	public enum ServiceCategoryID {
		BASE_SI, AUTH_SI, USER_SI, MESSAGE_SI, SHOP_SI
	}

	public Class getAuthRecordClass() {
		return VoUserAccessBase.class;
	}

	private static Cache cache;
	public static Logger logger;
	public static String hostName;

	static {
		logger = Logger.getLogger(ServiceImpl.class.getName());
		try {
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
			cache = cacheFactory.createCache(Collections.emptyMap());
		} catch (CacheException e) {
			logger.severe("Failed to initialize chache." + e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromCache(Object key) {
		T rslt = null;
		if (null != cache && cache.containsKey(key)) {
			try {
				rslt = (T) cache.get(key);
			} catch (ClassCastException cce) {
				logger.severe("CACHE:FAiled to get object by key " + key + ". " + cce);
			}
		}
		return rslt;
	}

	@SuppressWarnings("unchecked")
	public static <T> T removeObjectFromCache(Object key) {
		T rslt = null;
		if (null != cache && cache.containsKey(key)) {
			try {
				rslt = (T) cache.remove(key);
			} catch (ClassCastException cce) {
				logger.severe("CACHE:FAiled to remove object by key " + key + ". " + cce);
			}
		}
		return rslt;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> void putObjectToCache(Object key, T value) {
		if (null != cache) {
			try {
				cache.put(key, value);
			} catch (Exception e) {
				e.printStackTrace();
				logger.severe("CACHE:FAiled to PUT Object to cache." + e);
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

	public long getCurrentUserId() throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return getCurrentUserId(pm);
		} finally {
			pm.close();
		}

	}

	protected long getCurrentUserId(PersistenceManager _pm) throws InvalidOperation {
		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");

		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try {

			VoSession sess = getCurrentSession(_pm);
			if (sess != null && 0 != sess.getUserId()) {
				return sess.getUserId();
			}
		} finally {
			if (null == _pm)
				pm.close();
		}
		throw new InvalidOperation(VoError.NotAuthorized, "can't get current user id");
	}

	protected VoUser getCurrentUser() throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return getCurrentUser(pm);
		} finally {
			pm.close();
		}
	}

	public VoUser getCurrentUser(PersistenceManager pm) throws InvalidOperation {
		if (null == pm)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No PM set, but Persistance Object returned.");

		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");

		VoSession sess = getCurrentSession(pm);
		if (sess != null && 0 != sess.getUserId()) {
			return pm.getObjectById(VoUser.class, sess.getUserId());
		}
		throw new InvalidOperation(VoError.NotAuthorized, "can't get current user id");
	}

	protected VoSession getCurrentSession() throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return getCurrentSession(pm);
		} finally {
			pm.close();
		}
	}

	public Map<Integer, Long> getCurrentSessionAttributes() throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return getCurrentSession(pm).getSessionAttributes();
		} finally {
			pm.close();
		}
	}

	protected VoSession getCurrentSession(PersistenceManager pm) throws InvalidOperation {
		if (null == pm)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No PM set, but Persistance Object returned.");

		if (null == sessionStorage)
			throw new InvalidOperation(VoError.GeneralError, "Failed to process request. No session set.");

		try {
			return pm.getObjectById(VoSession.class, sessionStorage.getId());
		} catch (JDOObjectNotFoundException e) {
			VoSession vs = new VoSession(sessionStorage.getId(), null);
			pm.makePersistent(vs);
			return vs;
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
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			VoSession currentSession = getCurrentSession(pm);
			currentSession.setSessionAttribute(key, value);
			pm.makePersistent(currentSession);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoSession currentSession = getCurrentSession(pm);
			currentSession.setSessionAttributes(typeValueMap);
			pm.makePersistent(currentSession);
		} finally {
			pm.close();
		}
	}

	public Long getSessionAttribute(CurrentAttributeType type) throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			return getSessionAttribute(null);
		} finally {
			pm.close();
		}
	}

	public Long getSessionAttribute(CurrentAttributeType type, PersistenceManager _pm) throws InvalidOperation {
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			VoSession currentSession = getCurrentSession(pm);
			return currentSession.getSessionAttribute(type);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	/**
	 * Method return true if method should have public access through Thrift interface, false to check access by USer ID
	 * 
	 * @param method
	 * @return true if method is public
	 */
	public boolean isPublicMethod(String method) {
		return false;
	}

	/**
	 * Method returns an identification of category for access and must be overwritten in all of child classes
	 * 
	 * @return
	 */
	public long categoryId() {
		return ServiceCategoryID.BASE_SI.ordinal();
	}

	public boolean accessAllowed(VoUserAccessBase voUserAccessBase, long currentUserId, long categoryId, String method, PersistenceManager pm) {
		return true;
	}
}
