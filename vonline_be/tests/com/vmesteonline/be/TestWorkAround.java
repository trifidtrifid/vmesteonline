package com.vmesteonline.be;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import org.junit.Assert;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.AuthServiceImpl;
import com.vmesteonline.be.Group;
import com.vmesteonline.be.MessageServiceImpl;
import com.vmesteonline.be.Rubric;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.authservice.LoginResult;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.utils.Defaults;

public class TestWorkAround {

	protected final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
			new LocalBlobstoreServiceTestConfig());
	public static String sessionId = "11111111111111111111111";

	protected AuthServiceImpl asi;
	protected UserServiceImpl usi;
	protected MessageServiceImpl msi;
	protected HashMap<MessageType, Long> noLinkedMessages = new HashMap<MessageType, Long>();
	protected TreeMap<Long, String> noTags = new TreeMap<Long, String>();
	protected PersistenceManager pm;

	protected Group homeGroup;
	protected Group group200m;
	protected Group group2000m;

	protected String topicSubject = "Test topic";

	protected boolean init() {
		try {
			helper.setUp();
			if (!Defaults.initDefaultData(false))
				return false;

			pm = PMF.get().getPersistenceManager();
			asi = new AuthServiceImpl(sessionId);
			if (LoginResult.SUCCESS != asi.login(Defaults.user1email, Defaults.user1pass))
				return false;
			usi = new UserServiceImpl(sessionId);
			msi = new MessageServiceImpl(sessionId);

			List<Group> userGroups = usi.getUserGroups();
			Assert.assertTrue(userGroups.size() > 0);
			Assert.assertTrue(userGroups.get(0) != null);
			homeGroup = userGroups.get(1);
			group200m = userGroups.get(2);
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	void close() {
		if (pm != null)
			pm.close();
		helper.tearDown();
	}

	protected long getUserGroupId(String email, int radius) {
		VoUser user = asi.getUserByEmail(email, pm);
		for (VoUserGroup ug : user.getGroups()) {
			if (ug.getRadius() == radius) {
				return ug.getId();
			}
		}
		return 0L;
	}

}
