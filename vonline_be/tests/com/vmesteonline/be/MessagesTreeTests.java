package com.vmesteonline.be;

import java.util.ArrayList;

import java.util.List;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;

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
		Assert.assertEquals(0, t.items.get(0).level);

		Assert.assertEquals(4, t.items.get(1).id);
		Assert.assertEquals(1, t.items.get(1).level);

		Assert.assertEquals(5, t.items.get(2).id);
		Assert.assertEquals(1, t.items.get(2).level);

		Assert.assertEquals(6, t.items.get(3).id);
		Assert.assertEquals(2, t.items.get(3).level);

	}

	@Test
	public void testGetTreeMessagesAfter() {
		try {
			MessagesTree t = new MessagesTree(lst);
			
			List<VoMessage> msgs = t.getTreeMessagesAfter(1, 3);
			Assert.assertEquals(3, msgs.size());
			Assert.assertEquals(4, msgs.get(0).getId().getId());
			Assert.assertEquals(1, msgs.get(0).getVisibleOffset());

			Assert.assertEquals(5, msgs.get(1).getId().getId());
			Assert.assertEquals(1, msgs.get(1).getVisibleOffset());

			Assert.assertEquals(6, msgs.get(2).getId().getId());
			Assert.assertEquals(2, msgs.get(2).getVisibleOffset());

		} catch (Exception e) {
			e.printStackTrace();
			fail("catch exception: " + e.getMessage());
		}
	}

	@Test
	public void testGetTreeMessagesAfterMoreThenHave() {
		try {
			MessagesTree t = new MessagesTree(lst);
			
			List<VoMessage> msgs = t.getTreeMessagesAfter(5, 20);
			Assert.assertEquals(2, msgs.size());
			Assert.assertEquals(6, msgs.get(0).getId().getId());
			Assert.assertEquals(7, msgs.get(1).getId().getId());

		} catch (Exception e) {
			e.printStackTrace();
			fail("catch exception: " + e.getMessage());
		}
	}

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

}
