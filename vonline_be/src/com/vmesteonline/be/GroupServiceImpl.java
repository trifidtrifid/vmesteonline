package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmetsteonline.be.utils.Defaults;

public class GroupServiceImpl extends ServiceImpl implements GroupService.Iface {

	public GroupServiceImpl() {
		Defaults.setGroups();
	}

	public GroupServiceImpl(HttpSession session) {
		super(session);
	}

	@Override
	public List<com.vmesteonline.be.Group> getGroupsForRegistration() throws com.vmesteonline.be.InvalidOperation, org.apache.thrift.TException {
		List<com.vmesteonline.be.Group> groups = new ArrayList<com.vmesteonline.be.Group>();

		Query q = PMF.getPm().newQuery(VoGroup.class);
		q.setFilter("radius == radParam");
		q.declareParameters("int radParam");
		List<VoGroup> grps = (List<VoGroup>) q.execute(0);

		if (grps.isEmpty())
			return groups;

		for (VoGroup voGroup : grps) {
			Group g = new Group();
			g.shortName = voGroup.getVisibleName();
			g.id = voGroup.getId().getId();
			groups.add(g);
		}

		return groups;
	}

	@Override
	public List<com.vmesteonline.be.Group> getUserGroups() throws com.vmesteonline.be.InvalidOperation, org.apache.thrift.TException {

		List<com.vmesteonline.be.Group> groups = new ArrayList<com.vmesteonline.be.Group>();
		return groups;
	}

}
