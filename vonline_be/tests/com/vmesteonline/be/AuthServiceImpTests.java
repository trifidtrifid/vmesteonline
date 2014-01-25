package com.vmesteonline.be;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vmesteonline.be.jdo2.VoUser;

public class AuthServiceImpTests extends DataStoreTestsHelper {

	AuthServiceImpl authSrvc;

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		initTestsVoGroups();
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
			assertEquals(Error.IncorrectParametrs, e.what);
		} catch (TException e) {
			e.printStackTrace();
			fail("unhadled exception");
		}

	}

	@Test
	public void testRegisterNewUser() {
		try {
			long ret = authSrvc.registerNewUser("testName", "testFamily", "testPassword", "test@eml", Long.toString(groupAId));
			VoUser user = authSrvc.getUserByEmail("test@eml");
			assertEquals("testName", user.getName());
			assertEquals("testPassword", user.getPassword());
			assertEquals(4, user.getGroups().size());

			assertEquals(0, user.getHomeGroup().getGroup().getRadius());
			assertEquals(groupALong, user.getGroups().get(0).getLongitude(), 0F);
			assertEquals(groupALat, user.getGroups().get(0).getLatitude(), 0F);

			assertEquals(200, user.getGroups().get(1).getGroup().getRadius());
			assertEquals(groupALong, user.getGroups().get(1).getLongitude(), 0F);
			assertEquals(groupALat, user.getGroups().get(1).getLatitude(), 0F);

			assertEquals(4, user.getRubrics().size());

		} catch (InvalidOperation e) {
			fail(e.why);
		}

	}
}
