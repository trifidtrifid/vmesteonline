package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	private static Logger logger = Logger
			.getLogger("com.vmesteonline.be.AuthServiceImpl");


	public AuthServiceImpl(JDBCConnector con) {
		super(con);
	}

	public AuthServiceImpl() {
	}

	@Override
	public Session login(final String email, final String password)
			throws InvalidOperation, TException {
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
	public Session getSession(final String salt) throws InvalidOperation,
			TException {
		Session sess = new Session();

		return sess;
	}

	@Override
	public int registerNewUser(String uname, String password, String groupId,
			String email) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		VoUser user = new VoUser(uname, "tt", email, password);
		pm.makePersistent(user);

		System.out.print("user " + email + " pass " + password + "\n");

		return 0;
	}

	private static String generateSalt() {
		String str = ("" + Math.random()
				* (Calendar.getInstance().getTimeInMillis()) * 1000000000.0);
		return (str + "fgthstnthrewqntf").substring(0, 16);
	}

	private List<Rubric> getDeafultRubrics() {
		List<Rubric> rubrics = new ArrayList<Rubric>();
		return rubrics;
	}

	private Rubric createRubric(String name) {
		Rubric r = new Rubric();
		r.name = name;
		return r;
	}

}