package com.vmesteonline.be;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.MessageService.Iface;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoRubric;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserMessage;

public class MessageServiceImpl extends ServiceImpl implements Iface {

	public MessageServiceImpl() {
	}

	public MessageServiceImpl(String sessId) {
		super(sessId);
	}

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");

	@Override
	public Message createMessage(long parentId, long groupId, MessageType type, String content, Map<MessageType, Long> linkedMessages,
			Map<Long, String> tags, long recipientId) throws InvalidOperation, TException {

		int now = (int) (System.currentTimeMillis() / 1000L);
		Message newMessage = new Message(0, parentId, type, 0, groupId, 0, now, 0, content, 0, 0, new HashMap<MessageType, Long>(),
				new HashMap<Long, String>(), new UserMessage(true, false, false));
		postMessage(newMessage);
		return newMessage;
	}

	@Override
	public long postMessage(Message msg) throws InvalidOperation, TException {
		long userId = getCurrentUserId();
		msg.setAuthorId(userId);
		boolean newMessage = 0 >= msg.getId();
		VoMessage vomsg;
		if (newMessage) {
			vomsg = new VoMessage(msg);
			newMessageNotify(vomsg);
			msg.setId(vomsg.getId().getId());
		} else {
			updateMessage(msg);
		}
		return msg.getId();
	}

	private void updateMessage(Message msg) throws InvalidOperation {

		int now = (int) (System.currentTimeMillis() / 1000);
		PersistenceManager pm = PMF.getPm();
		try {
			VoMessage storedMsg = pm.getObjectById(VoMessage.class, msg.getId());
			if (null == storedMsg)
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Message not found by ID=" + msg.getId());

			VoTopic topic = storedMsg.getTopic();
			if (null != topic) {
				topic.updateLikes(msg.getLikesNum() - storedMsg.getLikes());
				topic.updateUnlikes(msg.getUnlikesNum() - storedMsg.getUnlikes());
			} else {
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "No topic found by id=" + storedMsg.getTopic().getId()
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

			if (storedMsg.getTopic().getId().getId() != msg.getTopicId() || storedMsg.getAuthorId().getId() != msg.getAuthorId()
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

	@Override
	public Topic createTopic(long groupId, String subject, MessageType type, String content, Map<MessageType, Long> linkedMessages,
			Map<Long, String> tags, long rubricId, long communityId) throws TException {

		int now = (int) (System.currentTimeMillis() / 1000L);
		Message msg = new Message(0, 0, type, 0, groupId, 0, now, 0, content, 0, 0, new HashMap<MessageType, Long>(), new HashMap<Long, String>(),
				new UserMessage(true, false, false));
		Topic topic = new Topic(0, subject, msg, 0, 0, 0, now, 0, 0, new UserTopic());
		topic.setRubricId(rubricId);
		postTopic(topic);
		return topic;
	}

	@Override
	public long postTopic(Topic topic) throws InvalidOperation {
		long userId = getCurrentUserId();
		topic.getMessage().setAuthorId(userId);
		boolean newTopic = 0 >= topic.getId();
		if (newTopic) {
			VoTopic votopic = new VoTopic(topic, true, true, true);
			newTopicNotify(votopic);
		} else {
			updateTopic(topic);
		}
		return topic.getId();
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
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Failed to move topic No Rubric found by id=" + topic.getRubricId());
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
	public TopicListPart getTopics(long groupId, long rubricId, MessageType messageType, int commmunityId, int offset, int length)
			throws InvalidOperation, TException {

		TopicListPart mlp = null;

		if (!TEST_ON_FAKE_DATA) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {

			} finally {
				pm.close();
			}
		} else {
			int ss = (int) (groupId % 10);
			mlp = new TopicListPart(new HashSet<Topic>(), topicsaa[ss].length);
			for (int topNo = 0; topNo < length && topNo + offset < topicsaa[ss].length; topNo++) {
				mlp.addToTopics(topicsaa[ss][topNo]);
			}
		}
		return mlp;
	}

	private static boolean TEST_ON_FAKE_DATA = true;
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
					Message[] msgsa = new Message[(int) (Math.random() * 100)+1];
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
									Math.random() > 0.5, Math.random() > 0.5));

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
								new UserMessage(Math.random() > 0.5, Math.random() > 0.5, Math.random() > 0.5));
						topicsa[topNo].setLikesNum(topicsa[topNo].getLikesNum() + likes1);
						topicsa[topNo].setUnlikesNum(topicsa[topNo].getUnlikesNum() + unlikes1);
						topicsa[topNo].setMessageNum(topicsa[topNo].getMessageNum() + 1);
						topicsa[topNo].setUsersNum(topicsa[topNo].getUnlikesNum() + (Math.random() > 0.3 ? 1 : 0));
					}
				}
			}
		}
	}

	// ===================================================================================================================================
	@Override
	public MessageListPart getMessages(long topicId, long groupId, MessageType messageType, long parentId, boolean archived, int offset, int length)
			throws InvalidOperation, TException {

		MessageListPart mlp = null;

		if (!TEST_ON_FAKE_DATA) {

		} else {
			int ss = (int) (groupId % 10);
			mlp = new MessageListPart(new HashSet<Message>(), msgsaaa[ss][(int) topicId].length);
			for (int msgNo = 0; msgNo < length && msgNo + offset < msgsaaa[ss][(int) topicId].length; msgNo++) {
				mlp.addToMessages(msgsaaa[ss][(int) topicId][msgNo]);
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
	public long dislike(long messageId) throws InvalidOperation, TException {
		long unlikesNum = 0;
		long user = getCurrentUserId();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			VoMessage msg = pm.getObjectById(VoMessage.class, messageId);
			VoUserMessage um = pm.getObjectById(VoUserMessage.class, VoUserMessage.getObjectKey(user, messageId));
			if (null == um) {
				um = new VoUserMessage(user, messageId);
				um.setLikes(false);
				um.setUnlikes(true);
				um.setRead(true);
				um.setUserId(user);
				um.setMessage(messageId);
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
		long likesNum = 0;
		long user = getCurrentUserId();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Transaction currentTransaction = pm.currentTransaction();
		try {
			VoMessage msg = pm.getObjectById(VoMessage.class, messageId);
			VoUserMessage um = pm.getObjectById(VoUserMessage.class, VoUserMessage.getObjectKey(user, messageId));
			if (null == um) {
				um = new VoUserMessage(user, messageId);
				um.setLikes(true);
				um.setUnlikes(false);
				um.setRead(true);
				um.setUserId(user);
				um.setMessage(messageId);
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
				throw new InvalidOperation(VoError.GeneralError, "Failed to change like for message " + messageId + " by user " + user
						+ ". Transaction not commited. Reason is [" + e.getMessage() + "]. Rollbacked.");
			}
			return likesNum;
		} finally {
			pm.close();
		}
	}

	@Override
	public long markTopicUnintrested(long topicId, boolean interested) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long likeTopic(long topicId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long dislikeTopic(long topicId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

}
