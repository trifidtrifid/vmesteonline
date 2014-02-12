package com.vmesteonline.be;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.MessageService.Iface;
import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserAttitude;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.VoUserMessage;
import com.vmesteonline.be.jdo2.VoUserObject;
import com.vmesteonline.be.jdo2.VoUserTopic;

public class MessageServiceImpl extends ServiceImpl implements Iface {

	public MessageServiceImpl() {
		con = new MySQLJDBCConnector();
		try {
			con.execute("create table if not exists topic (`id` bigint not null, `longitude` decimal(10,7) not null,"
					+ " `lattitude` decimal(10,7) not null, `radius` integer not null, `rubricId` bigint not null, `createTime` integer not null);");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public MessageServiceImpl(String sessId) {
		super(sessId);
		con = new MySQLJDBCConnector();
		try {
			con.execute("create table if not exists topic (`id` bigint not null,`longitude` decimal(10,7) not null,"
					+ " `lattitude` decimal(10,7) not null, `radius` integer not null, `rubricId` bigint not null, `createTime` integer not null);");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Message createMessage(long topicId, long parentId, long groupId, MessageType type, String content, Map<MessageType, Long> linkedMessages,
			Map<Long, String> tags, long recipientId) throws InvalidOperation, TException {

		int now = (int) (System.currentTimeMillis() / 1000L);
		Message newMessage = new Message(0, parentId, type, topicId, groupId, 0, now, 0, content, 0, 0, new HashMap<MessageType, Long>(),
				new HashMap<Long, String>(), new UserMessage(true, false, false), 0);
		postMessage(newMessage);
		return newMessage;
	}

	// ===================================================================================================================================
	@SuppressWarnings("unchecked")
	@Override
	public MessageListPart getMessages(long topicId, long groupId, MessageType messageType, long parentId, boolean archived, int offset, int length)
			throws InvalidOperation, TException {

		MessageListPart mlp = new MessageListPart();

		if (!TEST_ON_FAKE_DATA) {
			PersistenceManager pm = PMF.getPm();
			try {

				Extent<VoMessage> ext = pm.getExtent(VoMessage.class);
				for (VoMessage m : ext) {
					m.toString();
				}
				if (parentId == 0) {
					Query q = pm.newQuery(VoMessage.class);
					q.setFilter("topicId == " + topicId + " && parentId == 0");
					List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
					return createMlp(voMsgs);
				} else {
					Query q = pm.newQuery(VoMessage.class);
					q.setFilter("topicId == " + topicId);
					List<VoMessage> voMsgs = (List<VoMessage>) q.execute();
					MessagesTree tree = new MessagesTree(voMsgs);
					voMsgs = tree.getTreeMessagesAfter(parentId, length);
					return createMlp(voMsgs);
				}
			} finally {
				pm.close();
			}

		} else {
			int ss = (int) (groupId % 10);
			mlp = new MessageListPart(new ArrayList<Message>(), msgsaaa[ss][(int) topicId].length);
			for (int msgNo = 0; msgNo < length && msgNo + offset < msgsaaa[ss][(int) topicId].length; msgNo++) {
				mlp.addToMessages(msgsaaa[ss][(int) topicId][msgNo]);
			}
		}
		return mlp;
	}

	/*
	 * private void getAllChildMessages(List<VoMessage> list, VoMessage msg) { for
	 * }
	 */
	@Override
	public TopicListPart getTopics(long groupId, long rubricId, int commmunityId, long lastLoadedTopicId, int length) throws InvalidOperation,
			TException {

		TopicListPart mlp = null;

		if (!TEST_ON_FAKE_DATA) {
			mlp = new TopicListPart();
			PersistenceManager pm = PMF.get().getPersistenceManager();

			try {

				try {

					VoUser user = getCurrentUser(pm);
					pm.retrieve(user);
					VoUserGroup group = user.getGroup(groupId);
					float lgd = group.getLongitudeDelta();
					float ltd = group.getLatitudeDelta();
					String req = "select `id` from topic where rubricId = " + rubricId + " && longitude <= " + (group.getLongitude() + lgd)
							+ " and longitude >= " + (group.getLongitude() - group.getLongitudeDelta()) + " and lattitude <= " + (group.getLatitude() + ltd)
							+ " and lattitude >= " + (group.getLatitude() - group.getLatitudeDelta()) + " order by createTime";

					List<VoTopic> topics = new ArrayList<VoTopic>();
					try {
						ResultSet rs = con.executeQuery(req);
						boolean addTopic = 0 == lastLoadedTopicId ? true : false;
						while (rs.next() && topics.size() < length) {
							long topicId = rs.getLong(1);
							VoTopic topic = pm.getObjectById(VoTopic.class, topicId);
							if (addTopic) {
								topics.add(topic);
							} else {
								if (topic.getId().getId() == lastLoadedTopicId) {
									addTopic = true;
								}
							}
						}
					} finally {
						con.close();
					}

					if (topics.isEmpty()) {
						logger.debug("can't find any topics");
						return mlp;
					}
					mlp.totalSize = topics.size();
					for (VoTopic voTopic : topics) {
						mlp.addToTopics(voTopic.getTopic());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			} finally {
				pm.close();
			}
			return mlp;

		} else {
			int ss = (int) (groupId % 10);
			mlp = new TopicListPart(new ArrayList<Topic>(), topicsaa[ss].length);
			for (int topNo = 0; topNo < length && topNo < topicsaa[ss].length; topNo++) {
				mlp.addToTopics(topicsaa[ss][topNo]);
			}
		}
		return mlp;
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
	public long postTopic(Topic topic) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			try {
				if (0 == topic.getId()) {
					VoTopic votopic = new VoTopic(topic, true, true, true);
					pm.makePersistent(votopic);
					topic.setId(votopic.getId().getId());
					VoUser user = getCurrentUser(pm);
					VoUserGroup ug = user.getGroup(votopic.getUserGroupId());
					con.execute("insert into topic (`id`, `longitude`, `lattitude`, `radius`, `rubricId`, `createTime`) values (" + votopic.getId().getId()
							+ "," + ug.getLongitude() + "," + ug.getLatitude() + "," + ug.getRadius() + "," + votopic.getRubricId() + "," + votopic.getCreatedAt()
							+ ");");
					newTopicNotify(votopic);
				} else {
					updateTopic(topic);
				}
				return topic.getId();
			} catch (Exception e) {
				e.printStackTrace();
				throw new InvalidOperation(VoError.GeneralError, "can't create topic. " + e);
			}
		} finally {
			pm.close();
		}
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

	protected void testMthd(long i, long d){
		
	}
	
	@Override
	public long dislike(long messageId) throws InvalidOperation, TException {
		long unlikesNum = 0;
		long user = getCurrentUserId();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			this.getClass().getMethod("testMthd").invoke(1, 43);
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			VoMessage msg = pm.getObjectById(VoMessage.class, messageId);
			VoUserMessage um = pm.getObjectById(VoUserMessage.class, VoUserMessage.getObjectKey(user, messageId));
			if (null == um) {
				um = new VoUserMessage(user, messageId);
				um.setLikes(false);
				um.setUnlikes(true);
				um.setRead(true);
				um.setUserId(user);
				um.setId(messageId);
			} else {
				if (!um.isUnlikes()) { // unlike already set
					if (um.isLikes()) {
						msg.decrementLikes();
					}
					um.setLikes(false);
				}
			}
			unlikesNum = msg.incrementUnlikes();
			pm.makePersistent(um);
			pm.makePersistent(msg);
			try {
				currentTransaction.commit();
			} catch (Exception e) {
				currentTransaction.rollback();
				throw new InvalidOperation(VoError.GeneralError, "Failed to change dislike. Transaction not commited. Reason is [" + e.getMessage()
						+ "]. Rollbacked.");
			}
			return unlikesNum;
		} finally {
			pm.close();
		}
	}

	@Override
	public long like(long messageId) throws InvalidOperation, TException {
		return this.<VoMessage, VoUserMessage> like(messageId, VoMessage.class, VoUserMessage.class, new VoUserMessage());
	}

	@Override
	public long markTopicUnintrested(long topicId, boolean interested) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long likeTopic(long topicId) throws InvalidOperation, TException {
		return this.<VoTopic, VoUserTopic> like(topicId, VoTopic.class, VoUserTopic.class, new VoUserTopic());
	}

	@Override
	public long dislikeTopic(long topicId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Topic createTopic(long groupId, String subject, MessageType type, String content, Map<MessageType, Long> linkedMessages,
			Map<Long, String> tags, long rubricId, long communityId) throws TException {

		int now = (int) (System.currentTimeMillis() / 1000L);
		Message msg = new Message(0, 0, type, 0, groupId, getCurrentUserId(), now, 0, content, 0, 0, new HashMap<MessageType, Long>(),
				new HashMap<Long, String>(), new UserMessage(true, false, false), 0);
		Topic topic = new Topic(0, subject, msg, 0, 0, 0, now, 0, 0, new UserTopic());
		topic.setRubricId(rubricId);
		postTopic(topic);
		return topic;
	}

	/**
	 * checkUpdates запрашивает наличие обновлений с момента предыдущего запроса,
	 * который возвращает сервер в ответе, если обновлений нет - в ответ приходит
	 * новое значение таймстампа формирования ответа на сервере. При наличии
	 * обновлений возвращается 0
	 **/
	@Override
	public int checkUpdates(int lastRequest) throws InvalidOperation {
		VoSession sess = getCurrentSession();
		int now = (int) (System.currentTimeMillis() / 1000L);
		if (now - sess.getLastActivityTs() > 60) { /*
																								 * Update last Activity once per
																								 * minute
																								 */
			sess.setLastActivityTs(now);
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(sess);
			} finally {
				pm.close();
			}
		}
		return sess.getLastUpdateTs() > lastRequest ? 0 : now;
	}

	@Override
	public GroupUpdates getUpdates() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long postMessage(Message msg) throws InvalidOperation, TException {
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

				} catch (Exception e) {
					e.printStackTrace();
					throw new InvalidOperation(VoError.IncorrectParametrs, "can't create message " + e.toString());
				}
			} finally {
				pm.close();
			}
			newMessageNotify(vomsg);
			msg.setId(vomsg.getId().getId());
		} else {
			updateMessage(msg);
		}
		return msg.getId();
	}

	protected <T extends VoUserAttitude, UserT extends VoUserObject> long like(long messageId, Class<T> tclass, Class<UserT> tUserClass, UserT newObject)
			throws InvalidOperation {
		long likesNum = 0;
		long userId = getCurrentUserId();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction currentTransaction = pm.currentTransaction();
		try {
	
			T msg = (T) pm.getObjectById(tclass, messageId);
			UserT um = (UserT) pm.getObjectById(tUserClass, VoUserMessage.getObjectKey(userId, messageId));
			if (null == um) {
				um = newObject;
				um.setLikes(true);
				um.setUnlikes(false);
				um.setRead(true);
				um.setId(messageId);
				um.setUserId(userId);
			} else {
				if (!um.isLikes()) { // unlike already set
					if (um.isUnlikes()) {
						msg.decrementUnlikes();
					}
					um.setUnlikes(false);
				}
			}
			likesNum = msg.incrementLikes();
			pm.makePersistent(um);
			pm.makePersistent(msg);
			try {
				currentTransaction.commit();
			} catch (Exception e) {
				currentTransaction.rollback();
				throw new InvalidOperation(VoError.GeneralError, "Failed to change like for message " + messageId + " by user " + userId
						+ ". Transaction not commited. Reason is [" + e.getMessage() + "]. Rollbacked.");
			}
			return likesNum;
		} finally {
			pm.close();
		}
	}

	private static MessageListPart createMlp(List<VoMessage> lst) {
		MessageListPart mlp = new MessageListPart();
		if (lst == null) {
			logger.warn("try to create MessagePartList from null object");
			return mlp;
		}
		mlp.totalSize = lst.size();
		for (VoMessage voMessage : lst) {
			mlp.addToMessages(voMessage.getMessage());
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

	private void newMessageNotify(VoMessage vomsg) throws InvalidOperation, TException {
		// TODO notify users about new message POSTED!
	}

	private void newTopicNotify(VoTopic votopic) {
		/* TODO Implement user notification */
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

	private static boolean TEST_ON_FAKE_DATA = false;
	// STUB DATA
	// =======================================================================================================================
	static Topic topicsaa[][] = new Topic[10][]; // rubric/ topics
	static Message msgsaaa[][][] = new Message[10][][]; // rubric/ topic /messages
	static String longText = "GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP. GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.";
	static {
		if (TEST_ON_FAKE_DATA) {
			int msgNo = 0;
			for (int ss = 0; ss < 10; ss++) {
				Topic[] topicsa = new Topic[(int) (Math.random() * 10) + 2];

				topicsaa[ss] = topicsa;
				msgsaaa[ss] = new Message[topicsa.length][];

				for (int topNo = 0; topNo < topicsa.length; topNo++) {
					Message[] msgsa = new Message[(int) (Math.random() * 100) + 1];
					msgsaaa[ss][topNo] = msgsa;

					boolean likes = Math.random() > 0.3, unlikes = !likes & Math.random() > 0.7;

					int pos = (int) (Math.random() * (longText.length() - 200));
					int len = (int) (Math.random() * (200));

					// topic message
					pos = (int) (Math.random() * (longText.length() - 1));
					len = (int) (Math.random() * (longText.length() - pos));
					int likesi = (int) (Math.random() * 100);
					int unlikesi = (int) (Math.random() * 100);
					msgsa[0] = new Message(msgNo, 0, MessageType.findByValue(1), topNo, 0, 1, 0, 0, "" + msgNo + "# " + longText.substring(pos, pos + len),
							likesi, unlikesi, new HashMap<MessageType, Long>(), new HashMap<Long, String>(), new UserMessage(Math.random() > 0.5,
									Math.random() > 0.5, Math.random() > 0.5), 0);

					msgNo++;

					topicsa[topNo] = new Topic(topNo, "" + topNo + "# " + longText.substring(pos, pos + len), msgsa[0], 0, (int) (Math.random() * 100), 0, 0,
							(int) (Math.random() * 10000), (int) (Math.random() * 100000), new UserTopic(false, likes, unlikes, Math.random() > 0.7,
									(int) (Math.random() * 1000), (int) (Math.random() * 1000)));

					topicsa[topNo].setLikesNum(topicsa[topNo].getLikesNum() + likesi);
					topicsa[topNo].setUnlikesNum(topicsa[topNo].getUnlikesNum() + unlikesi);
					topicsa[topNo].setMessageNum(topicsa[topNo].getMessageNum() + 1);
					topicsa[topNo].setUsersNum(topicsa[topNo].getUnlikesNum() + (Math.random() > 0.3 ? 1 : 0));

					for (int no = 1; no < msgsa.length; no++, msgNo++) {
						long parent = msgNo - (long) (Math.random() * no - 1);

						int pos1 = (int) (Math.random() * (longText.length() - 1));
						int len1 = (int) (Math.random() * (longText.length() - pos1 - 1));
						int likes1 = (int) (Math.random() * 100), unlikes1 = (int) (Math.random() * 100);
						msgsa[no] = new Message(msgNo, parent, MessageType.findByValue(1), topNo, 0, 1, 0, 0, "" + msgNo + "# "
								+ longText.substring(pos1, pos1 + len1), likes1, unlikes1, new HashMap<MessageType, Long>(), new HashMap<Long, String>(),
								new UserMessage(Math.random() > 0.5, Math.random() > 0.5, Math.random() > 0.5), 0);
						topicsa[topNo].setLikesNum(topicsa[topNo].getLikesNum() + likes1);
						topicsa[topNo].setUnlikesNum(topicsa[topNo].getUnlikesNum() + unlikes1);
						topicsa[topNo].setMessageNum(topicsa[topNo].getMessageNum() + 1);
						topicsa[topNo].setUsersNum(topicsa[topNo].getUnlikesNum() + (Math.random() > 0.3 ? 1 : 0));
					}
				}
			}
		}
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");

}
