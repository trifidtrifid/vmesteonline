package com.vmesteonline.be;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.utils.GroupHelper;

public class GroupHelperTests extends DataStoreTestsHelper {

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		super.initTestsVoGroups();
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testGetGroup() {
		super.saveVoGroup(new VoGroup("a200", "a 200 radius", "a 200 radius desc", groupALong, groupALat, 200));
		VoGroup a = GroupHelper.getGroup(groupA, 200);
		assertNotNull(a);
		assertEquals("a200", a.getVisibleName());
		assertEquals("a 200 radius", a.getName());
		assertEquals("a 200 radius desc", a.getDescription());
		assertEquals(groupALong, a.getLongitude(), 0F);
		assertEquals(groupALat, a.getLatitude(), 0F);
		assertEquals(200, a.getRadius());
	}
}
