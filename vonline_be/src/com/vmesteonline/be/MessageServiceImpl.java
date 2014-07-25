package com.vmesteonline.be;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.data.VoDatastoreHelper;
import com.vmesteonline.be.jdo2.VoBaseMessage;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoPoll;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.VoUserMessage;
import com.vmesteonline.be.jdo2.VoUserObject;
import com.vmesteonline.be.jdo2.VoUserTopic;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageListPart;
import com.vmesteonline.be.messageservice.MessageService.Iface;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.messageservice.Poll;
import com.vmesteonline.be.messageservice.Topic;
import com.vmesteonline.be.messageservice.TopicListPart;
import com.vmesteonline.be.messageservice.UserMessage;
import com.vmesteonline.be.messageservice.UserOpinion;
import com.vmesteonline.be.messageservice.WallItem;
import com.vmesteonline.be.utils.VoHelper;

public class MessageServiceImpl extends ServiceImpl implements Iface {

	public MessageServiceImpl() throws InvalidOperation {
		initDb();
	}

	public MessageServiceImpl(String sessId) throws InvalidOperation {
		super(sessId);
		initDb();
	}

	@Override
	public List<WallItem> getWallItems(long groupId) throws InvalidOperation {
		List<WallItem> wallItems = new ArrayList<WallItem>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			try {
				VoUser user = getCurrentUser(pm);
				pm.retrieve(user);
				VoUserGroup group = user.getGroupById(groupId);
				// todo add last loaded and length
				List<VoTopic> topics = getTopics(group, MessageType.WALL, 0, 10000, pm);

				if (topics.isEmpty()) {
					logger.fine("can't find any topics");
					return wallItems;
				}
				for (VoTopic voTopic : topics) {
					Topic tpc = voTopic.getTopic(user.getId(), pm);

					tpc.userInfo = UserServiceImpl.getShortUserInfo(voTopic.getAuthorId().getId());

					MessageListPart mlp = getMessagesAsList(tpc.id, 0, MessageType.BASE, 0, false, 10000);
					if (mlp.totalSize > 0)
						logger.info("find msgs " + mlp.messages.size());

					WallItem wi = new WallItem(mlp.messages, tpc);
					wallItems.add(wi);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			pm.close();
		}

		return wallItems;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MessageListPart getMessagesAsList(long topicId, long groupId, MessageType messageType, long lastLoadedId, boolean archived, int length)
			throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		try {

			Query q = pm.newQuery(VoMessage.class);
			q.setFilter("topicId == " + topicId);
			List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
			Collections.sort(voMsgs, new VoMessage.ComparatorByCreateDate());

			VoUser user = getCurrentUser(pm);

			if (lastLoadedId != 0) {
				List<VoMessage> subLst = null;
				for (int i = 0; i < voMsgs.size() - 1; i++) {
					if (voMsgs.get(i).getId() == lastLoadedId)
						subLst = voMsgs.subList(i + 1, voMsgs.size());
				}
				voMsgs = (subLst == null) ? new ArrayList<VoMessage>() : subLst;
			}
			return createMlp(voMsgs, user.getId(), pm, length);
		} finally {
			pm.close();
		}

	}

	@Override
	public MessageListPart getFirstLevelMessages(long topicId, long groupId, MessageType messageType, long lastLoadedId, boolean archived, int length)
			throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoUser user = getCurrentUser(pm);
			MessagesTree tree = MessagesTree.createMessageTree(topicId, pm);
			List<VoMessage> voMsgs = tree.getTreeMessagesFirstLevel(new MessagesTree.Filters(user.getId(), user.getGroupById(groupId)));

			if (lastLoadedId != 0) {
				List<VoMessage> subLst = null;
				for (int i = 0; i < voMsgs.size() - 1; i++) {
					if (voMsgs.get(i).getId() == lastLoadedId)
						subLst = voMsgs.subList(i + 1, voMsgs.size());
				}
				voMsgs = (subLst == null) ? new ArrayList<VoMessage>() : subLst;
			}

			return createMlp(voMsgs, user.getId(), pm, length);
		} finally {
			pm.close();
		}

	}

	// todo remove method
	public Message createMessage(long topicId, long parentId, long groupId, MessageType type, String content, Map<MessageType, Long> linkedMessages,
			Map<Long, String> tags, long recipientId) throws InvalidOperation, TException {

		int now = (int) (System.currentTimeMillis() / 1000L);
		Message newMessage = new Message(0, parentId, type, topicId, groupId, 0, now, 0, content, 0, 0, new HashMap<MessageType, Long>(),
				new HashMap<Long, String>(), new UserMessage(true, false, false), 0, null, null, null);
		newMessage.recipientId = recipientId;
		postMessage(newMessage);
		return null;
	}

	// ===================================================================================================================================
	@Override
	public MessageListPart getMessages(long topicId, long groupId, MessageType messageType, long lastLoadedMsgId, boolean archived, int length)
			throws InvalidOperation, TException {

		PersistenceManager pm = PMF.getPm();
		try {
			VoUser user = getCurrentUser(pm);
			MessagesTree tree = MessagesTree.createMessageTree(topicId, pm);
			List<VoMessage> voMsgs = tree.getTreeMessagesAfter(lastLoadedMsgId, new MessagesTree.Filters(user.getId(), user.getGroupById(groupId)));
			return createMlp(voMsgs, user.getId(), pm, length);
		} finally {
			pm.close();
		}

	}

	List<VoTopic> getTopics(VoUserGroup group, MessageType type, long lastLoadedTopicId, int length, PersistenceManager pm) {

		String req = "select `id` from topic where longitude <= " + VoHelper.getLongitudeMax(group.getLongitude(), group.getRadius()).toPlainString()
				+ " and longitude >= " + VoHelper.getLongitudeMin(group.getLongitude(), group.getRadius()).toPlainString() + " and lattitude <= "
				+ VoHelper.getLatitudeMax(group.getLatitude(), group.getRadius()).toPlainString() + " and lattitude >= "
				+ VoHelper.getLatitudeMin(group.getLatitude(), group.getRadius()).toPlainString() + " and radius >= " + group.getRadius()
				+ " order by createTime desc";
		List<VoTopic> topics = new ArrayList<VoTopic>();
		try {
			ResultSet rs = con.executeQuery(req);
			boolean addTopic = 0 == lastLoadedTopicId ? true : false;
			while (rs.next() && topics.size() < length) {
				long topicId = rs.getLong(1);
				VoTopic topic = pm.getObjectById(VoTopic.class, topicId);
				if (addTopic) {
					if (type == MessageType.WALL || type == topic.getType())
						topics.add(topic);
				} else {
					if (topic.getId() == lastLoadedTopicId) {
						addTopic = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}

		return topics;
	}

	@SuppressWarnings("unchecked")
	@Override
	public TopicListPart getTopics(long groupId, long rubricId, int commmunityId, long lastLoadedTopicId, int length) throws InvalidOperation {

		TopicListPart mlp = new TopicListPart();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			try {
				VoUser user = getCurrentUser(pm);
				pm.retrieve(user);
				VoUserGroup group = user.getGroupById(groupId);
				List<VoTopic> topics = getTopics(group, MessageType.BASE, lastLoadedTopicId, length, pm);

				if (topics.isEmpty()) {
					logger.fine("can't find any topics");
					return mlp;
				}
				mlp.totalSize = topics.size();
				for (VoTopic voTopic : topics) {
					Topic tpc = voTopic.getTopic(user.getId(), pm);

					VoUserTopic voUserTopic = VoDatastoreHelper.<VoUserTopic> getUserMsg(VoUserTopic.class, user.getId(), tpc.getId(), pm);
					if (voUserTopic == null) {
						voUserTopic = new VoUserTopic();

						Query q = pm.newQuery(VoMessage.class);
						q.setFilter("topicId == " + voTopic.getId());
						List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
						MessagesTree tree = new MessagesTree(voMsgs);
						voUserTopic.setMessagesCount(tree.getTopicChildMessagesCount(new MessagesTree.Filters(user.getId(), group)));

					} else if (voUserTopic.getLastUpdateMessageCount() != voTopic.getLastUpdate()) {
						Query q = pm.newQuery(VoMessage.class);
						q.setFilter("topicId == " + voTopic.getId());
						List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
						MessagesTree tree = new MessagesTree(voMsgs);
						voUserTopic.setMessagesCount(tree.getTopicChildMessagesCount(new MessagesTree.Filters(user.getId(), group)));

					}

					tpc.usertTopic = voUserTopic.getUserTopic();
					tpc.userInfo = UserServiceImpl.getShortUserInfo(voTopic.getAuthorId().getId());
					tpc.setMessageNum(voUserTopic.getMessagesCount());
					mlp.addToTopics(tpc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} finally {
			pm.close();
		}
		return mlp;

	}

	@Override
	public long restoreTopicFromArchive(long topicId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long makeMessageLinked(long message1Id, long message2Id) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long markTopicUnintrested(long topicId, boolean interested) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long markReadMessage(long messageId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long markReadTopic(long topicId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long moveTopicToArchive(long topicId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Topic postTopic(Topic topic) throws InvalidOperation {

		PersistenceManager pm = PMF.getPm();
		try {
			try {
				if (0 == topic.getId()) {
					int now = (int) (System.currentTimeMillis() / 1000L);
					topic.lastUpdate = now;
					topic.message.created = now;
					topic.message.authorId = getCurrentUserId();

					VoTopic votopic = new VoTopic(topic);

					if (topic.poll != null) {

						VoPoll poll = VoPoll.create(topic.poll);
						pm.makePersistent(poll);
						votopic.setPollId(poll.getId());
						topic.poll.pollId = poll.getId();
					}

					pm.makePersistent(votopic);
					topic.setId(votopic.getId());

					VoUser user = getCurrentUser(pm);
					VoUserGroup ug = user.getGroupById(votopic.getUserGroupId());
					votopic.setLongitude(ug.getLongitude());
					votopic.setLatitude(ug.getLatitude());

					topic.userInfo = user.getShortUserInfo();
					con.execute("insert into topic (`id`, `longitude`, `lattitude`, `radius`, `rubricId`, `createTime`) values (" + votopic.getId() + ","
							+ ug.getLongitude() + "," + ug.getLatitude() + "," + ug.getRadius() + "," + votopic.getRubricId() + "," + votopic.getCreatedAt() + ");");

				} else {
					updateTopic(topic);
				}
				return topic;
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "can't create topic. " + e);
			}
		} finally {
			pm.close();
		}
	}

	@Override
	public UserOpinion likeOrDislikeMessage(long messageId, int opinion) throws InvalidOperation {
		if (opinion > 0)
			return this.<VoMessage, VoUserMessage> like(messageId, VoMessage.class, VoUserMessage.class, new VoUserMessage(), true);

		return this.<VoMessage, VoUserMessage> like(messageId, VoMessage.class, VoUserMessage.class, new VoUserMessage(), false);
	}

	@Override
	public UserOpinion likeOrDislikeTopic(long topicId, int opinion) throws InvalidOperation {
		if (opinion > 0)
			return this.<VoTopic, VoUserTopic> like(topicId, VoTopic.class, VoUserTopic.class, new VoUserTopic(), true);
		return this.<VoTopic, VoUserTopic> like(topicId, VoTopic.class, VoUserTopic.class, new VoUserTopic(), false);
	}

	/**
	 * checkUpdates запрашивает наличие обновлений с момента предыдущего запроса, который возвращает сервер в ответе, если обновлений нет - в ответ
	 * приходит новое значение таймстампа формирования ответа на сервере. При наличии обновлений возвращается 0
	 **/
	@Override
	public int checkUpdates(int lastRequest) throws InvalidOperation {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		VoSession sess = getCurrentSession(pm);
		int now = (int) (System.currentTimeMillis() / 1000L);
		if (now - sess.getLastActivityTs() > 60) { /*
																								 * Update last Activity once per minute
																								 */
			sess.setLastActivityTs(now);
			try {
				pm.makePersistent(sess);
			} finally {
				pm.close();
			}
		}
		return sess.getLastUpdateTs() > lastRequest ? 0 : now;
	}

	@Override
	public Message postMessage(Message msg) throws InvalidOperation, TException {
		long userId = getCurrentUserId();
		msg.setAuthorId(userId);
		VoMessage vomsg;
		if (0 == msg.getId()) {
			PersistenceManager pm = PMF.getPm();
			try {
				try {
					vomsg = new VoMessage(msg);
					VoTopic topic = pm.getObjectById(VoTopic.class, msg.getTopicId());
					topic.setMessageNum(topic.getMessageNum() + 1);
					topic.setLastUpdate((int) (System.currentTimeMillis() / 1000));
					pm.makePersistent(topic);
					VoUser user = getCurrentUser(pm);
					msg.userInfo = user.getShortUserInfo();

				} catch (Exception e) {
					e.printStackTrace();
					throw new InvalidOperation(VoError.IncorrectParametrs, "can't create message " + e.toString());
				}
			} finally {
				pm.close();
			}
			msg.setId(vomsg.getId());

		} else {
			updateMessage(msg);
		}
		return msg;
	}

	protected <T extends VoBaseMessage, UserT extends VoUserObject> UserOpinion like(long messageId, Class<T> tclass, Class<UserT> tUserClass,
			UserT newObject, boolean like) throws InvalidOperation {
		long userId = getCurrentUserId();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {

			T msg;
			try {
				msg = (T) pm.getObjectById(tclass, messageId);
			} catch (Exception e) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "can't find object with id " + messageId);
			}

			UserT um;
			try {
				um = (UserT) pm.getObjectById(tUserClass, VoUserObject.<UserT> createKey(tUserClass, userId, messageId));
			} catch (Exception e) {
				um = newObject;
				um.setUserId(userId);
				um.setId(messageId);
			}

			if (like)
				this.<T, UserT> incrementer(msg, um);
			else
				this.<T, UserT> decrementer(msg, um);

			pm.makePersistent(um);
			pm.makePersistent(msg);

			return new UserOpinion(msg.getLikes(), msg.getUnlikes());
		} finally {
			pm.close();
		}
	}

	protected <T extends VoBaseMessage, UserT extends VoUserObject> void incrementer(T msg, UserT um) {
		if (!um.isLikes()) {
			um.setLikes(true);
			msg.incrementLikes();
		}

		if (um.isUnlikes()) {
			um.setUnlikes(false);
			msg.decrementUnlikes();
		}
		um.setRead(true);
	}

	protected <T extends VoBaseMessage, UserT extends VoUserObject> void decrementer(T msg, UserT um) {
		if (!um.isUnlikes()) {
			um.setUnlikes(true);
			msg.incrementUnlikes();
		}
		if (um.isLikes()) {
			um.setLikes(false);
			msg.decrementLikes();
		}
		um.setRead(true);
	}

	private void initDb() throws InvalidOperation {
		con = new MySQLJDBCConnector();
		try {
			con.execute("create table if not exists topic (`id` bigint not null, `longitude` decimal(10,7) not null,"
					+ " `lattitude` decimal(10,7) not null, `radius` integer not null, `rubricId` bigint not null, `createTime` integer not null);");
		} catch (Exception e) {
			logger.severe("Failed to connect to database." + e.getMessage());
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Failed to connect to database." + e.getMessage());
		}
	}

	@Override
	public Poll doPoll(long pollId, int item) throws InvalidOperation {
		long userId = getCurrentUserId();
		PersistenceManager pm = PMF.getPm();
		try {
			VoPoll poll = pm.getObjectById(VoPoll.class, pollId);
			if (!poll.isAlreadyPoll(userId)) {
				poll.getValues().set(item, poll.getValues().get(item) + 1);
				poll.doPoll(userId);
			}
			return poll.getPoll(userId);
		} catch (Exception e) {
			logger.severe("can't do poll. " + e.getMessage());
			e.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "incorect parametr for poll");
		} finally {
			pm.close();
		}
	}

	private static MessageListPart createMlp(List<VoMessage> lst, long userId, PersistenceManager pm, int length) throws InvalidOperation {

		if (lst.size() > length)
			lst = lst.subList(0, length);

		MessageListPart mlp = new MessageListPart();
		if (lst == null) {
			logger.warning("try to create MessagePartList from null object");
			return mlp;
		}
		mlp.totalSize = lst.size();
		for (VoMessage voMessage : lst) {
			VoUserMessage voUserMsg = VoDatastoreHelper.<VoUserMessage> getUserMsg(VoUserMessage.class, userId, voMessage.getId(), pm);
			Message msg = voMessage.getMessage(pm);
			msg.userInfo = UserServiceImpl.getShortUserInfo(voMessage.getAuthorId().getId());
			msg.userMessage = null == voUserMsg ? null : voUserMsg.getUserMessage();
			mlp.addToMessages(msg);
		}
		return mlp;
	}

	private void updateMessage(Message msg) throws InvalidOperation {

		int now = (int) (System.currentTimeMillis() / 1000);
		PersistenceManager pm = PMF.getPm();
		try {
			VoMessage storedMsg = pm.getObjectById(VoMessage.class, msg.getId());
			if (null == storedMsg)
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Message not found by ID=" + msg.getId());

			VoTopic topic = pm.getObjectById(VoTopic.class, storedMsg.getTopicId());
			if (null != topic) {
				topic.updateLikes(msg.getLikesNum() - storedMsg.getLikes());
				topic.updateUnlikes(msg.getUnlikesNum() - storedMsg.getUnlikes());
			} else {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "No topic found by id=" + storedMsg.getTopicId()
						+ " that stored in Message ID=" + msg.getId());
			}

			/* Check if content changed, then update edit date */
			if (!Arrays.equals(storedMsg.getContent(), msg.getContent().getBytes())) {
				int editedAt = 0 == msg.getEdited() ? now : msg.getEdited();
				storedMsg.setEditedAt(editedAt);
				topic.setLastUpdate(editedAt);
				storedMsg.setContent(msg.getContent().getBytes());
			}

			VoUser author = pm.getObjectById(VoUser.class, storedMsg.getAuthorId());
			if (null != author) {
				author.updateLikes(msg.getLikesNum() - storedMsg.getLikes());
				author.updateUnlikes(msg.getUnlikesNum() - storedMsg.getUnlikes());
			} else {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "No AUTHOR found by id=" + storedMsg.getAuthorId()
						+ " that stored in Message ID=" + msg.getId());
			}

			if (storedMsg.getTopicId() != msg.getTopicId() || storedMsg.getAuthorId().getId() != msg.getAuthorId()
					|| storedMsg.getRecipient() != msg.getRecipientId() || storedMsg.getCreatedAt() != msg.getCreated() || storedMsg.getType() != msg.getType())
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs,
						"Parameters: topic, author, recipient, createdAt, type could not be changed!");

			storedMsg.setLikes(msg.getLikesNum());
			storedMsg.setUnlikes(msg.getUnlikesNum());
			pm.makePersistent(storedMsg);
			pm.makePersistent(topic);
			pm.makePersistent(storedMsg);
		} finally {
			pm.close();
		}
	}

	private void updateTopic(Topic topic) throws InvalidOperation {

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			VoTopic theTopic = pm.getObjectById(VoTopic.class, topic.getId());
			if (null == theTopic) {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "FAiled to update Topic. No topic found by ID" + topic.getId());
			}

			VoRubric rubric = pm.getObjectById(VoRubric.class, KeyFactory.createKey(VoRubric.class.getSimpleName(), topic.getRubricId()));
			if (null == rubric) {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Failed to move topic No Rubric found by id="
						+ topic.getRubricId());
			}
			theTopic.setLikes(topic.likesNum);
			theTopic.setUnlikes(topic.unlikesNum);
			theTopic.setUsersNum(topic.usersNum);
			theTopic.setViewers(topic.viewers);
			theTopic.setLastUpdate((int) (System.currentTimeMillis() / 1000));
			pm.makePersistent(theTopic);

		} finally {
			pm.close();
		}
	}

	protected JDBCConnector con;
	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");

	@Override
	public boolean isPublicMethod(String method) {
		return true;// publicMethods.contains(method);
	}

	// ======================================================================================================================

	@Override
	public long categoryId() {
		return ServiceCategoryID.MESSAGE_SI.ordinal();
	}

}
