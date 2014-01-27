package com.vmesteonline.be;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
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
		String sessionId = "11111111111111111111111";
		MessageServiceImpl msi = new MessageServiceImpl( sessionId );
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
	public void createTopicTest() {
		String sessionId = "11111111111111111111111";
		//create locations
		try{
			PersistenceManager pm = PMF.get().getPersistenceManager();
		
			VoGroup homeGroup = new VoGroup("Home GRP", "", "Descr of Home", 10.0f, 30.0f, 0);
			VoGroup blockGroup = new VoGroup("Block GRP", "", "Descr of Block", 10.0f, 30.0f, 100);
			pm.makePersistent(homeGroup);
			pm.makePersistent(blockGroup);
			
			AuthServiceImpl asi = new AuthServiceImpl(sessionId);
			asi.registerNewUser("Test1", "USer2", "123", "a1@b.com", ""+homeGroup.getId().getId());
			asi.registerNewUser("Test2", "USer2", "123", "a2@b.com", ""+homeGroup.getId().getId());
			
			Assert.assertTrue(asi.login("a1@b.com", "123"));
			
			VoUser user1 = asi.getUserByEmail("a1@b.com");
			VoUser user2 = asi.getUserByEmail("a2@b.com");
			
			UserServiceImpl usi = new UserServiceImpl(sessionId);
			List<Group> userGroups = usi.getUserGroups();
			List<Rubric> userRubrics = usi.getUserRubrics();
			Assert.assertTrue(userGroups.size()>0);
			Assert.assertTrue(userRubrics.size()>0);
			Assert.assertTrue(userRubrics.get(0)!=null);
			
			//start to test that we can create a topic in a group
			MessageServiceImpl msi = new MessageServiceImpl(sessionId);
			HashMap<MessageType, Long> noLinkedMessages = new HashMap<MessageType, Long>();
			TreeMap<Long, String> noTags = new TreeMap<Long, String>();
			
			Topic topic = msi.createTopic(blockGroup.getId().getId(),"Test topic", MessageType.BASE, "CCOntent of the first topic is a simple string", 
					noLinkedMessages, noTags, userRubrics.get(0).getId(), 0L);
			//create a message inslide the topic
			Message msg = msi.createMessage(topic.getMessage().getId(), homeGroup.getId().getId(), MessageType.BASE, "COntent of the first message in the topic", 
					noLinkedMessages, noTags, 0L);
			
			Assert.assertEquals(msg.getTopicId(), topic.getId());
			Assert.assertEquals(msg.getParentId(), topic.getMessage().getId());
			Assert.assertEquals(msg.getAuthorId(), topic.getMessage().getAuthorId());
			Assert.assertEquals(msg.getAuthorId(), user1.getId().longValue());
			Assert.assertEquals(msg.likesNum, 0);
			Assert.assertEquals(msg.unlikesNum, 0);
	
		} catch(Exception e){
			e.printStackTrace();
			fail("Exception thrown."+e.getMessage());
		}			
		
	}
	
	@Test
	public void createMessage() {
		
		//com.vmesteonline.be.Message msg = new 
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
