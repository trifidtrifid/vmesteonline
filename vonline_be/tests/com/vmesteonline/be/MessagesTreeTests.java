package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.jdo2.VoMessage;

public class MessagesTreeTests extends MessagesTree {

	List<VoMessage> lst = new ArrayList<VoMessage>();

	VoMessage createVoMsg(long id, long parentId) {
		VoMessage msg = new VoMessage();
		msg.setId(KeyFactory.createKey(VoMessage.class.toString(), id));
		msg.setParentId(parentId);
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
	}

	@Test
	public void testMessageTreeCreateObject() {
		MessagesTree t = new MessagesTree(lst); 
	}

}
