package com.vmesteonline.be;

import java.io.IOException;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class PersistenceInitFilter implements Filter {

	private static final PersistenceManagerFactory persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory("transactions-optional");

	private static PersistenceManagerFactory factory() {
		return persistenceManagerFactory;
	}

	private static ThreadLocal<PersistenceManager> currentManager = new ThreadLocal<PersistenceManager>();

	public static PersistenceManager getManager() {
		if (currentManager.get() == null || currentManager.get().isClosed()) {
			currentManager.set(factory().getPersistenceManager());
		}
		return currentManager.get();
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		PersistenceManager manager = null;
		try {
			manager = getManager();
			chain.doFilter(req, res);
		} finally {
			if (manager != null) {
				manager.flush();
				manager.close();
			}
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
