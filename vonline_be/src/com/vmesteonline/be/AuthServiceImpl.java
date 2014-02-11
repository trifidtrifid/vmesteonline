package com.vmesteonline.be;

import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.Defaults;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	public AuthServiceImpl() {
	}

	public AuthServiceImpl(String sessId) {
		super(sessId);
	}

	public static void checkIfAuthorised(String httpSessId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			getSession(httpSessId, pm);
		} finally {
			pm.close();
		}
	}
	

	public static VoSession getSession(String sessId, PersistenceManager pm) throws InvalidOperation {

		try {
			VoSession sess = null;
			sess = PMF.get().getPersistenceManager().getObjectById(VoSession.class, sessId);
			if (sess == null)
				throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);
			return sess;
		} catch (JDOObjectNotFoundException e) {
			throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);
		} catch (Exception e) {
			logger.debug("exception: " + e.toString());
		}
		throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);

	}

	@Override
	public boolean login(final String email, final String password) throws InvalidOperation, TException {
		if (sessionStorage == null) {
			logger.error("http session is null");
			throw new InvalidOperation(VoError.IncorrectParametrs, "http session is null");
		}

		logger.info("try authentificate user " + email + " pass " + password);

		PersistenceManager pm = PMF.getPm();
		VoUser u = getUserByEmail(email, pm);
		if (u != null) {
			if (u.getPassword().equals(password)) {
				VoSession sess = new VoSession(sessionStorage.getId(), u);
				VoUserGroup homeGroup = u.getHomeGroup();
				if (homeGroup != null) {
					sess.setLatitude(homeGroup.getLatitude());
					sess.setLongitude(homeGroup.getLongitude());
				}
				pm.makePersistent(sess);
				return true;
			} else
				logger.info("incorrect password " + email + " pass " + password);

		}
		throw new InvalidOperation(VoError.IncorrectParametrs, "incorrect login or password");
	}

	@Override
	public long registerNewUser(String firstname, String lastname, String password, String email, String locationId) throws InvalidOperation {

		if (getUserByEmail(email) != null)
			throw new InvalidOperation(VoError.RegistrationAlreadyExist, "registration exsist");

		PersistenceManager pm = PMF.getPm();
		VoUser user = new VoUser(firstname, lastname, email, password);
		pm.makePersistent(user);
		try {
			// find all defaults rubrics for user
			Query q = pm.newQuery(VoRubric.class);
			q.setFilter(" subscribedByDefault == true");
			List<VoRubric> defRubrics = (List<VoRubric>) q.execute();
			if (defRubrics.isEmpty())
				defRubrics = Defaults.defaultRubrics;
			for (VoRubric rubric : defRubrics) {
				user.addRubric(rubric);
			}

			try {
				user.setLocation(Long.parseLong(locationId), true, pm);
			} catch (NumberFormatException | InvalidOperation e) {
				throw new InvalidOperation(VoError.IncorectLocationCode, "Incorrect code." + e);
			}

		} finally {
			pm.close();
		}

		logger.info("register " + email + " pass " + password + " id " + user.getId());
		return user.getId();

	}

	@Override
	public void logout() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			pm.deletePersistent(getCurrentSession(pm));
		} finally {
			pm.close();
		}
	}

	public VoUser getUserByEmail(String email) {
		return getUserByEmail(email, null);
	}

	public VoUser getUserByEmail(String email, PersistenceManager _pm) {

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			Query q = pm.newQuery(VoUser.class);
			q.setFilter("email == emailParam");
			q.declareParameters("float emailParam");
			List<VoUser> users = (List<VoUser>) q.execute(email);
			if (users.isEmpty())
				return null;
			if (users.size() != 1)
				logger.error("has more than one user with email " + email);
			return users.get(0);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	@Override
	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation, TException {
		super.setCurrentAttribute(typeValueMap);
	}

	@Override
	public Map<Integer, Long> getCurrentAttributes() throws InvalidOperation, TException {
		return super.getCurrentSession().getSessionAttributes();
	}

}