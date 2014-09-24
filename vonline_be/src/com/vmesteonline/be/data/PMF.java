package com.vmesteonline.be.data;

/**
 * Created by brozer on 1/12/14.
 */
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

public final class PMF {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private PMF() {
	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	public static PersistenceManager getNewPm() {
		return pmfInstance.getPersistenceManager();
	}

	public static <T> Query getQuery(Class<T> type) {
		return pmfInstance.getPersistenceManager().newQuery(type);
	}
}
