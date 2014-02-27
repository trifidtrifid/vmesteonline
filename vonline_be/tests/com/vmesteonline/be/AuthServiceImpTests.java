package com.vmesteonline.be;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.utils.Defaults;

public class AuthServiceImpTests {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	AuthServiceImpl asi;
	UserServiceImpl usi;

	PersistenceManager pm;

	static private String httpSessionId = "111";

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		Defaults.init();
		pm = PMF.getPm();
		asi = new AuthServiceImpl(httpSessionId);
		usi = new UserServiceImpl(httpSessionId);

	}

	@After
	public void tearDown() throws Exception {
		pm.close();
		helper.tearDown();
	}

	@Test
	public void testDefaultsUserGetGroups() {
		try {
			asi.login(Defaults.user1email, Defaults.user1pass);
			List<Group> gs = usi.getUserGroups();
			Assert.assertEquals("Республиканская 32/3", gs.get(0).getName());

			asi.login(Defaults.user2email, Defaults.user2pass);
			gs = usi.getUserGroups();
			Assert.assertEquals("Республиканская 35", gs.get(0).getName());

		} catch (InvalidOperation e) {
			e.printStackTrace();
			fail("exception: " + e.getMessage());
		}
	}

	@Test
	public void testDefaultsUserCreation() {
		VoUser user = asi.getUserByEmail(Defaults.user1email, pm);
		Assert.assertEquals(0, user.getGroups().get(0).getRadius());
		Assert.assertEquals("Парадная 2", user.getGroups().get(0).getName());
		Assert.assertEquals("Парадная 2", user.getGroups().get(0).getLongitude());

		user = asi.getUserByEmail(Defaults.user2email, pm);
		Assert.assertEquals(0, user.getGroups().get(0).getRadius());
		Assert.assertEquals("Парадная 1", user.getGroups().get(0).getName());
		Assert.assertEquals("Парадная 1", user.getGroups().get(0).getLongitude());

		
		user = asi.getUserByEmail(Defaults.user3email, pm);
		Assert.assertEquals(0, user.getGroups().get(0).getRadius());
		Assert.assertEquals("Парадная 1", user.getGroups().get(0).getName());

	}

	@Test
	public void testLogin() {
		try {
			asi.login("test", "ppp");
		} catch (InvalidOperation e) {
			assertEquals(VoError.IncorrectParametrs, e.what);
		}

	}

	@Test
	public void testGetSessionNotAuthorized() {
		try {
			AuthServiceImpl.checkIfAuthorised("ttemptySession");
			fail("session should throw exception");
		} catch (InvalidOperation e) {
			assertEquals(VoError.NotAuthorized, e.what);
		}
	}

	@Test
	public void testRegisterNewUser() {
		List<String> locations;
		try {
			locations = UserServiceImpl.getLocationCodesForRegistration();
		} catch (InvalidOperation e1) {
			e1.printStackTrace();
			fail(e1.getMessage());
			return;
		}
		try {
			PersistenceManager pm = PMF.getPm();
			long ret = asi.registerNewUser("testName", "testFamily", "testPassword", "test@eml", locations.get(0));
			VoUser user = asi.getUserByEmail("test@eml", pm);
			assertEquals("testName", user.getName());
			assertEquals("testPassword", user.getPassword());
			/*
			 * Assert.assertNotNull(user.getHomeGroup()); assertEquals(0,
			 * user.getHomeGroup().getRadius());
			 */
			VoPostalAddress.getKeyValue(Long.parseLong(locations.get(0)));
			VoPostalAddress postalAddress = pm.getObjectById(VoPostalAddress.class, VoPostalAddress.getKeyValue(Long.parseLong(locations.get(0))));

			VoUser userByRet = pm.getObjectById(VoUser.class, ret);
			assertEquals(userByRet.getName(), user.getName());
			assertEquals(userByRet.getPassword(), user.getPassword());

			float longitude = postalAddress.getUserHomeGroup().getLongitude();
			assertEquals(user.getLongitude(), longitude, 0F);
			float latitude = postalAddress.getUserHomeGroup().getLatitude();
			assertEquals(user.getLatitude(), latitude, 0F);

			List<VoRubric> rubrics = user.getRubrics();
			assertEquals(rubrics.isEmpty(), false);

			List<VoUserGroup> groups = user.getGroups();
			assertEquals(groups.isEmpty(), false);
			for (VoUserGroup ug : groups) {
				assertEquals(ug.getLatitude(), latitude, 0F);
				assertEquals(ug.getLongitude(), longitude, 0F);
			}
			boolean found = false;
			for (VoUser hobit : postalAddress.getBuilding().getUsers()) {
				if (hobit.getId() == (userByRet.getId())) {
					found = true;
					break;
				}
			}
			assertEquals(found, true);
			assertEquals(true, asi.login("test@eml", "testPassword"));

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
