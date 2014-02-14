package com.vmesteonline.be;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import org.apache.thrift.TException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;

public class MessageServiceTests {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private static String sessionId = "11111111111111111111111";
	private static String user1email = "a1@b.com";
	private static String user1pass = "123";

	private static String user2email = "a2@b.com";
	private static String user2pass = "123";

	private static String user3email = "a3@b.com";
	private static String user3pass = "123";

	AuthServiceImpl asi;
	UserServiceImpl usi;
	MessageServiceImpl msi;
	HashMap<MessageType, Long> noLinkedMessages = new HashMap<MessageType, Long>();
	TreeMap<Long, String> noTags = new TreeMap<Long, String>();
	PersistenceManager pm;

	Group topicGroup;
	Rubric topicRubric;
	String topicSubject = "Test topic";

	private Topic createTopic() throws Exception {
		return msi.createTopic(topicGroup.getId(), topicSubject, MessageType.BASE, "Content of the first topic is a simple string", noLinkedMessages,
				noTags, topicRubric.getId(), 0L);
	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		MySQLJDBCConnector con = new MySQLJDBCConnector();
		con.execute("drop table if exists topic");

		pm = PMF.get().getPersistenceManager();
		asi = new AuthServiceImpl(sessionId);
		List<String> locCodes = UserServiceImpl.getLocationCodesForRegistration();
		asi.registerNewUser("Test1", "USer2", user1pass, user1email, locCodes.get(0));
		asi.registerNewUser("Test2", "USer2", "123", "a2@b.com", locCodes.get(1));
		asi.registerNewUser("Test3", "USer2", user3pass, user3email, locCodes.get(1));

		Assert.assertTrue(asi.login("a1@b.com", "123"));

		usi = new UserServiceImpl(sessionId);
		msi = new MessageServiceImpl(sessionId);

		List<Group> userGroups = usi.getUserGroups();
		Assert.assertTrue(userGroups.size() > 0);
		Assert.assertTrue(userGroups.get(0) != null);
		topicGroup = userGroups.get(0);

		List<Rubric> userRubrics = usi.getUserRubrics();
		Assert.assertTrue(userRubrics.size() > 0);
		Assert.assertTrue(userRubrics.get(0) != null);
		topicRubric = userRubrics.get(0);
	}

	@After
	public void tearDown() throws Exception {
		pm.close();
		helper.tearDown();
	}

