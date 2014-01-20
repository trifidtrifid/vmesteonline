package com.vmesteonline.be;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import com.vmesteonline.be.MessageService.Iface;
import com.vmesteonline.be.jdo2.VoMessage;

public class MessageServiceImpl extends ServiceImpl implements Iface {

	private static Logger logger = Logger.getLogger("com.vmesteonline.be.MessageServceImpl");

	@Override
	public Message createMessage(long parentId, MessageType type, long topicId, String content, Map<MessageType, Long> linkedMessages,
			Map<Long, String> tags, long recipientId) throws InvalidOperation, TException {
		int now = (int) (System.currentTimeMillis() / 1000L);
		Message newMessage = new Message(0, parentId, type, topicId, 0, 0, now, 0, content, 0, 0, new HashMap<MessageType, Long>(),
				new HashMap<Long, String>());
		postMessage(newMessage);
		return newMessage;
	}

	@Override
	public long postMessage(Message msg) throws InvalidOperation, TException {
		long userId = getUserId();
		msg.setAuthorId(userId);
		boolean newMessage = 0==msg.getId();
		VoMessage vomsg = new VoMessage(msg,true,true);
		if( newMessage )
			newMessageNotify(vomsg);
		return vomsg.getId().getId();
	}

	private void newMessageNotify( VoMessage vomsg )  throws InvalidOperation, TException {
		// TODO notify users about new message POSTED!
	}
	@Override
	public Topic createTopic(String subject, long messageId, long rubricId, long communityId) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long postTopic(Topic topic) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkUpdates() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public GroupUpdates getUpdates() throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicListPart getTopics(long groupId, long rubricId, MessageType messageType, int commmunityId, int offset, int length)
			throws InvalidOperation, TException {

		int ss = (int) (groupId % 10);
		TopicListPart mlp = new TopicListPart(new HashSet<Topic>(), topicsaa[ss].length);
		for (int topNo = 0; topNo < length && topNo + offset < topicsaa[ss].length; topNo++) {
			mlp.addToTopics(topicsaa[ss][topNo]);
		}
		return mlp;
	}

