package com.vmetsteonline.be.utils;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;

public class SessionHelper {

	public static Long getUserId(String sessionId) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		VoSession sess = pm.getObjectById(VoSession.class, sessionId);
		if (sess != null)
			return sess.getUserId();

		return (long) 0;
	}
}