	@Test
	public void testGetTopicsSTUB() {
		try {
			int offset = 0;
			do {
				TopicListPart topics = msi.getTopics(0, 0, 0, 0L, 10);
				for (Topic top : topics.getTopics()) {
					System.out.println("TopicID:" + top.getId() + " topic:" + top.getSubject());
					MessageListPart messages = msi.getMessages(top.getId(), 0, MessageType.BASE, 0L, false, 0, 100000);
					for (Message msg : messages.getMessages()) {
						System.out.println("msg ID:" + msg.getId() + " topic:" + msg.getTopicId() + " parent:" + msg.getParentId() + " :" + msg.getContent());
					}
				}
				offset += topics.getTopicsSize();
				if (offset >= topics.getTotalSize())
					break;
			} while (true);

		} catch (TException e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}

	@Test
	public void testCreateTopicAndTwoReplies() {
		// create locations
		try {
			VoUser user1 = asi.getUserByEmail("a1@b.com", pm);
			VoUser user2 = asi.getUserByEmail("a2@b.com", pm);

			Topic topic = createTopic();
			Assert.assertNotNull(topic.getId());
			Message msg = msi.createMessage(topic.getId(), 0, user1.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);

			Assert.assertEquals(msg.getTopicId(), topic.getId());
			Assert.assertEquals(msg.getParentId(), topic.getMessage().getId());
			Assert.assertEquals(msg.getAuthorId(), topic.getMessage().getAuthorId());
			Assert.assertEquals(msg.getAuthorId(), user1.getId().longValue());
			Assert.assertEquals(msg.likesNum, 0);
			Assert.assertEquals(msg.unlikesNum, 0);

			Message msg2 = msi.createMessage(topic.getId(), msg.getId(), user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, 0L);
			Assert.assertEquals(msg2.getTopicId(), topic.getId());
			Assert.assertEquals(msg2.getParentId(), msg.getId());
			Assert.assertEquals(msg2.getAuthorId(), user1.getId().longValue());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetTopics() {

		try {
			Topic tpc = createTopic();
			TopicListPart rTopic = msi.getTopics(topicGroup.getId(), topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertEquals(tpc.getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(topicSubject, rTopic.topics.get(0).getSubject());
			Assert.assertNotNull(rTopic.topics.get(0).userInfo);
			Assert.assertEquals("Test1", rTopic.topics.get(0).userInfo.firstName);
			Assert.assertEquals("USer2", rTopic.topics.get(0).userInfo.lastName);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	@Test
	public void testGetFirstFiveTopics() {
		try {
			List<Topic> tpcs = new ArrayList<Topic>();
			for (int i = 0; i < 7; i++) {
				tpcs.add(createTopic());
				Thread.sleep(1200);
			}

			TopicListPart rTopic = msi.getTopics(topicGroup.getId(), topicRubric.getId(), 0, 0L, 5);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(5, rTopic.totalSize);
			Assert.assertEquals(tpcs.get(0).getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(tpcs.get(1).getId(), rTopic.topics.get(1).getId());
			Assert.assertEquals(tpcs.get(2).getId(), rTopic.topics.get(2).getId());
			Assert.assertEquals(tpcs.get(3).getId(), rTopic.topics.get(3).getId());
			Assert.assertEquals(tpcs.get(4).getId(), rTopic.topics.get(4).getId());

			rTopic = msi.getTopics(topicGroup.getId(), topicRubric.getId(), 0, rTopic.topics.get(4).getId(), 5);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(2, rTopic.totalSize);
			Assert.assertEquals(tpcs.get(5).getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(tpcs.get(6).getId(), rTopic.topics.get(1).getId());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	// test data struct
	// topic
	// -msg
	// --msg1
	// ---msg2
	// -msg3

	@Test
	public void testGetMessages() {
		try {
			VoUser user1 = asi.getUserByEmail("a1@b.com", pm);
			VoUser user2 = asi.getUserByEmail("a2@b.com", pm);

			Topic topic = createTopic();
			Message msg = msi.createMessage(topic.getId(), 0, user1.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			Message msg1 = msi.createMessage(topic.getId(), msg.getId(), user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, 0L);
			Message msg2 = msi.createMessage(topic.getId(), msg1.getId(), user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, 0L);
			Message msg3 = msi.createMessage(topic.getId(), 0, user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, 0L);

			MessageListPart mlp = msi.getMessages(topic.getId(), topicGroup.getId(), MessageType.BASE, 0, false, 0, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(msg3.getId(), mlp.messages.get(1).getId());

			mlp = msi.getMessages(topic.getId(), topicGroup.getId(), MessageType.BASE, msg.getId(), false, 0, 10);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getOffset());

			Assert.assertEquals(msg2.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(2, mlp.messages.get(1).getOffset());

			mlp = msi.getMessages(topic.getId(), topicGroup.getId(), MessageType.BASE, msg.getId(), false, 0, 1);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getOffset());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	@Test
	public void testLikeDislikeTopic() {

		try {
			Topic topic = createTopic();
			Assert.assertEquals(1, msi.likeTopic(topic.getId()));

			TopicListPart rTopic = msi.getTopics(topicGroup.getId(), topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertEquals(topic.getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(1, rTopic.topics.get(0).getLikesNum());
			Assert.assertEquals(0, rTopic.topics.get(0).getUnlikesNum());

			Assert.assertNotNull(rTopic.topics.get(0).getUsertTopic());
			Assert.assertTrue(rTopic.topics.get(0).getUsertTopic().likes);
			Assert.assertTrue(rTopic.topics.get(0).getUsertTopic().isread);
			Assert.assertFalse(rTopic.topics.get(0).getUsertTopic().unlikes);

			Assert.assertEquals(1, msi.dislikeTopic(topic.getId()));
			rTopic = msi.getTopics(topicGroup.getId(), topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertEquals(topic.getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(0, rTopic.topics.get(0).getLikesNum());
			Assert.assertEquals(1, rTopic.topics.get(0).getUnlikesNum());
			Assert.assertNotNull(rTopic.topics.get(0).getUsertTopic());
			Assert.assertFalse(rTopic.topics.get(0).getUsertTopic().likes);
			Assert.assertTrue(rTopic.topics.get(0).getUsertTopic().isread);
			Assert.assertTrue(rTopic.topics.get(0).getUsertTopic().unlikes);

		} catch (Exception e) {
			e.printStackTrace();
			fail("exception: " + e.getMessage());
		}

	}

	// test data struct
	// topic - user1
	// -msg - user1
	// --msg1 - user1
	// ---msg2 - user2
	// ----msg4 user1->user2
	// -msg3 - user2->user1

	@Test
	public void testGetPrivateMessage() {
		try {
			VoUser user1 = asi.getUserByEmail("a1@b.com", pm);
			VoUser user2 = asi.getUserByEmail("a2@b.com", pm);

			Topic topic = createTopic();
			Message msg = msi.createMessage(topic.getId(), 0, user1.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			Message msg1 = msi.createMessage(topic.getId(), msg.getId(), user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, 0L);

			Assert.assertTrue(asi.login(user2email, user2pass));
			Message msg2 = msi.createMessage(topic.getId(), msg1.getId(), user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, 0L);
			Message msg3 = msi.createMessage(topic.getId(), 0, user2.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, user1.getId());

			Assert.assertTrue(asi.login(user1email, user1pass));
			Message msg4 = msi.createMessage(topic.getId(), msg2.getId(), user1.getHomeGroup().getId().getId(), MessageType.BASE,
					"Content of the SECOND message in the topic", noLinkedMessages, noTags, user2.getId());

			Assert.assertTrue(asi.login(user3email, user3pass));

			MessageListPart mlp = msi.getMessages(topic.getId(), topicGroup.getId(), MessageType.BASE, 0, false, 0, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());

			mlp = msi.getMessages(topic.getId(), topicGroup.getId(), MessageType.BASE, msg.getId(), false, 0, 10);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getOffset());

			Assert.assertEquals(msg2.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(2, mlp.messages.get(1).getOffset());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

}
