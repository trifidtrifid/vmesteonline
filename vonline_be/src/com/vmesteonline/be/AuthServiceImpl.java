package com.vmesteonline.be;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmetsteonline.be.utils.Defaults;
import com.vmetsteonline.be.utils.GroupHelper;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	public AuthServiceImpl() {

	}

	@Override
	public Session login(final String email, final String password) throws InvalidOperation, TException {
		if (httpSession == null) {
			logger.error("http session is null");
			throw new InvalidOperation(0, "incorrect params");
		}

		logger.info("try authentificate user " + email + " pass " + password);

		VoUser u = getUserByEmail(email);
		if (u != null) {
			if (u.getPassword().equals(password)) {
				VoSession sess = new VoSession(httpSession.getId(), u);
				PMF.getPm().makePersistent(sess);
				return sess.feSession();
			} else
				logger.info("incorrect password " + email + " pass " + password);

		}
		throw new InvalidOperation(1, "incorrect login or password");
	}

	@Override
	public Session getSession(final String salt) throws InvalidOperation, TException {
		Session sess = new Session();

		return sess;
	}

	@Override
	public int registerNewUser(String uname, String password, long groupId, String email) throws InvalidOperation {

		if (getUserByEmail(email) != null) {
			throw new InvalidOperation(1, "unknown user home group");
		}
		PersistenceManager pm = PMF.getPm();
		VoUser user = new VoUser(uname, "tt", email, password);
		VoGroup home = GroupHelper.getGroupById(groupId);
		if (home == null)
			throw new InvalidOperation(1, "unknown user home group");

		// find all defaults groups for user
		VoUserGroup gs = new VoUserGroup(home);
		user.getGroups().add(gs);
		for (VoUserGroup r : Defaults.defaultUserGroups) {
			gs = r.clone();
			gs.setLatitude(home.getLatitude());
			gs.setLongitude(home.getLongitude());
			user.getGroups().add(gs);
		}

		// find all defaults rubrics for user
		for (VoRubric r : Defaults.defaultRubrics) {
			VoRubric ur = r.clone();
			user.getRubrics().add(ur);
		}

		pm.makePersistent(user);
		logger.info("register " + email + " pass " + password + " id " + user.getId());

		return 0;
	}

	public VoUser getUserByEmail(String email) {
		Query q = PMF.getPm().newQuery(VoUser.class);
		List<VoUser> users = (List<VoUser>) q.execute(email);
		if (users.isEmpty())
			return null;
		if (users.size() != 1)
			logger.error("has more than one user with email " + email);
		return users.get(0);
	}

	private List<VoRubric> getDeafultRubrics() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		javax.jdo.Query q = pm.newQuery(VoRubric.class);
		List<VoRubric> rbcs = (List<VoRubric>) q.execute();
		if (rbcs.isEmpty())
			logger.error("can't load default rubrics");
		return rbcs;
	}

}