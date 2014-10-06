package com.vmesteonline.be.data;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.PersistenceInitFilter;

public final class PMF {
	public static PersistenceManager getPm() {
		return PersistenceInitFilter.getManager();
	}
}
