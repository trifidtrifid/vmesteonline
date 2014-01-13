package com.vmesteonline.be;

import com.google.appengine.api.datastore.DatastoreService;
import com.vmesteonline.be.data.JDBCConnector;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AuthServiceImpl extends ServiceImpl implements AuthService.Iface {

	public AuthServiceImpl(JDBCConnector con) {
		super(con);
	}

	public AuthServiceImpl() {
	}

	@Override
	public Session login(final String uname, final String password)
			throws InvalidOperation, TException {
		Session sess = new Session();
		DatastoreService dataSrvc = DatastoreServiceFactory
				.getDatastoreService();

		System.out.print("try auten user " + uname + " pass " + password + "\n");

		try {
			Entity user = dataSrvc.get(KeyFactory.createKey("User", uname));

			System.out.print("try to compare pwd '" + password + "' pwd on store '" + user.getProperty("password")  +"'\n");

			if (user.getProperty("password").equals(password)) {
				user.setProperty("active", true);
				dataSrvc.put(user);
				sess.salt = generateSalt();
				sess.accessGranted = true;
				
			} else {
				sess.accessGranted = false;
				sess.error = "incorrect user or password";
			}

		} catch (EntityNotFoundException e) {
			sess.error = "incorrect user or password";
			System.out.print("can't find " + uname + " pass " + password + "\n");

		}

		return sess;
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
		DatastoreService dataSrvc = DatastoreServiceFactory
				.getDatastoreService();
		Entity user = new Entity("User", email);
		user.setUnindexedProperty("password", password);
		user.setUnindexedProperty("nick", uname);
		user.setUnindexedProperty("home", groupId);
		user.setProperty("active", false);
		dataSrvc.put(user);
		System.out.print("user " + email + " pass " + password + "\n");
		
		List<Rubric> rubric = getDeafultRubrics();
		
		
		return 0;
	}

	private static String generateSalt() {
		String str = ("" + Math.random()
				* (Calendar.getInstance().getTimeInMillis()) * 1000000000.0);
		return (str + "fgthstnthrewqntf").substring(0, 16);
	}

	
	private List<Rubric> getDeafultRubrics(){
		List<Rubric> rubrics = new ArrayList<Rubric>();
		return rubrics;
	}
	
	private Rubric createRubric(String name){
		Rubric r = new Rubric();
		r.id 
	}

}