	// STUB DATA
	// =======================================================================================================================
	static Topic topicsaa[][] = new Topic[10][]; //  rubric/ topics
	static Message msgsaaa[][][] = new Message[10][][]; // rubric/ topic /messages 
	static String longText = "GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP. GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.GPRS Tunneling Protocol (GTP) is a group of IP-based communications protocols used to carry general packet radio service (GPRS) within GSM, UMTS and LTE networks. In 3GPP architectures, GTP and Proxy Mobile IPv6 based interfaces are specified on various interface points. GTP can be decomposed into separate protocols, GTP-C, GTP-U and GTP'. GTP-C is used within the GPRS core network for signaling between gateway GPRS support nodes (GGSN) and serving GPRS support nodes (SGSN). This allows the SGSN to activate a session on a user's behalf (PDP context activation), to deactivate the same session, to adjust quality of service parameters, or to update a session for a subscriber who has just arrived from another SGSN. GTP-U is used for carrying user data within the GPRS core network and between the radio access network and the core network. The user data transported can be packets in any of IPv4, IPv6, or PPP formats. GTP' (GTP prime) uses the same message structure as GTP-C and GTP-U, but has an independent function. It can be used for carrying charging data from the charging data function (CDF) of the GSM or UMTS network to the charging gateway function (CGF). In most cases, this should mean from many individual network elements such as the GGSNs to a centralized computer that delivers the charging data more conveniently to the network operator's billing center. Different GTP variants are implemented by RNCs, SGSNs, GGSNs and CGFs within 3GPP networks. GPRS mobile stations (MSs) are connected to a SGSN without being aware of GTP. GTP can be used with UDP or TCP. UDP is either recommended or mandatory, except for tunnelling X.25 in version 0. GTP version 1 is used only on UDP.";
	static {
		int msgNo = 0;
		for (int ss = 0; ss < 10; ss++) {
			Topic[] topicsa = new Topic[(int) (Math.random() * 10)+2];
			
			topicsaa[ss] = topicsa;
			msgsaaa[ss] = new Message[topicsa.length][];
			
			
			for (int topNo = 0; topNo < topicsa.length; topNo++) {
				Message[] msgsa = new Message[(int) (Math.random() * 100)];
				msgsaaa[ss][topNo] = msgsa;
				
				boolean likes = Math.random() > 0.3, unlikes = !likes & Math.random() > 0.7;

				int pos = (int) (Math.random() * (longText.length() - 200 ));
				int len = (int) (Math.random() * (200));
				
				topicsa[topNo] = new Topic(topNo, "" + topNo + "# "
						+ longText.substring(pos, pos + len), 0L, 0,
						(int) (Math.random() * 100), 0, 0, (int) (Math.random() * 10000), (int) (Math.random() * 100000), new UserTopic(false, likes, unlikes,
								Math.random() > 0.7, (int) (Math.random() * 1000), (int) (Math.random() * 1000)));

				
				//topic message
				pos = (int) (Math.random() * (longText.length() - 1));
				len = (int) (Math.random() * (longText.length() - pos));
				int likesi = (int) (Math.random() * 100);
				int unlikesi = (int) (Math.random() * 100);
				msgsa[0] = new Message(msgNo, 0, MessageType.findByValue(1), topNo, 0, 1,
						0, 0, "" + msgNo + "# " + longText.substring(pos, pos + len), likesi, unlikesi, new HashMap<MessageType, Long>(),
						new HashMap<Long, String>());
				topicsa[topNo].setLikesNum(topicsa[topNo].getLikesNum() + likesi);
				topicsa[topNo].setUnlikesNum(topicsa[topNo].getUnlikesNum() + unlikesi);
				topicsa[topNo].setMessageNum(topicsa[topNo].getMessageNum() + 1);
				topicsa[topNo].setUsersNum(topicsa[topNo].getUnlikesNum() + (Math.random() > 0.3 ? 1 : 0));
				msgNo++;
				
				for (int no=1; no < msgsa.length; no++, msgNo++ ) {
					long parent = msgNo - (long) (Math.random() * no - 1);
					
					int pos1 = (int) (Math.random() * (longText.length() - 1));
					int len1 = (int) (Math.random() * (longText.length() - pos1 - 1));
					int likes1 = (int) (Math.random() * 100), unlikes1 = (int) (Math.random() * 100);
					msgsa[no] = new Message(msgNo, parent, MessageType.findByValue(1), topNo, 0, 1,
							0, 0, "" + msgNo + "# " + longText.substring(pos1, pos1 + len1), likes1, unlikes1, new HashMap<MessageType, Long>(),
							new HashMap<Long, String>());
					topicsa[topNo].setLikesNum(topicsa[topNo].getLikesNum() + likes1);
					topicsa[topNo].setUnlikesNum(topicsa[topNo].getUnlikesNum() + unlikes1);
					topicsa[topNo].setMessageNum(topicsa[topNo].getMessageNum() + 1);
					topicsa[topNo].setUsersNum(topicsa[topNo].getUnlikesNum() + (Math.random() > 0.3 ? 1 : 0));
				}
			}
		}
	}

	// ===================================================================================================================================
	@Override
	public MessageListPart getMessages(long topicId, long groupId, MessageType messageType, long parentId, int offset, int length)
			throws InvalidOperation, TException {
		int ss = (int)(groupId % 10);
		MessageListPart mlp = new MessageListPart( new HashSet<Message>(), msgsaaa[ss][(int) topicId].length);
		for( int msgNo=0;  msgNo<length && msgNo + offset < msgsaaa[ss][(int) topicId].length; msgNo++ ){
			mlp.addToMessages(msgsaaa[ss][(int) topicId][msgNo]);
		}
		return mlp;
	}

	@Override
	public long like(long messageId, long userId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long dislike(long messageId, long userId) throws InvalidOperation, TException {
		// TODO Auto-generated method stub
		return 0;
	}

}
