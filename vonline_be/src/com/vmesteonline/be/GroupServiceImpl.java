package com.vmesteonline.be;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.GroupService;

import java.awt.print.Printable;
import java.util.ArrayList;
import java.util.List;

import org.apache.jasper.tagplugins.jstl.core.Out;

public class GroupServiceImpl extends ServiceImpl implements GroupService.Iface {

	public GroupServiceImpl(JDBCConnector con) {
		super(con);
	}

	public GroupServiceImpl() {
	}

	@Override
	public List<com.vmesteonline.be.Group> getGroupsForRegistration()
			throws com.vmesteonline.be.InvalidOperation,
			org.apache.thrift.TException {
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
	public List<com.vmesteonline.be.Group> getUserGroups(int userId)
			throws com.vmesteonline.be.InvalidOperation,
			org.apache.thrift.TException {
		List<com.vmesteonline.be.Group> groups = new ArrayList<com.vmesteonline.be.Group>();
		return groups;
	}

}
