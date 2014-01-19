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
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.GroupHelper;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	public AuthServiceImpl() {

	}

	public static VoSession getSession(String sessId) throws InvalidOperation {
		VoSession sess = PMF.get().getPersistenceManager().getObjectById(VoSession.class, sessId);
		if (sess == null)
			throw new InvalidOperation(Error.NotAuthorized, "can't find active session for " + sessId);
		return sess;
	}

	@Override
	public boolean login(final String email, final String password) throws InvalidOperation, TException {
		if (httpSession == null) {
			logger.error("http session is null");
			throw new InvalidOperation(Error.IncorrectParametrs, "http session is null");
		}

		logger.info("try authentificate user " + email + " pass " + password);

		VoUser u = getUserByEmail(email);
		if (u != null) {
			if (u.getPassword().equals(password)) {
				VoSession sess = new VoSession(httpSession.getId(), u);
				PMF.getPm().makePersistent(sess);
				return true;
			} else
				logger.info("incorrect password " + email + " pass " + password);

		}
		throw new InvalidOperation(Error.IncorrectParametrs, "incorrect login or password");
	}

	@Override
	public boolean registerNewUser(String firstname, String lastname, String password, String email, String locationId) throws InvalidOperation {

		if (getUserByEmail(email) != null)
			throw new InvalidOperation(Error.IncorrectParametrs, "registration exsist");

		PersistenceManager pm = PMF.getPm();
		VoUser user = new VoUser(firstname, lastname, email, password);
		VoGroup home = GroupHelper.getGroupById(Long.decode(locationId));
		if (home == null)
			throw new InvalidOperation(Error.RegistrationAlreadyExist, "unknown user home group");

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

		return true;
	}

	@Override
	public void logout() throws InvalidOperation, TException {
//		VoSession sess = getSession();

	}

	public VoUser getUserByEmail(String email) {
		Query q = PMF.getPm().newQuery(VoUser.class);
		q.setFilter("email == emailParam");
		q.declareParameters("float emailParam");
		List<VoUser> users = (List<VoUser>) q.execute(email);
		if (users.isEmpty())
			return null;
		if (users.size() != 1)
			logger.error("has more than one user with email " + email);
		return users.get(0);
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

}