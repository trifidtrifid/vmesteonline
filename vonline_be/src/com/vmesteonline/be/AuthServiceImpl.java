package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jdo.PersistenceManager;
import org.apache.thrift.TException;

import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	public AuthServiceImpl(JDBCConnector con) {
		super(con);
	}

	public AuthServiceImpl() {
	}

	@Override
	public Session login(final String email, final String password)
			throws InvalidOperation, TException {
		System.out.print("tttry auten user " + email + " pass " + password
				+ "\n");

		PersistenceManager pm = PMF.getPm();
		javax.jdo.Query q = pm.newQuery(VoUser.class);
		q.setFilter("email == emlParam");
		q.declareParameters("String emlParam");

		try {
			List<VoUser> users = (List<VoUser>) q.execute(email);
			if (!users.isEmpty()) {
				for (VoUser u : users) {
					System.out.print("try to compare pwd '" + password
							+ "' pwd on store '" + u.getPassword() + "'\n");

					if (u.getPassword().equals(password)) {
						VoSession sess = new VoSession(u);
						pm.makePersistent(sess);
						return sess.feSession();
					}
				}
			}

		} finally {
			System.out
					.print("can't find " + email + " pass " + password + "\n");

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