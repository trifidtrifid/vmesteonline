package com.vmesteonline.be;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import static org.junit.Assert.*;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAuthServiceImpl {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testLogin() {
		AuthServiceImpl auth = new AuthServiceImpl();
		try {
			Session sess = auth.login("test", "ppp");
			assertEquals(false, sess.accessGranted);
		} catch (InvalidOperation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetSession() {
		fail("Not yet implemented");
	}

	@Test
	public void testRegisterNewUser() {
		fail("Not yet implemented");
	}

}
