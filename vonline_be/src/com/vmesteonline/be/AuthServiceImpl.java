package com.vmesteonline.be;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoInviteCode;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
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

			if (null == session || 0 == session.getUserId()) {
				throw new InvalidOperation(VoError.NotAuthorized, "can't find user session for " + httpSessId);
			}
			try {
				pm.getObjectById(VoUser.class, session.getUserId());
			} catch (Exception e) {
				session.setUserId(null);
				throw new InvalidOperation(VoError.NotAuthorized, "can't find user session for " + httpSessId);
			}

		} finally {
			pm.close();
		}
	}

	public boolean allowUserAccess(String email, String pwd, boolean checkPwd) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser u = getUserByEmail(email, pm);
			if (u != null) {
				if (u.getPassword().equals(pwd) || !checkPwd) {

					logger.info("save session '" + sessionStorage.getId() + "' userId " + u.getId());
					VoSession currentSession = getCurrentSession(pm);
					if (null == currentSession)
						currentSession = new VoSession(sessionStorage.getId(), u);
					else
						currentSession.setUser(u);
					pm.makePersistent(currentSession);
					return true;
				} else
					logger.info("incorrect password " + email + " pass " + pwd);

			}
		} finally {
			pm.close();
		}
		if (checkPwd)
			throw new InvalidOperation(VoError.IncorrectParametrs, "incorrect login or password");

		return false;
	}

	public static VoSession getSession(String sessId, PersistenceManager pm) throws InvalidOperation {

		try {
			VoSession sess = pm.getObjectById(VoSession.class, sessId);
			if (sess == null)
				throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);
			return sess;
		} catch (JDOObjectNotFoundException e) {
			throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);
		} catch (Exception e) {
			logger.fine("exception: " + e.toString());
		}
		throw new InvalidOperation(VoError.NotAuthorized, "can't find active session for " + sessId);

	}

	@Override
	public boolean login(final String email, final String password) throws InvalidOperation {
		if (sessionStorage == null) {
			logger.fine("http session is null");
			throw new InvalidOperation(VoError.IncorrectParametrs, "http session is null");
		}

		logger.info("try authentificate user " + email + " pass " + password);

		return allowUserAccess(email, password, true);
	}

	public void allowUserAccess(PersistenceManager pm, VoUser u) throws InvalidOperation {
		logger.info("save session '" + sessionStorage.getId() + "' userId " + u.getId());
		VoSession currentSession = getCurrentSession(pm);
		if (null == currentSession)
			currentSession = new VoSession(sessionStorage.getId(), u);
		else
			currentSession.setUser(u);
		/*
		 * sess.setLatitude(u.getLatitude()); sess.setLongitude(u.getLongitude());
		 */
		pm.makePersistent(currentSession);
	}

	@Override
	public UserLocation checkInviteCode(String code) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		try {
			VoInviteCode invite = VoInviteCode.getInviteCode(code, pm);
			VoPostalAddress pa = pm.getObjectById(VoPostalAddress.class, invite.getPostalAddressId());
			VoBuilding vBuilding = pm.getObjectById(VoBuilding.class, pa.getBuilding());
			if( vBuilding.getLatitude() == null || vBuilding.getLongitude() == null ){
				VoGeocoder.getPosition(vBuilding, false );
				pm.makePersistent(vBuilding);
			}
			return new UserLocation( pa.getAddressText(pm), Long.toString(invite.getPostalAddressId()), 
					VoGeocoder.createMapImageURL(  vBuilding.getLongitude(), vBuilding.getLatitude(), 450, 450 ));
		} finally {
			pm.close();
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public long registerNewUser(String firstname, String lastname, String password, String email, String inviteCode, int gender)
			throws InvalidOperation {

		if (getUserByEmail(email) != null)
			throw new InvalidOperation(VoError.RegistrationAlreadyExist, "registration exsist for user with email " + email);
		if (null == inviteCode || "".equals(inviteCode.trim()))
			throw new InvalidOperation(VoError.IncorrectParametrs, "unknown invite code " + inviteCode);

		PersistenceManager pm = PMF.getPm();

		try {

			VoInviteCode voInviteCode = VoInviteCode.getInviteCode(inviteCode, pm);
			voInviteCode.registered();

			VoUser user = new VoUser(firstname, lastname, email, password);
			user.setGender(gender);
			pm.makePersistent(user);
			pm.makePersistent(voInviteCode);

			try {
				user.setLocation(voInviteCode.getPostalAddressId(), pm);
			} catch (NumberFormatException | InvalidOperation e) {
				throw new InvalidOperation(VoError.IncorectLocationCode, "Incorrect code." + e);
			}

			List<VoUserGroup> groups = user.getGroups();
			logger.info("register " + email + " pass " + password + " id " + user.getId() + " location code: " + inviteCode + " home group: "
					+ (0 == groups.size() ? "Undefined!" : groups.get(0).getName()));

			try {
				String body = "<h2>" + firstname + " " + lastname + "</h2><br/>Вы зарегистрировались на сайте www.vmesteonline.ru. Ваш логин " + email
						+ ".<br/>";
				EMailHelper.sendSimpleEMail(email, "Вы зарегестрированы на Bo! сайте", body);

			} catch (Exception e) {
				logger.fine("can't send email to " + email + " " + e.getMessage());
				e.printStackTrace();
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
		q.setFilter("email == '" + email + "'");
		List<VoUser> users = (List<VoUser>) q.execute();
		if (users.isEmpty())
			return null;
		if (users.size() != 1)
			logger.severe("has more than one user with email " + email);
		return users.get(0);
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.AuthServiceImpl");

	@Override
	public void setCurrentAttribute(Map<Integer, Long> typeValueMap) throws InvalidOperation {
		super.setCurrentAttribute(typeValueMap);
	}

	@Override
	public Map<Integer, Long> getCurrentAttributes() throws InvalidOperation {
		return getCurrentSessionAttributes();
	}

	@Override
	public boolean checkEmailRegistered(String email) {
		PersistenceManager pm = PMF.getPm();
		try {
			return null != getUserByEmail(email, pm);
		} finally {
			pm.close();
		}
	}

	@Override
	public void sendConfirmCode(String to, String localfileName) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser vu = getUserByEmail(to, pm);
			if (null == vu)
				throw new InvalidOperation(VoError.IncorrectParametrs, "Nobody found by email '" + to + "'");

			long code = System.currentTimeMillis() % 123456L;
			vu.setConfirmCode(code);
			pm.makePersistent(vu);

			File localFIle = new File(localfileName);
			FileInputStream fis = new FileInputStream(localFIle);
			byte[] content = new byte[(int) localFIle.length()];
			fis.read(content);
			fis.close();

			EMailHelper.sendSimpleEMail(to, "Код для смены пароля на сайте Во!",
					new String(content, "UTF-8").replace("%code%", "" + code).replace("%name%", vu.getName() + " " + vu.getLastName()));
			logger.info("Code to change password is: " + code);

		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to send email to '" + to + "'. " + e);

		} finally {
			pm.close();
		}
	}

	@Override
	public void confirmRequest(String email, String confirmCode, String newPassword) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser vu = getUserByEmail(email, pm);
			if (null != vu && Long.parseLong(confirmCode) == vu.getConfirmCode()) {
				vu.setEmailConfirmed(true);
				if (null != newPassword && !"".equals(newPassword.trim()))
					vu.setPassword(newPassword);
				pm.makePersistent(vu);
			} else
				throw new InvalidOperation(VoError.IncorrectParametrs, "No such code registered for user!");

		} finally {
			pm.close();
		}
	}

	@Override
	public boolean checkIfEmailConfirmed(String email) throws TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser vu = getUserByEmail(email, pm);
			return null != vu && vu.isEmailConfirmed();

		} finally {
			pm.close();
		}
	}

	// TODO what is this?
	@Override
	public boolean isPublicMethod(String method) {
		return true;// publicMethods.contains(method);
	}

	// ======================================================================================================================

	@Override
	public long categoryId() {
		return ServiceCategoryID.AUTH_SI.ordinal();
	}

}