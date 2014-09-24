package com.vmesteonline.be;

import static org.junit.Assert.fail;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.labs.repackaged.com.google.common.base.Pair;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoGeocoder;

public class VoGeocoderTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() throws Exception {
		helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testGetPosition() {
		PersistenceManager pm = PMF.getNewPm();
		Extent<VoBuilding> vbe = pm.getExtent(VoBuilding.class);
		UserServiceImpl usi = new UserServiceImpl("123");
		try {
			UserServiceImpl.getLocationCodesForRegistration();
		} catch (InvalidOperation e1) {
			e1.printStackTrace();
			fail("Failed! " + e1.getMessage());
			return;
		}
		for (VoBuilding voBuilding : vbe) {
			try {
				Pair<String, String> position = VoGeocoder.getPosition(voBuilding, false, pm);
				Assert.assertTrue(position != null);
				Assert.assertTrue(Float.valueOf(position.first) > -90);
				Assert.assertTrue(Float.valueOf(position.first) < 90);
				Assert.assertTrue(Float.valueOf(position.first) > -180);
				Assert.assertTrue(Float.valueOf(position.first) < 180);

			} catch (InvalidOperation e) {
				e.printStackTrace();
				fail("Failed! " + e.getMessage());
			}
		}
	}

}
