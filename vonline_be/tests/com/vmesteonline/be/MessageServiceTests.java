package com.vmesteonline.be;

// ======================================================================================================================
//inviteCode 1 addr zan 32 k 3 kv 5 staircase 1 user a
//inviteCode 2 addr zan 32 k 3 kv 50 staircase 2 user b 
//inviteCode 3 addr zan 32 k 3 kv 51 staircase 2  user c
//inviteCode 4 addr zan 35 kv 35 staircase 1 user d
//inviteCode 5 addr resp 6 kv 5 staircase 1 user e
//=======================================================================================================================

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageListPart;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.messageservice.Poll;
import com.vmesteonline.be.messageservice.Topic;
import com.vmesteonline.be.messageservice.TopicListPart;
import com.vmesteonline.be.messageservice.UserMessage;
import com.vmesteonline.be.messageservice.UserTopic;
import com.vmesteonline.be.messageservice.WallItem;
import com.vmesteonline.be.utils.Defaults;

public class MessageServiceTests extends TestWorkAround {

	private Message createMessage(long tpcId, long msgId, MessageType type, String anonName) throws Exception {
		Message msg = new Message(0, msgId, type, tpcId, getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, (int) (System.currentTimeMillis() / 1000L), 0, "test content", 0, 0,
				new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, anonName, null, null, 0);
		if (type == MessageType.BLOG)
			return msi.postBlogMessage(msg);
		else
			return msi.postMessage(msg);

	}

	private Poll createPoll() {
		Poll poll = new Poll();
		poll.subject = "test poll";
		poll.names = new ArrayList<String>();
		poll.names.add("first");
		poll.names.add("second");
		return poll;
	}

	private Message createMessage(long tpcId, long msgId) throws Exception {
		Message msg = new Message(0, msgId, MessageType.BASE, tpcId, getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, (int) (System.currentTimeMillis() / 1000L), 0, "test content",
				0, 0, new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null, null,
				null,0);
		return msi.postMessage(msg);
	}


	private Topic createTopic(long groupId) throws Exception {
		return createTopic(groupId, MessageType.BASE);
	}

	private Topic createTopic(long groupId, MessageType type) throws Exception {
		Message msg = new Message(0, 0, type, 0, groupId, 0, 0, 0, "Content of the first topic is a simple string", 0, 0,
				new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null, null, null,0);
		Topic topic = new Topic(0, topicSubject, msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, null, null);
		return msi.postTopic(topic);
	}

	@Before
	public void setUp() throws Exception {
		Assert.assertTrue(init());
	}

	@After
	public void tearDown() throws Exception {
		close();
	}

