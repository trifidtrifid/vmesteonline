package com.vmesteonline.be;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalBlobstoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageListPart;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.messageservice.Topic;
import com.vmesteonline.be.messageservice.TopicListPart;
import com.vmesteonline.be.utils.Defaults;

public class MessageServiceTests {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
			new LocalBlobstoreServiceTestConfig());
	public static String sessionId = "11111111111111111111111";

	AuthServiceImpl asi;
	UserServiceImpl usi;
	MessageServiceImpl msi;
	HashMap<MessageType, Long> noLinkedMessages = new HashMap<MessageType, Long>();
	TreeMap<Long, String> noTags = new TreeMap<Long, String>();
	PersistenceManager pm;

	Group homeGroup;
	Group group200m;
	Group group2000m;

	Rubric topicRubric;
	String topicSubject = "Test topic";

	private Topic createTopic() throws Exception {
		return msi.createTopic(homeGroup.getId(), topicSubject, MessageType.BASE, "Content of the first topic is a simple string", noLinkedMessages,
				noTags, topicRubric.getId(), 0L);
	}

	private Topic createTopic(long groupId) throws Exception {
		return msi.createTopic(groupId, topicSubject, MessageType.BASE, "Content of the first topic is a simple string", noLinkedMessages, noTags,
				topicRubric.getId(), 0L);
	}

	@Before
	public void setUp() throws Exception {
		helper.setUp();
		Assert.assertTrue(Defaults.initDefaultData());

		pm = PMF.get().getPersistenceManager();
		asi = new AuthServiceImpl(sessionId);
		Assert.assertTrue(asi.login(Defaults.user1email, Defaults.user1pass));
		usi = new UserServiceImpl(sessionId);
		msi = new MessageServiceImpl(sessionId);
		List<Rubric> userRubrics = usi.getUserRubrics();
		Assert.assertTrue(userRubrics.size() > 0);
		Assert.assertTrue(userRubrics.get(0) != null);
		topicRubric = userRubrics.get(0);

		List<Group> userGroups = usi.getUserGroups();
		Assert.assertTrue(userGroups.size() > 0);
		Assert.assertTrue(userGroups.get(0) != null);
		homeGroup = userGroups.get(1);
		group200m = userGroups.get(2);
		group2000m = userGroups.get(3);
	}

	@After
	public void tearDown() throws Exception {
		if (pm != null)
			pm.close();
		helper.tearDown();
	}

	@Test
	public void testCreateTopicAndTwoReplies() {
		// create locations
		try {
			VoUser user1 = asi.getUserByEmail(Defaults.user1email, pm);

			Topic topic = createTopic();
			Assert.assertNotNull(topic.getId());
			long homeGroupId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			Message msg = msi.createMessage(topic.getId(), 0, homeGroupId, MessageType.BASE, "Content of the first message in the topic", noLinkedMessages,
					noTags, 0L);

			Assert.assertEquals(msg.getTopicId(), topic.getId());
			Assert.assertEquals(msg.getParentId(), topic.getMessage().getId());
			Assert.assertEquals(msg.getAuthorId(), topic.getMessage().getAuthorId());
			Assert.assertEquals(msg.getAuthorId(), user1.getId());
			Assert.assertEquals(msg.likesNum, 0);
			Assert.assertEquals(msg.unlikesNum, 0);

			Message msg2 = msi.createMessage(topic.getId(), msg.getId(), homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);
			Assert.assertEquals(msg2.getTopicId(), topic.getId());
			Assert.assertEquals(msg2.getParentId(), msg.getId());
			Assert.assertEquals(msg2.getAuthorId(), user1.getId());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetChildMessagesInTopic() {
		// create locations
		try {

			Topic topic = createTopic();
			Assert.assertNotNull(topic.getId());
			long homeGroupId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			Message msg = msi.createMessage(topic.getId(), 0, homeGroupId, MessageType.BASE, "Content of the first message in the topic", noLinkedMessages,
					noTags, 0L);
			msi.createMessage(topic.getId(), msg.getId(), homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic", noLinkedMessages,
					noTags, 0L);
			TopicListPart tlp = msi.getTopics(homeGroup.getId(), topicRubric.getId(), 0, 0L, 10);
			Assert.assertEquals(2, tlp.topics.get(0).getMessageNum());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetChildMessagesNumInMessage() {
		try {
			Topic topic = createTopic();
			Assert.assertNotNull(topic.getId());
			long homeGroupId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			Message msg1 = msi.createMessage(topic.getId(), 0, homeGroupId, MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg = msi.createMessage(topic.getId(), msg1.getId(), homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);
			msg = msi.createMessage(topic.getId(), msg.getId(), homeGroupId, MessageType.BASE, "Content of the third message in the topic",
					noLinkedMessages, noTags, 0L);
			msi.createMessage(topic.getId(), msg.getId(), homeGroupId, MessageType.BASE, "Content of the fourth message in the topic", noLinkedMessages,
					noTags, 0L);

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, 0, false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(3, mlp.messages.get(0).getChildMsgsNum());

			mlp = msi.getMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, msg1.getId(), false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(3, mlp.totalSize);
			Assert.assertEquals(2, mlp.messages.get(0).getChildMsgsNum());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetTopics() {

		try {
			Topic tpc = createTopic();
			TopicListPart rTopic = msi.getTopics(homeGroup.getId(), topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertEquals(tpc.getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(topicSubject, rTopic.topics.get(0).getSubject());
			Assert.assertNotNull(rTopic.topics.get(0).userInfo);
			Assert.assertEquals(Defaults.user1name, rTopic.topics.get(0).userInfo.firstName);
			Assert.assertEquals(Defaults.user1lastName, rTopic.topics.get(0).userInfo.lastName);

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
			}

			TopicListPart rTopic = msi.getTopics(homeGroup.getId(), topicRubric.getId(), 0, 0L, 5);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(5, rTopic.totalSize);
			Assert.assertEquals(tpcs.get(0).getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(tpcs.get(1).getId(), rTopic.topics.get(1).getId());
			Assert.assertEquals(tpcs.get(2).getId(), rTopic.topics.get(2).getId());
			Assert.assertEquals(tpcs.get(3).getId(), rTopic.topics.get(3).getId());
			Assert.assertEquals(tpcs.get(4).getId(), rTopic.topics.get(4).getId());

			rTopic = msi.getTopics(homeGroup.getId(), topicRubric.getId(), 0, rTopic.topics.get(4).getId(), 5);
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
			long user1homeGroupId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			long user2homeGroupId = getUserGroupId(Defaults.user2email, Defaults.radiusHome);

			Topic topic = createTopic();
			Message msg = msi.createMessage(topic.getId(), 0, user1homeGroupId, MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg1 = msi.createMessage(topic.getId(), msg.getId(), user2homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg2 = msi.createMessage(topic.getId(), msg1.getId(), user2homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg3 = msi.createMessage(topic.getId(), 0, user2homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, 0, false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(msg3.getId(), mlp.messages.get(1).getId());

			mlp = msi.getMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, msg.getId(), false, 10);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getOffset());

			Assert.assertEquals(msg2.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(2, mlp.messages.get(1).getOffset());

			mlp = msi.getMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, msg.getId(), false, 1);
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
			Assert.assertEquals(1, msi.likeOrDislikeTopic(topic.getId(), 1).likes);
			Assert.assertEquals(0, msi.likeOrDislikeTopic(topic.getId(), 1).dislikes);

			TopicListPart rTopic = msi.getTopics(homeGroup.getId(), topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertEquals(topic.getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(1, rTopic.topics.get(0).getLikesNum());
			Assert.assertEquals(0, rTopic.topics.get(0).getUnlikesNum());

			Assert.assertNotNull(rTopic.topics.get(0).getUsertTopic());
			Assert.assertTrue(rTopic.topics.get(0).getUsertTopic().likes);
			Assert.assertTrue(rTopic.topics.get(0).getUsertTopic().isread);
			Assert.assertFalse(rTopic.topics.get(0).getUsertTopic().unlikes);

			Assert.assertEquals(1, msi.likeOrDislikeTopic(topic.getId(), -1).dislikes);
			Assert.assertEquals(0, msi.likeOrDislikeTopic(topic.getId(), -1).likes);
			rTopic = msi.getTopics(homeGroup.getId(), topicRubric.getId(), 0, 0L, 10);
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
			VoUser user1 = asi.getUserByEmail(Defaults.user1email, pm);
			VoUser user2 = asi.getUserByEmail(Defaults.user2email, pm);
			long user1homeGroupId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			long user2homeGroupId = getUserGroupId(Defaults.user2email, Defaults.radiusHome);

			Topic topic = createTopic();
			Message msg = msi.createMessage(topic.getId(), 0, user1homeGroupId, MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg1 = msi.createMessage(topic.getId(), msg.getId(), user2homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);

			Assert.assertTrue(asi.login(Defaults.user2email, Defaults.user2pass));
			Message msg2 = msi.createMessage(topic.getId(), msg1.getId(), user2homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, 0L);
			msi.createMessage(topic.getId(), 0, user2homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic", noLinkedMessages, noTags,
					user1.getId());

			Assert.assertTrue(asi.login(Defaults.user1email, Defaults.user1pass));

			msi.createMessage(topic.getId(), msg2.getId(), user1homeGroupId, MessageType.BASE, "Content of the SECOND message in the topic",
					noLinkedMessages, noTags, user2.getId());

			Assert.assertTrue(asi.login(Defaults.user3email, Defaults.user3pass));

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user3email, Defaults.radiusHome), MessageType.BASE, 0,
					false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());

			mlp = msi.getMessages(topic.getId(), getUserGroupId(Defaults.user3email, Defaults.radiusHome), MessageType.BASE, msg.getId(), false, 10);
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

	@Test
	public void testGetTopicsFromSameGroupAnotherUser() {

		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusMedium));

			asi.login(Defaults.user2email, Defaults.user2pass);
			long grId = getUserGroupId(Defaults.user2email, Defaults.radiusMedium);
			TopicListPart rTopic = msi.getTopics(grId, topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetTopicsFromSmallerGroupAnotherUser() {

		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));

			asi.login(Defaults.user2email, Defaults.user2pass);
			long grId = getUserGroupId(Defaults.user2email, Defaults.radiusStarecase);
			TopicListPart rTopic = msi.getTopics(grId, topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetTopicsFromBiggerGroupSameUser() {

		try {
			createTopic();
			long grId = getUserGroupId(Defaults.user1email, Defaults.radiusSmall);
			TopicListPart rTopic = msi.getTopics(grId, topicRubric.getId(), 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	long getUserGroupId(String email, int radius) {
		VoUser user = asi.getUserByEmail(email, pm);
		for (VoUserGroup ug : user.getGroups()) {
			if (ug.getRadius() == radius) {
				return ug.getId();
			}
		}
		return 0L;
	}

	@Test
	public void testGetFirstLeveMessages() {
		try {

			Topic topic = createTopic();
			Message msg = msi.createMessage(topic.getId(), 0, homeGroup.getId(), MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg1 = msi.createMessage(topic.getId(), 0, homeGroup.getId(), MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg2 = msi.createMessage(topic.getId(), 0, homeGroup.getId(), MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg3 = msi.createMessage(topic.getId(), 0, homeGroup.getId(), MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg4 = msi.createMessage(topic.getId(), 0, homeGroup.getId(), MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);
			Message msg5 = msi.createMessage(topic.getId(), 0, homeGroup.getId(), MessageType.BASE, "Content of the first message in the topic",
					noLinkedMessages, noTags, 0L);

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, 0, false, 2);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(msg1.getId(), mlp.messages.get(1).getId());

			mlp = msi.getFirstLevelMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, msg2.getId(), false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(3, mlp.totalSize);
			Assert.assertEquals(msg3.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(msg4.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(msg5.getId(), mlp.messages.get(2).getId());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	// topic 2000m
	// -msg1 2000m
	// --msg2 2000m
	// --msg3 200m
	// ---msg4 200m
	// -msg5 200m

	@Test
	public void testGetMessagesBiggerGroup() {
		try {

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, 2000));
			Message msg1 = msi.createMessage(topic.getId(), 0, getUserGroupId(Defaults.user1email, 2000), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			Message msg2 = msi.createMessage(topic.getId(), msg1.getId(), getUserGroupId(Defaults.user1email, 2000), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			Message msg3 = msi.createMessage(topic.getId(), msg1.getId(), getUserGroupId(Defaults.user1email, 200), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			Message msg4 = msi.createMessage(topic.getId(), msg3.getId(), getUserGroupId(Defaults.user1email, 200), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			Message msg5 = msi.createMessage(topic.getId(), 0, getUserGroupId(Defaults.user1email, 200), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);

			Assert.assertTrue(asi.login(Defaults.user2email, Defaults.user2pass));

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user2email, 2000), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getChildMsgsNum());

			mlp = msi.getMessages(topic.getId(), getUserGroupId(Defaults.user2email, 2000), MessageType.BASE, msg1.getId(), false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg2.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(0, mlp.messages.get(0).getChildMsgsNum());

			mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user2email, 200), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(3, mlp.messages.get(0).getChildMsgsNum());

			Assert.assertEquals(msg5.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(0, mlp.messages.get(1).getChildMsgsNum());

			mlp = msi.getMessages(topic.getId(), getUserGroupId(Defaults.user2email, 200), MessageType.BASE, msg1.getId(), false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(3, mlp.totalSize);
			Assert.assertEquals(msg2.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(0, mlp.messages.get(0).getChildMsgsNum());

			Assert.assertEquals(msg3.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(1, mlp.messages.get(1).getChildMsgsNum());

			Assert.assertEquals(msg4.getId(), mlp.messages.get(2).getId());
			Assert.assertEquals(0, mlp.messages.get(2).getChildMsgsNum());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	// topic 2000m A
	// -msg1 20m B
	// --msg2 20m A

	@Test
	public void testGetMessagesBiggerGroupDifferentUsers() {
		try {

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, 2000));
			asi.login(Defaults.user2email, Defaults.user2pass);
			Message msg1 = msi.createMessage(topic.getId(), 0, getUserGroupId(Defaults.user1email, 20), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			asi.login(Defaults.user1email, Defaults.user1pass);
			Message msg2 = msi.createMessage(topic.getId(), msg1.getId(), getUserGroupId(Defaults.user1email, 20), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);

			asi.login(Defaults.user2email, Defaults.user2pass);
			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user2email, 2000), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(0, mlp.totalSize);

			mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user2email, 20), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);

			mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user2email, 0), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(0, mlp.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	// topic 2000m A
	// -msg1 2000m C

	@Test
	public void testGetMessagesBiggerGroupDifferentUsersAC() {
		try {

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, 2000));
			asi.login(Defaults.user3email, Defaults.user3pass);
			Message msg1 = msi.createMessage(topic.getId(), 0, getUserGroupId(Defaults.user3email, 2000), MessageType.BASE,
					"Content of the first message in the topic", noLinkedMessages, noTags, 0L);
			asi.login(Defaults.user1email, Defaults.user1pass);

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user1email, 2000), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);

			mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user1email, 200), MessageType.BASE, 0, false, 20);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(0, mlp.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

}
