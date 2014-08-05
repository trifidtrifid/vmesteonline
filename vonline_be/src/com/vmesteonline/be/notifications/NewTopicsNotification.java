package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

public class NewTopicsNotification extends Notification {

	private JDBCConnector con = new MySQLJDBCConnector();

	public void makeNotification() {

		int now = (int)(System.currentTimeMillis()/1000L);

		Set<VoUser> users = createRecipientsList();
		Map<VoUserGroup, Set<VoUser>> groupUserMap = arrangeUsersInGroups(users);

		// collect topics by group
		Map<VoUserGroup, List<VoTopic>> groupTopicMap = new TreeMap<VoUserGroup, List<VoTopic>>(ugComp);
		PersistenceManager pm = PMF.getPm();
		try {
			for (VoUserGroup ug : groupUserMap.keySet()) {
				groupTopicMap.put(ug, MessageServiceImpl.getTopics(ug, MessageType.BASE, 0, 10, false, con, pm));
			}

			// create message for each user
			String body = "Близкие события\n\n";
			for (VoUser u : users) {
				for (VoUserGroup ug : u.getGroups()) {
					List<VoTopic> topics = groupTopicMap.get(ug);
					if (topics.size() != 0) {
						body += createGroupContent(pm, ug, topics);
					}
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

	private String createGroupContent(PersistenceManager pm, VoUserGroup ug, List<VoTopic> topics) {
		String groupContent = "Пишут в группе '" + ug.getName() + "=====\n";
		for (VoTopic tpc : topics) {
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

}
