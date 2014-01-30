package com.vmesteonline.be;

import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.GroupHelper;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	public AuthServiceImpl() {
	}

	public AuthServiceImpl(String sessId) {
		super(sessId);
	}

	public static VoSession getSession(String sessId) throws InvalidOperation {
		VoSession sess = PMF.get().getPersistenceManager().getObjectById(VoSession.class, sessId);
		if (sess == null)
			throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);
		return sess;
	}

	@Override
	public boolean login(final String email, final String password) throws InvalidOperation, TException {
		if (sessionStorage == null) {
			logger.error("http session is null");
			throw new InvalidOperation(VoError.IncorrectParametrs, "http session is null");
		}

		logger.info("try authentificate user " + email + " pass " + password);

		VoUser u = getUserByEmail(email);
		if (u != null) {
			if (u.getPassword().equals(password)) {
				VoSession sess = new VoSession(sessionStorage.getId(), u);
				VoUserGroup homeGroup = u.getHomeGroup();
				if (homeGroup != null) {
					sess.setLatitude(homeGroup.getLatitude());
					sess.setLongitude(homeGroup.getLongitude());
				}
				PMF.getPm().makePersistent(sess);
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
		try {
			// find all defaults rubrics for user
			Query q = pm.newQuery(VoRubric.class);
			q.setFilter(" subscribedByDefault == true");
			List<VoRubric> defRubrics = (List<VoRubric>) q.execute();
			if( defRubrics.isEmpty() ) defRubrics = Defaults.defaultRubrics;
			for (VoRubric rubric : defRubrics) {
				user.addRubric(rubric);
			}
		} finally {
			pm.close();
		}

		try {
			user.setLocation(Long.parseLong(locationId), true);
		} catch (NumberFormatException | InvalidOperation e) {
			throw new InvalidOperation(VoError.IncorectLocationCode, "Incorrect code." + e);
		}

		logger.info("register " + email + " pass " + password + " id " + user.getId());
		return user.getId();

	}

	@Override
	public void logout() throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			pm.deletePersistent(getCurrentSession());
		} finally {
			pm.close();
		}
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

	@Override
	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation, TException {
		super.setCurrentAttribute(typeValueMap);
	}

	@Override
	public Map<Integer, Long> getCurrentAttributes() throws InvalidOperation, TException {
		return super.getCurrentAttributes();
	}

}