	@Test
	public void testCreateTopticAndTwoReplies() {
		// create locations
		try {
			VoUser user1 = asi.getUserByEmail(Defaults.user1email, pm);

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			Assert.assertNotNull(topic.getId());
			Message msg = createMessage(topic.getId(), 0);

			Assert.assertEquals(msg.getTopicId(), topic.getId());
			Assert.assertEquals(msg.getParentId(), topic.getMessage().getId());
			Assert.assertEquals(msg.getAuthorId(), topic.getMessage().getAuthorId());
			Assert.assertEquals(msg.getAuthorId(), user1.getId());
			Assert.assertEquals(msg.likesNum, 0);
			Assert.assertEquals(msg.unlikesNum, 0);

			Message msg2 = createMessage(topic.getId(), msg.getId());
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

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			Assert.assertNotNull(topic.getId());
			Message msg = createMessage(topic.getId(), 0);
			createMessage(topic.getId(), msg.getId());
			TopicListPart tlp = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertEquals(2, tlp.topics.get(0).getMessageNum());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetChildMessagesNumInMessage() {
		try {
			Topic topic = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			Assert.assertNotNull(topic.getId());
			Message msg1 = createMessage(topic.getId(), 0);
			Message msg = createMessage(topic.getId(), msg1.getId());
			msg = createMessage(topic.getId(), msg.getId());
			createMessage(topic.getId(), msg.getId());

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(),getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, 0, false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());

			mlp = msi.getMessages(topic.getId(), getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, msg1.getId(), false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(3, mlp.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetTopics() {

		try {
			Topic tpc = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 10);
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
	public void testGetAdvert() {
		try {
			Topic tpc = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.ADVERT);
			TopicListPart rTopic = msi.getAdverts(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 10);
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
	public void testGetWallItems() {

		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.WALL);
			List<WallItem> rTopic = msi.getWallItems(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 10000);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(2, rTopic.size());

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
				tpcs.add(createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase)));
			}

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 5);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(5, rTopic.totalSize);
			Assert.assertEquals(tpcs.get(0).getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(tpcs.get(1).getId(), rTopic.topics.get(1).getId());
			Assert.assertEquals(tpcs.get(2).getId(), rTopic.topics.get(2).getId());
			Assert.assertEquals(tpcs.get(3).getId(), rTopic.topics.get(3).getId());
			Assert.assertEquals(tpcs.get(4).getId(), rTopic.topics.get(4).getId());

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, rTopic.topics.get(4).getId(), 5);
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

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			Message msg = createMessage(topic.getId(), 0);
			Message msg1 = createMessage(topic.getId(), msg.getId());
			Message msg2 = createMessage(topic.getId(), msg1.getId());
			Message msg3 = createMessage(topic.getId(), 0);

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, 0, false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(msg3.getId(), mlp.messages.get(1).getId());

			mlp = msi.getMessages(topic.getId(), getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, msg.getId(), false, 10);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getOffset());

			Assert.assertEquals(msg2.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals(2, mlp.messages.get(1).getOffset());

			mlp = msi.getMessages(topic.getId(),getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, msg.getId(), false, 1);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(1, mlp.messages.get(0).getOffset());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}

	}

	@Test
	public void testPostTopicWithPoll() {

		try {
			Poll poll = createPoll();
			long grId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);

			Message msg = new Message(0, 0, MessageType.BASE, 0,getUserGroupId(Defaults.user1email, Defaults.radiusHome), 0, 0, 0, "Content of the first topic is a simple string", 0, 0,
					new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null, null, null,0);
			Topic topic = new Topic(0, "testSubject", msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, poll, null);
			msi.postTopic(topic);

			TopicListPart rTopic = msi.getTopics(grId, 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertNotNull(rTopic.topics.get(0).poll);
			Assert.assertEquals(poll.subject, rTopic.topics.get(0).poll.subject);
			Assert.assertEquals(poll.names.get(0), rTopic.topics.get(0).poll.names.get(0));
			Assert.assertEquals(0, rTopic.topics.get(0).poll.values.get(0).intValue());
			Assert.assertEquals(0, rTopic.topics.get(0).poll.values.get(1).intValue());
			Assert.assertEquals("Aname", rTopic.topics.get(0).userInfo.firstName);
			Assert.assertEquals("Afamily", rTopic.topics.get(0).userInfo.lastName);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testdoPoll() {

		try {
			Poll poll = createPoll();

			Message msg = new Message(0, 0, MessageType.BASE, 0, getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0, "Content of the first topic is a simple string", 0, 0,
					new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null, null, null,0);
			Topic topic = new Topic(0, "testSubject", msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, poll, null);
			topic = msi.postTopic(topic);

			msi.doPoll(topic.poll.pollId, 0);
			msi.doPoll(topic.poll.pollId, 1);
			msi.doPoll(topic.poll.pollId, 1);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);
			Assert.assertNotNull(rTopic.topics.get(0).poll);
			Assert.assertEquals(poll.subject, rTopic.topics.get(0).poll.subject);
			Assert.assertEquals(poll.names.get(0), rTopic.topics.get(0).poll.names.get(0));
			Assert.assertTrue(rTopic.topics.get(0).poll.alreadyPoll);
			Assert.assertEquals(1, rTopic.topics.get(0).poll.values.get(0).intValue());
			Assert.assertEquals(0, rTopic.topics.get(0).poll.values.get(1).intValue());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetFirstLeveMessages() {
		try {

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			Message msg = createMessage(topic.getId(), 0);
			Message msg1 = createMessage(topic.getId(), 0);
			Message msg2 = createMessage(topic.getId(), 0);
			Message msg3 = createMessage(topic.getId(), 0);
			Message msg4 = createMessage(topic.getId(), 0);
			Message msg5 = createMessage(topic.getId(), 0);

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, 0, false, 2);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals(msg1.getId(), mlp.messages.get(1).getId());

			mlp = msi.getFirstLevelMessages(topic.getId(), getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BASE, msg2.getId(), false, 10);
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

	@Test
	// сообщение подъезд видно себе видно в подъезде, видно в доме, видно в округе
	public void testGetTopicsStarecaseToSelf() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение подъезд сосед по подъезду видно в подъезде видно в доме видно в округе
	public void testGetTopicsStarecaseToStaresase() {
		try {
			asi.login(Defaults.user2email, Defaults.user2pass);
			createTopic(getUserGroupId(Defaults.user2email, Defaults.radiusStarecase));
			asi.login(Defaults.user3email, Defaults.user3pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение подъезд сосед по дому не видно не видно не видно
	public void testGetTopicsStarecaseToHome() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			asi.login(Defaults.user2email, Defaults.user2pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение подъезд сосед по округе не видно не видно не видно
	public void testGetTopicsStarecaseToSmall() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase));
			asi.login(Defaults.user4email, Defaults.user4pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по дому себе в подъезд не видно, видно в доме, видно в округе
	public void testGetTopicsHomeToSelf() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusHome));
			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по дому соседу по подъезду в подъезде не видно, видно в доме, видно в округе
	public void testGetTopicsHomeToStaresase() {
		try {
			asi.login(Defaults.user2email, Defaults.user2pass);
			createTopic(getUserGroupId(Defaults.user2email, Defaults.radiusHome));
			asi.login(Defaults.user3email, Defaults.user3pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по дому соседу по дому в подъезде не видно, видно в доме, видно в округе
	public void testGetTopicsHomeToHome() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusHome));
			asi.login(Defaults.user2email, Defaults.user2pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение подъезд сосед по округе не видно не видно не видно
	public void testGetTopicsHomeToSmall() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusHome));
			asi.login(Defaults.user4email, Defaults.user4pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по округе себе в подъезде не видно, в доме не видно, видно в округе
	public void testGetTopicsSmallToSelf() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusSmall));
			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user1email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по округе соседу по подъезду в подъезде не видно, в доме не видно, видно в округе
	public void testGetTopicsSmallToStarecase() {
		try {
			asi.login(Defaults.user2email, Defaults.user2pass);
			createTopic(getUserGroupId(Defaults.user2email, Defaults.radiusSmall));
			asi.login(Defaults.user3email, Defaults.user3pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user3email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по округе соседу по дому в подъезде не видно, в доме не видно, видно в округе
	public void testGetTopicsSmallToHome() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusSmall));
			asi.login(Defaults.user2email, Defaults.user2pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user2email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	// сообщение по округе соседу по округе в подъезде не видно, в доме не видно, видно в округе
	public void testGetTopicsSmallToSmall() {
		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusSmall));
			asi.login(Defaults.user4email, Defaults.user4pass);

			TopicListPart rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusStarecase), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusHome), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

			rTopic = msi.getTopics(getUserGroupId(Defaults.user4email, Defaults.radiusSmall), 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testBlogMessages() {
		try {

			Topic topic = createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusStarecase), MessageType.BLOG);

			Message msg = createMessage(topic.getId(), 0, MessageType.BLOG, "");
			asi.logout();
			Message msg1 = createMessage(topic.getId(), 0, MessageType.BLOG, "Anonimous");

			MessageListPart mlp = msi.getMessagesAsList(topic.getId(), MessageType.BLOG, 0, false, 10);
			Assert.assertEquals(2, mlp.totalSize);
			Assert.assertEquals(msg.getId(), mlp.messages.get(0).getId());
			Assert.assertEquals("Aname Afamily", mlp.messages.get(0).getAnonName());
			Assert.assertNotNull(mlp.messages.get(0).getUserInfo());
			Assert.assertEquals("Aname", mlp.messages.get(0).getUserInfo().getFirstName());

			Assert.assertEquals(msg1.getId(), mlp.messages.get(1).getId());
			Assert.assertEquals("Anonimous", mlp.messages.get(1).getAnonName());

			TopicListPart tlp = msi.getBlog(0, 5);
			Assert.assertEquals(1, tlp.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}
}
