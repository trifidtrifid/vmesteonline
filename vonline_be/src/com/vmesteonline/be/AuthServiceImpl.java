package com.vmesteonline.be;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;

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

		PersistenceManager pm = PMF.get().getPersistenceManager();
		javax.jdo.Query q = pm.newQuery(VoUser.class);
		q.setFilter("email == emlParam");
		q.declareParameters("String emlParam");

		try {
			List<VoUser> users = (List<VoUser>) q.execute(email);
			if (!users.isEmpty()) {
				for (VoUser u : users) {
					if (u.getPassword().equals(password)) {
						VoSession sess = new VoSession(httpSession.getId(), u);
						pm.makePersistent(sess);
						return sess.feSession();
					}
				}
			}
		} finally {
			logger.info("can't find " + email + " pass " + password);
		}

		Session errSess = new Session();
		errSess.accessGranted = false;
		errSess.error = "can't find user or incorrect password";
		return errSess;
	}

	@Override
	public Session getSession(final String salt) throws InvalidOperation, TException {
		Session sess = new Session();

		return sess;
	}

	@Override
	public int registerNewUser(String uname, String password, String groupId, String email) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		VoUser user = new VoUser(uname, "tt", email, password);
		pm.makePersistent(user);
		logger.info("register " + email + " pass " + password + " id " + user.getId());

		return 0;
	}

	private List<VoRubric> getDeafultRubrics() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		javax.jdo.Query q = pm.newQuery(VoRubric.class);
		List<VoRubric> rbcs = (List<VoRubric>) q.execute();
		if (rbcs.isEmpty())
			logger.error("can't load default rubrics");
		return rbcs;
	}
	
	private List<VoGroup> getDeafultGroups(VoGroup home) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		javax.jdo.Query q = pm.newQuery(VoRubric.class);
		List<VoRubric> rbcs = (List<VoRubric>) q.execute();
		if (rbcs.isEmpty())
			logger.error("can't load default rubrics");
		return rbcs;
	}
}