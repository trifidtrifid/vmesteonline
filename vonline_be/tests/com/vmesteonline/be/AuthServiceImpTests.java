package com.vmesteonline.be;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

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

public class AuthServiceImpTests {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	AuthServiceImpl asi;

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		asi = new AuthServiceImpl();
		HttpSessionStubForTests httpSess = new HttpSessionStubForTests();
		httpSess.setId("1");
		asi.setSession(httpSess);
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
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
			Assert.assertNotNull(user.getHomeGroup());
			assertEquals(0, user.getHomeGroup().getRadius());

			VoPostalAddress.getKeyValue(Long.parseLong(locations.get(0)));
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

			List<VoUserGroup> groups = user.getGroups();
			assertEquals(groups.isEmpty(), false);
			for (VoUserGroup ug : groups) {
				assertEquals(ug.getLatitude(), latitude, 0F);
				assertEquals(ug.getLongitude(), longitude, 0F);
			}
			boolean found = false;
			for (VoUser hobit : postalAddress.getBuilding().getUsers()) {
				if (hobit.getId().equals(userByRet.getId())) {
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
