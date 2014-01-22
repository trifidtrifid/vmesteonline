package com.vmesteonline.be;

import static org.junit.Assert.fail;

import java.util.List;
import java.util.Vector;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.utils.Pair;

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
					MessageListPart messages = msi.getMessages(top.getId(), 0, MessageType.BASE, 0L, false, 0, 100000);
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
	public void testInsertMessageToListRepresentationOfTree(){

		List<Pair<Long, Long>> parentChildPairsList = new Vector<Pair<Long,Long>>();
		try {
			parentChildPairsList.add( new Pair<Long, Long>(0L, 1L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(1, 0, parentChildPairsList), new Pair<Long, Long>(1L, 2L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(1, 0, parentChildPairsList), new Pair<Long, Long>(1L, 3L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(1, 0, parentChildPairsList), new Pair<Long, Long>(1L, 4L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(1, 0, parentChildPairsList), new Pair<Long, Long>(1L, 14L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(4, 0, parentChildPairsList), new Pair<Long, Long>(4L, 5L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(4, 0, parentChildPairsList), new Pair<Long, Long>(4L, 6L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(6, 0, parentChildPairsList), new Pair<Long, Long>(6L, 8L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(6, 0, parentChildPairsList), new Pair<Long, Long>(6L, 9L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(6, 0, parentChildPairsList), new Pair<Long, Long>(6L, 10L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(10, 0, parentChildPairsList), new Pair<Long, Long>(10L, 11L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(10, 0, parentChildPairsList), new Pair<Long, Long>(10L, 12L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(10, 0, parentChildPairsList), new Pair<Long, Long>(10L, 13L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(14, 0, parentChildPairsList), new Pair<Long, Long>(14L, 15L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(4, 0, parentChildPairsList), new Pair<Long, Long>(4L, 7L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(14, 0, parentChildPairsList), new Pair<Long, Long>(14L, 16L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(15, 0, parentChildPairsList), new Pair<Long, Long>(15L, 17L));
			parentChildPairsList.add( VoTopic.getPosOfTheLastChildOf(15, 0, parentChildPairsList), new Pair<Long, Long>(15L, 18L));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		Assert.assertTrue( parentChildPairsList.get(0).right.equals(1L));
		Assert.assertTrue( parentChildPairsList.get(3).right.equals(4L));
		Assert.assertTrue( parentChildPairsList.get(7).right.equals(9L));
		Assert.assertTrue( parentChildPairsList.get(9).right.equals(11L));
		Assert.assertTrue( parentChildPairsList.get(11).right.equals(13L));
		Assert.assertTrue( parentChildPairsList.get(13).right.equals(14L));
		Assert.assertTrue( parentChildPairsList.get(15).right.equals(17L));
		Assert.assertTrue( parentChildPairsList.get(17).right.equals(16L));
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
