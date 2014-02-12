package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.jdo2.VoMessage;

public class MessagesTreeTests extends MessagesTree {

	List<VoMessage> lst = new ArrayList<VoMessage>();
	int msgCreateTime;
	VoMessage createVoMsg(long id, long parentId) {
		VoMessage msg = new VoMessage();
		msg.setId(KeyFactory.createKey(VoMessage.class.toString(), id));
		msg.setParentId(parentId);
		msgCreateTime += 10;
		msg.setCreatedAt(msgCreateTime);
		return msg;
	}

	// topic
	// -msg1
	// --msg4
	// --msg5
	// ---msg6
	// ----msg7
	// -msg2
	// --msg8
	// -msg3

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		msgCreateTime = 0;
		lst.add(createVoMsg(1, 0));
		lst.add(createVoMsg(2, 0));
		lst.add(createVoMsg(3, 0));
		lst.add(createVoMsg(4, 1));
		lst.add(createVoMsg(5, 1));
		lst.add(createVoMsg(6, 5));
		lst.add(createVoMsg(7, 6));
		lst.add(createVoMsg(8, 2));
	}

	@After
	public void tearDown() throws Exception {
		helper.tearDown();
	}

	@Test
	public void testMessageTreeCreateObject() {
		MessagesTree t = new MessagesTree(lst);
		Assert.assertEquals(8, t.items.size());
		Assert.assertEquals(1, t.items.get(0).id);
		Assert.assertEquals(4, t.items.get(1).id);
		Assert.assertEquals(5, t.items.get(2).id);
		Assert.assertEquals(6, t.items.get(3).id);

	}

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

}
