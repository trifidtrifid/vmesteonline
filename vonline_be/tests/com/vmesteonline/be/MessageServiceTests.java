package com.vmesteonline.be;

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

	private Message createMessage(long tpcId, long msgId) throws Exception {
		Message msg = new Message(0, msgId, MessageType.BASE, tpcId, homeGroup.getId(), 0, (int) (System.currentTimeMillis() / 1000L), 0, "test content", 0, 0,
				new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null);
		return msi.postMessage(msg);
	}

	private Topic createTopic() throws Exception {
		return createTopic(0, MessageType.BASE);
	}

	private Topic createTopic(long groupId) throws Exception {
		return createTopic(groupId, MessageType.BASE);
	}

	private Topic createTopic(long groupId, MessageType type) throws Exception {
		Message msg = new Message(groupId, 0, type, 0, homeGroup.getId(), 0, 0, 0, "Content of the first topic is a simple string", 0, 0,
				new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null);
		Topic topic = new Topic(0, topicSubject, msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, null);
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

			Topic topic = createTopic();
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

			Topic topic = createTopic();
			Assert.assertNotNull(topic.getId());
			Message msg = createMessage(topic.getId(), 0);
			createMessage(topic.getId(), msg.getId());
			TopicListPart tlp = msi.getTopics(homeGroup.getId(), 0, 0, 0L, 10);
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
			Message msg1 = createMessage(topic.getId(), 0);
			Message msg = createMessage(topic.getId(), msg1.getId());
			msg = createMessage(topic.getId(), msg.getId());
			createMessage(topic.getId(), msg.getId());

			MessageListPart mlp = msi.getFirstLevelMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, 0, false, 10);
			Assert.assertNotNull(mlp);
			Assert.assertEquals(1, mlp.totalSize);
			Assert.assertEquals(msg1.getId(), mlp.messages.get(0).getId());

			mlp = msi.getMessages(topic.getId(), homeGroup.getId(), MessageType.BASE, msg1.getId(), false, 10);
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
			Topic tpc = createTopic();
			TopicListPart rTopic = msi.getTopics(homeGroup.getId(), 0, 0, 0L, 10);
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
			Topic tpc = createTopic(homeGroup.getId(), MessageType.ADVERT);
			TopicListPart rTopic = msi.getAdverts(homeGroup.getId(), 0, 10);
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
			List<WallItem> rTopic = msi.getWallItems(homeGroup.getId());
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(1, rTopic.size());

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

			TopicListPart rTopic = msi.getTopics(homeGroup.getId(), 0, 0, 0L, 5);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(5, rTopic.totalSize);
			Assert.assertEquals(tpcs.get(0).getId(), rTopic.topics.get(0).getId());
			Assert.assertEquals(tpcs.get(1).getId(), rTopic.topics.get(1).getId());
			Assert.assertEquals(tpcs.get(2).getId(), rTopic.topics.get(2).getId());
			Assert.assertEquals(tpcs.get(3).getId(), rTopic.topics.get(3).getId());
			Assert.assertEquals(tpcs.get(4).getId(), rTopic.topics.get(4).getId());

			rTopic = msi.getTopics(homeGroup.getId(), 0, 0, rTopic.topics.get(4).getId(), 5);
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

			Topic topic = createTopic();
			Message msg = createMessage(topic.getId(), 0);
			Message msg1 = createMessage(topic.getId(), msg.getId());
			Message msg2 = createMessage(topic.getId(), msg1.getId());
			Message msg3 = createMessage(topic.getId(), 0);

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

			TopicListPart rTopic = msi.getTopics(homeGroup.getId(), 0, 0, 0L, 10);
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
			rTopic = msi.getTopics(homeGroup.getId(), 0, 0, 0L, 10);
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

	@Test
	public void testGetTopicsFromSmallerGroup() {

		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusMedium));
			long grId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			TopicListPart rTopic = msi.getTopics(grId, 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testPostTopic() {

		try {
			Poll poll = createPoll();
			long grId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);

			Message msg = new Message(0, 0, MessageType.BASE, 0, homeGroup.getId(), 0, 0, 0, "Content of the first topic is a simple string", 0, 0,
					new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null);
			Topic topic = new Topic(0, "testSubject", msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, poll);
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

			Message msg = new Message(0, 0, MessageType.BASE, 0, homeGroup.getId(), 0, 0, 0, "Content of the first topic is a simple string", 0, 0,
					new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null, null);
			Topic topic = new Topic(0, "testSubject", msg, 0, 0, 0, 0, 0, 0, new UserTopic(), null, poll);
			topic = msi.postTopic(topic);

			msi.doPoll(topic.poll.pollId, 0);
			msi.doPoll(topic.poll.pollId, 1);
			msi.doPoll(topic.poll.pollId, 1);

			long grId = getUserGroupId(Defaults.user1email, Defaults.radiusHome);
			TopicListPart rTopic = msi.getTopics(grId, 0, 0, 0L, 10);
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

	private Poll createPoll() {
		Poll poll = new Poll();
		poll.subject = "test poll";
		poll.names = new ArrayList<String>();
		poll.names.add("first");
		poll.names.add("second");
		return poll;
	}

	@Test
	public void testGetTopicsFromSameGroupAnotherUser() {

		try {
			createTopic(getUserGroupId(Defaults.user1email, Defaults.radiusMedium));

			asi.login(Defaults.user2email, Defaults.user2pass);
			long grId = getUserGroupId(Defaults.user2email, Defaults.radiusMedium);
			TopicListPart rTopic = msi.getTopics(grId, 0, 0, 0L, 10);
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
			TopicListPart rTopic = msi.getTopics(grId, 0, 0, 0L, 10);
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
			TopicListPart rTopic = msi.getTopics(grId, 0, 0, 0L, 10);
			Assert.assertNotNull(rTopic);
			Assert.assertEquals(0, rTopic.totalSize);

		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception thrown." + e.getMessage());
		}
	}

	@Test
	public void testGetFirstLeveMessages() {
		try {

			Topic topic = createTopic();
			Message msg = createMessage(topic.getId(), 0);
			Message msg1 = createMessage(topic.getId(), 0);
			Message msg2 = createMessage(topic.getId(), 0);
			Message msg3 = createMessage(topic.getId(), 0);
			Message msg4 = createMessage(topic.getId(), 0);
			Message msg5 = createMessage(topic.getId(), 0);

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
}
