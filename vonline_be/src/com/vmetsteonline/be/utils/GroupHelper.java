package com.vmetsteonline.be.utils;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;

public class GroupHelper {

	private static Logger logger = Logger.getLogger(GroupHelper.class);

	public static VoGroup getGroupById(Long id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		VoGroup group = pm.getObjectById(VoGroup.class, id);
		return group;
	}

	public static VoGroup getGroup(VoGroup basic, int radius) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		javax.jdo.Query q = pm.newQuery(VoGroup.class);
		q.setFilter("longitude == longParam && latitude == latParam && radius == radiusParam");
		q.declareParameters("float longParam, float latParam, int radiusParam");
		List<VoGroup> users = (List<VoGroup>) q.execute(basic.getLongitude(), basic.getLatitude(), radius);
		if (users.isEmpty()) {
			return null;
		}

		if (users.size() != 1) {
			logger.error("group with name " + basic.getName() + " with radius " + Integer.toString(radius) + " has " + Integer.toString(users.size())
					+ " groups");
		}
		return users.get(0);
	}
}
