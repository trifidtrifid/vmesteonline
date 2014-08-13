package com.vmesteonline.be;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.data.VoDatastoreHelper;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoPoll;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.VoUserTopic;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageListPart;
import com.vmesteonline.be.messageservice.MessageService.Iface;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.messageservice.Poll;
import com.vmesteonline.be.messageservice.Topic;
import com.vmesteonline.be.messageservice.TopicListPart;
import com.vmesteonline.be.messageservice.WallItem;
import com.vmesteonline.be.utils.EMailHelper;
import com.vmesteonline.be.utils.StorageHelper;
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
	public void sendInfoEmail(String email, String name, String content) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();

		try {
			String subj = "Contacts: ";
			try {
				VoUser voUser = getCurrentUser(pm);
				subj += "registered user " + voUser.getName() + " " + voUser.getLastName() + " " + voUser.getContacts();
			} catch (Exception e) {
				if (email == null || name == null || email.length() == 0 || name.length() == 0)
					throw new InvalidOperation(VoError.IncorrectParametrs, "email and name can't be empty or null");
				subj += "unregistered user " + name + " " + email;
			}
			EMailHelper.sendSimpleEMail("trifid@gmail.com", subj, content);
		} catch (Exception e) {
			e.printStackTrace();
			logger.warning("warning when try to send email from contacts. user " + name + " email " + email + " content " + content);
		} finally {
			pm.close();
		}

	}


	@Override
	public List<WallItem> getWallItems(long groupId, long lastLoadedIdTopicId, int length) throws InvalidOperation, TException {
		List<WallItem> wallItems = new ArrayList<WallItem>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			try {
				VoUser user = getCurrentUser(pm);
				pm.retrieve(user);
				VoUserGroup group = user.getGroupById(groupId);
				// todo add last loaded and length
				List<VoTopic> topics = getTopics(group, MessageType.WALL, lastLoadedIdTopicId, length, false, pm);

				if (topics.isEmpty()) {
					logger.fine("can't find any topics");
					return wallItems;
				}
				for (VoTopic voTopic : topics) {
					Topic tpc = voTopic.getTopic(user.getId(), pm);

					tpc.userInfo = UserServiceImpl.getShortUserInfo(voTopic.getAuthorId().getId());

					MessageListPart mlp = getMessagesAsList(tpc.id, MessageType.BASE, 0, false, 10000);
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
	public MessageListPart getMessagesAsList(long topicId, MessageType messageType, long lastLoadedId, boolean archived, int length)
			throws InvalidOperation {
		long userId = 0;
		if (messageType != MessageType.BLOG)
			userId = getCurrentUserId();

		PersistenceManager pm = PMF.getPm();
		try {

			Query q = pm.newQuery(VoMessage.class);
			q.setFilter("topicId == " + topicId);
			List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
			Collections.sort(voMsgs, new VoMessage.ComparatorByCreateDate());

			if (lastLoadedId != 0) {
				List<VoMessage> subLst = null;
				for (int i = 0; i < voMsgs.size() - 1; i++) {
					if (voMsgs.get(i).getId() == lastLoadedId)
						subLst = voMsgs.subList(i + 1, voMsgs.size());
				}
				voMsgs = (subLst == null) ? new ArrayList<VoMessage>() : subLst;
			}
			return createMlp(voMsgs, userId, pm, length);
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
	
	// ===================================================================================================================================
	private static String mlpKeyPrefix = "MessageListPartByGroupAndTopic";
	@Override
	public MessageListPart getMessages(long topicId, long groupId, MessageType messageType, long lastLoadedMsgId, boolean archived, int length)
			throws InvalidOperation, TException {

		String key = mlpKeyPrefix+":"+topicId+":"+groupId+":"+messageType+":"+lastLoadedMsgId+":"+archived+":"+length;
		
		PersistenceManager pm = PMF.getPm();
		try {
			int lastUpdate = pm.getObjectById(VoTopic.class, topicId).getLastUpdate();
			
			Object objectFromCache = getObjectFromCache(key);
			if( null!=objectFromCache && objectFromCache instanceof VoHelper.CacheObjectUnit<?> 
				&& ((VoHelper.CacheObjectUnit<?>)objectFromCache).timestamp == lastUpdate )
				return ((VoHelper.CacheObjectUnit<MessageListPart>)objectFromCache).object;
			
			VoUser user = getCurrentUser(pm);
			MessagesTree tree = MessagesTree.createMessageTree(topicId, pm);
			List<VoMessage> voMsgs = tree.getTreeMessagesAfter(lastLoadedMsgId, new MessagesTree.Filters(user.getId(), user.getGroupById(groupId)));
			MessageListPart mlp = createMlp(voMsgs, user.getId(), pm, length);
			putObjectToCache(key, new VoHelper.CacheObjectUnit<MessageListPart>(lastUpdate,mlp));
			return mlp;
		} finally {
			pm.close();
		}

	}

	public List<VoTopic> getTopics(VoUserGroup group, MessageType type, long lastLoadedTopicId, int length, boolean importantOnly, PersistenceManager pm) {
		return getTopics(group, type, lastLoadedTopicId, length, importantOnly, con, pm);
	}

	public static List<VoTopic> getTopics(VoUserGroup group, MessageType type, long lastLoadedTopicId, int length, boolean importantOnly,
			JDBCConnector con, PersistenceManager pm) {

		String req = "select `id` from topic where";
		if (group != null)
			req += " radius >= " + group.getRadius() + " and longitude <= "
					+ VoHelper.getLongitudeMax(group.getLongitude(), group.getLatitude(), group.getRadius()).toPlainString() + " and longitude >= "
					+ VoHelper.getLongitudeMin(group.getLongitude(), group.getLatitude(), group.getRadius()).toPlainString() + " and lattitude <= "
					+ VoHelper.getLatitudeMax(group.getLatitude(), group.getRadius()).toPlainString() + " and lattitude >= "
					+ VoHelper.getLatitudeMin(group.getLatitude(), group.getRadius()).toPlainString();
		else if (type != MessageType.BLOG)
			return null;

		switch (type) {

		case BASE:
			req += " and messageType = " + Integer.toString(MessageType.BASE.getValue());
			break;
		case ADVERT:
			req += " and messageType = " + Integer.toString(MessageType.ADVERT.getValue());
			break;
		case WALL:
			req += " and messageType != " + Integer.toString(MessageType.ADVERT.getValue()) + " and messageType != "
					+ Integer.toString(MessageType.BLOG.getValue());
			break;
		case BLOG:
			req += " messageType = " + Integer.toString(MessageType.BLOG.getValue());
			break;

		default:
			break;

		}

		req += " order by createTime desc";

		List<VoTopic> topics = new ArrayList<VoTopic>();
		try {
			ResultSet rs = con.executeQuery(req);

			boolean addTopic = 0 == lastLoadedTopicId ? true : false;
			while (rs.next() && topics.size() < length) {
				long topicId = rs.getLong(1);
				VoTopic topic = pm.getObjectById(VoTopic.class, topicId);

				boolean isImportant = false;
				if (group != null) {
					isImportant = group.getImportantScore() <= topic.getImportantScore();
					topic.setImportant(isImportant);
				}
				if (addTopic && (!importantOnly || isImportant)) {
					topics.add(topic);
					long topicAge = (System.currentTimeMillis() / 1000L) - topic.getLastUpdate();
					if (importantOnly && (topics.size() > 4 || topicAge > 86400 * 7)) // в запросе важных возвращается не более 5 и не старше недели
						break;
				} else if (topic.getId() == lastLoadedTopicId) {
					addTopic = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.close();
		}
		return topics;
	}

	@Override
	public TopicListPart getBlog(long lastLoadedTopicId, int length) throws InvalidOperation {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<VoTopic> topics = getTopics(null, MessageType.BLOG, lastLoadedTopicId, length, false, pm);
		TopicListPart mlp = new TopicListPart();
		mlp.totalSize = topics.size();

		for (VoTopic voTopic : topics) {
			Topic tpc = voTopic.getTopic(0, pm);
			mlp.addToTopics(tpc);
		}
		return mlp;

	}

	@Override
	public TopicListPart getTopics(long groupId, long rubricId, int commmunityId, long lastLoadedTopicId, int length) throws InvalidOperation {
		return getTopics(groupId, rubricId, commmunityId, lastLoadedTopicId, length, MessageType.BASE, false);
	}

	@Override
	public TopicListPart getImportantTopics(long groupId, long rubricId, int commmunityId, int length) throws InvalidOperation {
		return getTopics(groupId, rubricId, commmunityId, 0, 1000, MessageType.WALL, true);
	}

	@Override
	public TopicListPart getAdverts(long groupId, long lastLoadedTopicId, int length) throws InvalidOperation {
		return getTopics(groupId, 0, 0, lastLoadedTopicId, length, MessageType.ADVERT, false);
	}

	@SuppressWarnings("unchecked")
	private TopicListPart getTopics(long groupId, long rubricId, int commmunityId, long lastLoadedTopicId, int length, MessageType type,
			boolean importantOnly) {

		TopicListPart mlp = new TopicListPart();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {

			try {
				VoUser user = getCurrentUser(pm);
				pm.retrieve(user);
				VoUserGroup group = user.getGroupById(groupId);
				List<VoTopic> topics = getTopics(group, type, lastLoadedTopicId, length, importantOnly, pm);

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
					con.execute("insert into topic (`id`, `longitude`, `lattitude`, `radius`, `rubricId`, `createTime`, `messageType`) values ("
							+ votopic.getId() + "," + ug.getLongitude() + "," + ug.getLatitude() + "," + ug.getRadius() + "," + votopic.getRubricId() + ","
							+ votopic.getCreatedAt() + "," + votopic.getType().getValue() + ");");

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
	public Message postBlogMessage(Message msg) throws InvalidOperation {
		if( 0!=msg.getId() ){
			updateMessage(msg);
			return msg;
		}
		PersistenceManager pm = PMF.getPm();
		try {
			try {

				VoUser voUser = getCurrentUser();
				msg.anonName += voUser.getName() + " " + voUser.getLastName();
				msg.userInfo = voUser.getShortUserInfo();
				msg.authorId = voUser.getId();
			} catch (InvalidOperation e) {
				if (msg.getAnonName() == null || msg.getAnonName().isEmpty())
					throw new InvalidOperation(VoError.IncorrectParametrs, "has no user name");
			}

			VoMessage vomsg = new VoMessage(msg, MessageType.BLOG);
			pm.makePersistent(vomsg);
			msg.setId(vomsg.getId());
			return msg;

		} finally {
			pm.close();
		}

	}

	@Override
	public Message postMessage(Message msg) throws InvalidOperation {
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

					if (msg.type != MessageType.BLOG)
						msg.userInfo = getCurrentUser(pm).getShortUserInfo();

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

	private void initDb() throws InvalidOperation {
		con = new MySQLJDBCConnector();
		try {
			con.execute("create table if not exists topic (`id` bigint not null, `longitude` decimal(10,7) not null,"
					+ " `lattitude` decimal(10,7) not null, `radius` integer not null, `rubricId` bigint not null, `createTime` integer not null, `messageType` integer not null);");
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
			Message msg = voMessage.getMessage(userId, pm);
			if (voMessage.getAuthorId() != null)
				msg.userInfo = UserServiceImpl.getShortUserInfo(voMessage.getAuthorId().getId());
			mlp.addToMessages(msg);
		}
		return mlp;
	}

	private void updateMessage(Message msg) throws InvalidOperation {

		int now = (int) (System.currentTimeMillis() / 1000);
		PersistenceManager pm = PMF.getPm();
		try {
			VoMessage storedMsg = pm.getObjectById(VoMessage.class, msg.getId());
			
			if (storedMsg.getAuthorId().getId() != getCurrentUserId(pm) )
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "User is not author of message");

			VoTopic topic = pm.getObjectById(VoTopic.class, storedMsg.getTopicId());

			/* Check if content changed, then update edit date */
			if (!storedMsg.getContent().equals( msg.getContent())) {
				//int editedAt = 0 == msg.getEdited() ? now : msg.getEdited();
				storedMsg.setEditedAt(now);
				topic.setLastUpdate(now);
				storedMsg.setContent(msg.getContent());
			}

			if (storedMsg.getTopicId() != msg.getTopicId() || storedMsg.getAuthorId().getId() != msg.getAuthorId()
					|| storedMsg.getRecipient() != msg.getRecipientId() || storedMsg.getCreatedAt() != msg.getCreated() || storedMsg.getType() != msg.getType())
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs,
						"Parameters: topic, author, recipient, createdAt, type could not be changed!");

			pm.makePersistent(storedMsg);
			pm.makePersistent(topic);
			pm.makePersistent(storedMsg);
		} catch( JDOObjectNotFoundException onfe ){
			throw new InvalidOperation(VoError.IncorrectParametrs,
					"Message not found");

		} finally {
			pm.close();
		}
	}

	private void updateTopic(Topic topic) throws InvalidOperation {

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			VoTopic theTopic;
			try {
				theTopic = pm.getObjectById(VoTopic.class, topic.getId());
			} catch (Exception e1) {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "FAiled to update Topic. No topic found by ID" + topic.getId());
			}

			try {
				pm.getObjectById(VoRubric.class, KeyFactory.createKey(VoRubric.class.getSimpleName(), topic.getRubricId()));
			} catch (Exception e) {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Failed to move topic No Rubric found by id="
						+ topic.getRubricId());
			}
			updateMessage( topic.getMessage() );
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

	// ======================================================================================================================

	@Override
	public int markMessageImportant(long messageId, boolean isImportant) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoTopic msg = pm.getObjectById(VoTopic.class, messageId);
			VoUser author = null == msg.getAuthorId() ? null : pm.getObjectById(VoUser.class, msg.getAuthorId());
			int impScore = msg.markImportant(getCurrentUser(), author, isImportant, pm);

			// time to send notification if not sent?
			if (isImportant && 0 == msg.getImportantNotificationSentDate()) {
				VoUserGroup topicGroup = pm.getObjectById(VoUserGroup.class, msg.getUserGroupId());
				if (impScore >= topicGroup.getImportantScore()) {
					
					Queue queue = QueueFactory.getDefaultQueue();
		      queue.add(withUrl("/tasks/notification").param("rt", "mbi")
		      		.param("it", ""+msg.getId())
		      		.param("ug", ""+topicGroup.getId()));
					
					//Notification.messageBecomeImportantNotification(msg, topicGroup);
					msg.setImportantNotificationSentDate((int) (System.currentTimeMillis() / 1000L));
				}
			}
			return impScore;
		} catch(JDOObjectNotFoundException onfe){
			throw new InvalidOperation(VoError.IncorrectParametrs, "No message found by ID:"+messageId);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public int markMessageLike(long messageId) throws InvalidOperation{
		PersistenceManager pm = PMF.getPm();
		try {
			VoTopic msg = pm.getObjectById(VoTopic.class, messageId);
			if( msg.getAuthorId() == null || msg.getAuthorId().getId() == getCurrentUserId())
				return msg.getPopularityScore();
			
			VoUser author = null == msg.getAuthorId() ? null : pm.getObjectById(VoUser.class, msg.getAuthorId());
			return msg.markLikes(getCurrentUser(), author, pm);
		} catch(JDOObjectNotFoundException onfe){
			throw new InvalidOperation(VoError.IncorrectParametrs, "No message found by ID:"+messageId);
		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	@Override
	public Message deleteMessage(long msgId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			VoMessage msg = pm.getObjectById(VoMessage.class, msgId);
			long cuId = getCurrentUserId();
			if( msg.getAuthorId().getId() != cuId )
				throw new InvalidOperation(VoError.IncorrectParametrs, "USer is not the author");
			
			long topicId = msg.getTopicId();
			VoTopic topic = pm.getObjectById(VoTopic.class, topicId);
			topic.setMessageNum( topic.getMessageNum() - 1 );
			topic.setChildMessageNum( topic.getChildMessageNum() - 1);
			
			
			deleteAttachments(pm, msg.getImages());
			deleteAttachments(pm, msg.getDocuments());
			
			//check if message can be deleted
			List<VoMessage> msgsOfTopic = (List<VoMessage>) pm.newQuery(VoMessage.class,"topicId=="+topicId ).execute();
			boolean canDelete = true;
			for( VoMessage msgot : msgsOfTopic){
				if( msgot.getParentId() == msgId){
					canDelete = false;
					break;
				}
			}
			if(canDelete){
				pm.deletePersistent(msg);
				return null;
			} else {
				msg.setContent("Сообщение удалено пользователем.");
				return msg.getMessage(cuId, pm);
			}
			
		} catch( JDOObjectNotFoundException onfe ){
			throw new InvalidOperation(VoError.IncorrectParametrs,
					"Message not found");

		} finally {
			pm.close();
		}
	}

	// ======================================================================================================================

	private void deleteAttachments(PersistenceManager pm, List<Long> imgs) {
		for( Long attachId: imgs ){
			try {
				VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, attachId);
				StorageHelper.deleteImage(att.getGSFileName());
				pm.deletePersistent(att);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// ======================================================================================================================

	@Override
	public Topic deleteTopic(long topicId) throws InvalidOperation, TException {
		PersistenceManager pm = PMF.getPm();
		try {
			VoTopic tpc = pm.getObjectById(VoTopic.class, topicId);
			long cu = getCurrentUserId();
			if( tpc.getAuthorId().getId() != cu )
				throw new InvalidOperation(VoError.IncorrectParametrs, "USer is not the author");
			
			deleteAttachments(pm, tpc.getImages());
			deleteAttachments(pm, tpc.getDocuments());
			
			if(0==tpc.getMessageNum()){
				pm.deletePersistent(tpc);
				return null;
			} else {
				tpc.setContent("Тема удалена пользователем.");
				return tpc.getTopic(cu, pm);
			}
			
		} catch( JDOObjectNotFoundException onfe ){
			throw new InvalidOperation(VoError.IncorrectParametrs,
					"Topic not found");

		} finally {
			pm.close();
		}
	}
}
