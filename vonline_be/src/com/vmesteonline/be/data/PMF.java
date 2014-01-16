package com.vmesteonline.be.data;

/**
 * Created by brozer on 1/12/14.
 */
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private PMF() {
	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

	public static PersistenceManager getPm() {
		return pmfInstance.getPersistenceManager();
	}
}
