package com.vmesteonline.be;

import java.io.IOException;
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
import com.vmesteonline.be.utils.Defaults;
import com.vmesteonline.be.utils.EMailHelper;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	public AuthServiceImpl() {
	}

	public AuthServiceImpl(String sessId) {
		super(sessId);
	}

	public static void checkIfAuthorised(String httpSessId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoSession session = getSession(httpSessId, pm);
			if( 0!=session.getUserId())
				throw new InvalidOperation(VoError.NotAuthorized, "can't find user session for " + httpSessId);
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
	public boolean login(final String email, final String password) throws InvalidOperation {
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
				sess.setLatitude(u.getLatitude());
				sess.setLongitude(u.getLongitude());
				pm.makePersistent(sess);
				return true;
			} else
				logger.info("incorrect password " + email + " pass " + password);

		}
		throw new InvalidOperation(VoError.IncorrectParametrs, "incorrect login or password");
	}

	@SuppressWarnings("unchecked")
	@Override
	public long registerNewUser(String firstname, String lastname, String password, String email, String locationId) throws InvalidOperation {

		if (getUserByEmail(email) != null)
			throw new InvalidOperation(VoError.RegistrationAlreadyExist, "registration exsist for user with email " + email);

		PersistenceManager pm = PMF.getPm();

		try {
			VoUser user = new VoUser(firstname, lastname, email, password);
			pm.makePersistent(user);

			// find all defaults rubrics for user
			Query q = pm.newQuery(VoRubric.class);
			q.setFilter(" subscribedByDefault == true");
			List<VoRubric> defRubrics = (List<VoRubric>) q.execute();
			if (defRubrics.isEmpty())
				defRubrics = Defaults.defaultRubrics;
			for (VoRubric rubric : defRubrics) {
				user.addRubric(rubric);
			}

			if( null==locationId || "".equals(locationId.trim()) ){
				logger.info("register " + email + " pass " + password + " id " + user.getId() + " Wihout location code and User Group");
			} else {
				try {
					user.setLocation(Long.parseLong(locationId), true, pm);
				} catch (NumberFormatException | InvalidOperation e) {
					throw new InvalidOperation(VoError.IncorectLocationCode, "Incorrect code." + e);
				}
	
				logger.info("register " + email + " pass " + password + " id " + user.getId() + " location code: " + locationId + " home group: "
						+ user.getGroups().get(0).getName());
			}
			return user.getId();

		} finally {
			pm.close();
		}

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
		PersistenceManager pm = PMF.getPm();
		try {
			return getUserByEmail(email, pm);
		} finally {
			pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public VoUser getUserByEmail(String email, PersistenceManager pm) {

		Query q = pm.newQuery(VoUser.class);
		q.setFilter("email == '"+email+"'");
		List<VoUser> users = (List<VoUser>) q.execute();
		if (users.isEmpty())
			return null;
		if (users.size() != 1)
			logger.error("has more than one user with email " + email);
		return users.get(0);
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	@Override
	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation {
		super.setCurrentAttribute(typeValueMap);
	}

	@Override
	public Map<Integer, Long> getCurrentAttributes() throws InvalidOperation {
		return super.getCurrentSession().getSessionAttributes();
	}

	@Override
	public boolean checkEmailRegistered(String email) {
		PersistenceManager pm = PMF.getPm();
		try {
			return null!=getUserByEmail(email,pm);
		} finally {
			pm.close();
		}
	}

	@Override
	public void sendChangePasswordCodeRequest(String to, String htmlTemplate) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser vu = getUserByEmail(to,pm);
			long code = System.currentTimeMillis() % 123456L;
			vu.setChangePasswordCode( code );
			pm.makePersistent(vu);
			
			EMailHelper.sendSimpleEMail("Во! <info@vmesteonline.ru>", to, "Код для смены пароля на сайте Во!", 
					htmlTemplate.replace("%code%", ""+code).replace("%name%", vu.getName() + " " +vu.getLastName()));
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to send email to '"+to+"'. " +to);
			
		} finally {
			pm.close();
		}
	}

	@Override
	public void changePasswordOfUser(String email, String confirmCode, String newPassword) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser vu = getUserByEmail(email,pm);
			if( null!=vu && Long.parseLong(confirmCode) == vu.getChangePasswordCode( )) {
				vu.setPassword(newPassword);
				pm.makePersistent(vu);	
			} else 
				throw new InvalidOperation(VoError.IncorrectParametrs, "No such code registered for user!");
			
		} finally {
			pm.close();
		}
	}
}