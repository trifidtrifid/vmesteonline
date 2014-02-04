package com.vmesteonline.be;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;

public class AuthServiceImpTests {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	AuthServiceImpl authSrvc;

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		authSrvc = new AuthServiceImpl();
		HttpSessionStubForTests httpSess = new HttpSessionStubForTests();
		httpSess.setId("1");
		authSrvc.setSession(httpSess);
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testLogin() {
		try {
			authSrvc.login("test", "ppp");
		} catch (InvalidOperation e) {
			assertEquals(VoError.IncorrectParametrs, e.what);
		} catch (TException e) {
			e.printStackTrace();
			fail("unhadled exception");
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
			long ret = authSrvc.registerNewUser("testName", "testFamily", "testPassword", "test@eml", locations.get(0));
			VoUser user = authSrvc.getUserByEmail("test@eml");
			assertEquals("testName", user.getName());
			assertEquals("testPassword", user.getPassword());

			assertEquals(0, user.getHomeGroup().getGroup().getRadius());

			VoPostalAddress.getKeyValue(Long.parseLong(locations.get(0)));
			PersistenceManager pm = PMF.getPm();
			VoPostalAddress postalAddress = pm.getObjectById(VoPostalAddress.class, VoPostalAddress.getKeyValue(Long.parseLong(locations.get(0))));

			VoUser userByRet = pm.getObjectById(VoUser.class, ret);
			assertEquals(userByRet.getName(), user.getName());
			assertEquals(userByRet.getPassword(), user.getPassword());

			float longitude = postalAddress.getBuilding().getUserGroup().getLongitude();
			assertEquals(user.getHomeGroup().getLongitude(), longitude, 0F);
			float latitude = postalAddress.getBuilding().getUserGroup().getLatitude();
			assertEquals(user.getHomeGroup().getLatitude(), latitude, 0F);

			List<VoRubric> rubrics = user.getRubrics();
			assertEquals(rubrics.isEmpty(), false);

			Set<VoUserGroup> groups = user.getGroups();
			assertEquals(groups.isEmpty(), false);
			for (VoUserGroup ug : groups) {
				assertEquals(ug.getLatitude(), latitude, 0F);
				assertEquals(ug.getLongitude(), longitude, 0F);
				if (ug.isHome())
					assertEquals(ug.getGroup().getRadius(), 0);
			}
			boolean found = false;
			for (VoUser hobit : postalAddress.getBuilding().getUsers()) {
				if (hobit.getId().equals(userByRet.getId())) {
					found = true;
					break;
				}
			}
			assertEquals(found, true);
			assertEquals(true, authSrvc.login("test@eml", "testPassword"));

		} catch (TException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
