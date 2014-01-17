package com.vmesteonline.be;

import javax.jdo.PersistenceManager;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;

public class DataStoreTestsHelper {

	protected final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	DataStoreTestsHelper() {
		groupA = new VoGroup(groupAVisibleName, groupAName, groupADescription, groupALong, groupALat, groupARadius);
		groupB = new VoGroup(groupBVisibleName, groupBName, groupBDescription, groupBLong, groupBLat, groupBRadius);
		helper.setUp();
	}

	protected void initTestsVoGroups() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		pm.makePersistent(groupA);
		pm.makePersistent(groupB);
	}

	protected void saveVoGroup(VoGroup g) {
		PMF.get().getPersistenceManager().makePersistent(g);
	}

	static protected String groupAVisibleName = "groupA";
	static protected String groupAName = "group alfa";
	static protected String groupADescription = "group alfa desc";
	static protected float groupALong = 10.10F;
	static protected float groupALat = 15.15F;
	static protected int groupARadius = 0;

	static protected String groupBVisibleName = "groupB";
	static protected String groupBName = "group beta";
	static protected String groupBDescription = "group beta desc";
	static protected float groupBLong = 20.20F;
	static protected float groupBLat = 25.25F;
	static protected int groupBRadius = 0;

	VoGroup groupA;
	VoGroup groupB;

}
