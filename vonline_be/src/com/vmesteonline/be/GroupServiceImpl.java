package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;

public class GroupServiceImpl extends ServiceImpl implements GroupService.Iface {

	public GroupServiceImpl() {
	}

	public GroupServiceImpl(HttpSession session) {
		super(session);
	}

	@Override
	public List<com.vmesteonline.be.Group> getGroupsForRegistration()
			throws com.vmesteonline.be.InvalidOperation,
			org.apache.thrift.TException {

		PersistenceManager pm = PMF.getPm();
		pm.newQuery(VoGroup.class);
		
		
		List<com.vmesteonline.be.Group> groups = new ArrayList<com.vmesteonline.be.Group>();
		Group g = new Group();
		g.shortName = "otdel 1";
		g.id = 1;
		groups.add(g);
		g = new Group();
		g.shortName = "otdel 2";
		g.id = 2;
		groups.add(g);
		g = new Group();
		g.shortName = "otdel 3";
		g.id = 3;
		groups.add(g);
		return groups;
	}

	@Override
	public List<com.vmesteonline.be.Group> getUserGroups()
			throws com.vmesteonline.be.InvalidOperation,
			org.apache.thrift.TException {
		
		
		List<com.vmesteonline.be.Group> groups = new ArrayList<com.vmesteonline.be.Group>();
		return groups;
	}

}
