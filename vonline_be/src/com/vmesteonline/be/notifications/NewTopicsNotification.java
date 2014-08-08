package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.MessageServiceImpl;
import com.vmesteonline.be.NotificationFreq;
import com.vmesteonline.be.data.JDBCConnector;
import com.vmesteonline.be.data.MySQLJDBCConnector;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.messageservice.MessageType;
import com.vmesteonline.be.notifications.Notification.NotificationMessage;

public class NewTopicsNotification extends Notification {

	private JDBCConnector con = new MySQLJDBCConnector();
	
	public NewTopicsNotification( Map< VoUser, List<NotificationMessage>> ntf ) {
		this.messagesToSend = ntf;
	}

	public void makeNotification( Set<VoUser> users ) {

		int now = (int)(System.currentTimeMillis()/1000L);

		PersistenceManager pm = PMF.getPm();
		try {
			Map<VoUserGroup, Set<VoTopic>> groupTopicMap = collectTopicsByGroups(users, pm);

			// create message for each user
			String body = "Близкие события\n\n";
			
			for (VoUser u : users) {
				Set<VoTopic> userTopics = new TreeSet<VoTopic>( topicIdComp );
				
				for (VoUserGroup ug : u.getGroups()) {
					Set<VoTopic> topics = groupTopicMap.get(ug);
					topics.removeAll(userTopics);
					if (topics.size() != 0) {
						body += createGroupContent(pm, ug, topics);
					}
					userTopics.addAll(topics);
				}
				NotificationMessage mn = new NotificationMessage();
				mn.message = body;
				mn.subject = "Близкие события";
				mn.to = u.getEmail();
				try {
					sendMessage(mn, u);
					u.setLastNotified(now);
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}

		} finally {
			pm.close();
		}
	}

	private Map<VoUserGroup, Set<VoTopic>> collectTopicsByGroups(Set<VoUser> users, PersistenceManager pm) {
		// collect topics by group
		Map<VoUserGroup, Set<VoUser>> groupUserMap = arrangeUsersInGroups(users);
		Map<VoUserGroup, Set<VoTopic>> groupTopicMap = new TreeMap<VoUserGroup, Set<VoTopic>>(ugComp);
		
		for (VoUserGroup ug : groupUserMap.keySet()) {
			Set<VoTopic> topics = new TreeSet<VoTopic>(topicIdComp);
			topics.addAll(MessageServiceImpl.getTopics(ug, MessageType.BASE, 0, 10, false, con, pm));
			groupTopicMap.put(ug, topics);
		}
		return groupTopicMap;
	}

	private String createGroupContent(PersistenceManager pm, VoUserGroup ug, Set<VoTopic> topics) {
		String groupContent = "Пишут в группе '" + ug.getName() + "=====\n";
		Set<VoTopic> orderedTopics = new TreeSet<VoTopic>( topicCreatedDateComp );
		for (VoTopic tpc : orderedTopics) {
			String topicTxt = createTopicContent(pm, ug, tpc);
			groupContent += topicTxt;
		}
		return groupContent;
	}

	private String createTopicContent(PersistenceManager pm, VoUserGroup ug, VoTopic tpc) {
		String topicTxt = new Date(((long) tpc.getCreatedAt()) * 1000L) + " " + pm.getObjectById(VoUser.class, tpc.getAuthorId()).getName();
		topicTxt += (ug.getImportantScore() <= tpc.getImportantScore() ? "Важно!" : "") + "===== \n";
		topicTxt += new String(tpc.getContent(), 0, Math.min(255, tpc.getContent().length));
		topicTxt += "...\n--------------------------------------------------------\n\n";
		return topicTxt;
	}

	
	Comparator<VoTopic> topicIdComp = new Comparator<VoTopic>(){
		@Override
		public int compare(VoTopic o1, VoTopic o2) {
			return Long.compare( o1.getId(), o2.getId());
		}
	};
	
	Comparator<VoTopic> topicCreatedDateComp = new Comparator<VoTopic>(){

		@Override
		public int compare(VoTopic o1, VoTopic o2) {
			return -Integer.compare(o1.getCreatedAt(), o2.getCreatedAt());
		}
		
	};
}
