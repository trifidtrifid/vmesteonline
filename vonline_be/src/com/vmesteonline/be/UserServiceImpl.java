package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.GroupHelper;

public class UserServiceImpl extends ServiceImpl implements UserService.Iface {

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	public UserServiceImpl() {

	}

	public UserServiceImpl(HttpSession sess) {
		super(sess);
	}

	@Override
	public List<Group> getUserGroups() throws InvalidOperation, TException {
		VoSession sess = AuthServiceImpl.getSession(httpSession.getId());
		VoUser user = PMF.getPm().getObjectById(VoUser.class, sess.getUserId());
		if (user == null) {
			logger.error("can't find user by id " + Long.toString(sess.getUserId()));
			throw new InvalidOperation(Error.GeneralError, "can't find user bu id");
		}

		logger.info("find user name " + user.getEmail());

		if (user.getGroups() == null) {
			logger.warn("user with id " + Long.toString(sess.getUserId()) + " has no any groups");
			throw new InvalidOperation(Error.GeneralError, "can't find user bu id");
		}
		List<Group> groups = new ArrayList<Group>();
		for (VoUserGroup g : user.getGroups()) {
			Group gr = new Group();
			gr.id = g.getId().getId();
			gr.visibleName = g.getVisibleName();
			groups.add(gr);
		}
		return groups;
	}

	@Override
	public List<Rubric> getUserRubrics() throws InvalidOperation, TException {
		VoSession sess = AuthServiceImpl.getSession(httpSession.getId());
		VoUser user = PMF.getPm().getObjectById(VoUser.class, sess.getUserId());
		if (user == null) {
			logger.error("can't find user by id " + Long.toString(sess.getUserId()));
			throw new InvalidOperation(Error.GeneralError, "can't find user bu id");
		}

		logger.info("find user name " + user.getEmail());

		if (user.getGroups() == null) {
			logger.warn("user with id " + Long.toString(sess.getUserId()) + " has no any groups");
			throw new InvalidOperation(Error.GeneralError, "can't find user bu id");
		}

		List<Rubric> rubrics = new ArrayList<Rubric>();
		for (VoRubric r : user.getRubrics()) {
			Rubric ru = new Rubric();
			ru.id = r.getId().getId();
			ru.visibleName = r.getVisibleName();
			rubrics.add(ru);
		}
		return rubrics;
	}

	public static List<Group> getGroupsForRegistration() {
		List<com.vmesteonline.be.Group> groups = new ArrayList<com.vmesteonline.be.Group>();

		Query q = PMF.getPm().newQuery(VoGroup.class);
		q.setFilter("radius == radParam");
		q.declareParameters("int radParam");
		List<VoGroup> grps = (List<VoGroup>) q.execute(0);

		if (grps.isEmpty())
			return groups;

		for (VoGroup voGroup : grps) {
			Group g = new Group();
			g.visibleName = voGroup.getVisibleName();
			g.id = voGroup.getId().getId();
			groups.add(g);
		}

		return groups;
	}

}