package com.vmesteonline.be;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class MessageServiceTests {
	
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
	public void testGetTopicsSTUB() {
		MessageServiceImpl msi = new MessageServiceImpl();
		try {
			int offset=0;
			do {
				TopicListPart topics = msi.getTopics( 0, 0, MessageType.BASE, 0, 0, 10);
				for( Topic top: topics.getTopics() ){
					System.out.println("TopicID:"+top.getId() + " topic:"+top.getSubject());
					MessageListPart messages = msi.getMessages(top.getId(), 0, MessageType.BASE, 0, 0, 100000);
					for( Message msg: messages.getMessages()) {
						System.out.println("msg ID:"+msg.getId() + " topic:"+msg.getTopicId()+" parent:"+msg.getParentId()+" :"+msg.getContent() );
					}
				}
				offset += topics.getTopicsSize();
				if(offset>=topics.getTotalSize()) break;
			} while( true );
			
		} catch ( TException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
		
	}

	@Test
	public void testGetTopics() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testGetMessagesSTUB() {
		

	}

	@Test
	public void testGetMessages() {
		fail("Not yet implemented");
	}

}
