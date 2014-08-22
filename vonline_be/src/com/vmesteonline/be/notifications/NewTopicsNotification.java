package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import org.apache.commons.lang3.StringEscapeUtils;

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

	
	public NewTopicsNotification( Map< VoUser, List<NotificationMessage>> ntf ) {
		this.messagesToSend = ntf;
	}

	public void makeNotification( Set<VoUser> users ) {

		int now = (int)(System.currentTimeMillis()/1000L);

		PersistenceManager pm = PMF.getPm();
		try {
			Map<Long, Set<VoTopic>> groupTopicMap = collectTopicsByGroups(users, pm);

			// create message for each user
			String body = "Близкие события<br/><br/>";
			
			for (VoUser u : users) {
				Set<VoTopic> userTopics = new TreeSet<VoTopic>( topicIdComp );
				
				for (Long ug : u.getGroups()) {
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

	private Map<Long, Set<VoTopic>> collectTopicsByGroups(Set<VoUser> users, PersistenceManager pm) {
		// collect topics by group
		Map<Long, Set<VoUser>> groupUserMap = arrangeUsersInGroups(users);
		Map<Long, Set<VoTopic>> groupTopicMap = new TreeMap<Long, Set<VoTopic>>();
		
		List<Long> groups = new ArrayList<Long>();
		groups.addAll(groupUserMap.keySet());
		
		Set<VoTopic> topics = new TreeSet<VoTopic>(topicIdComp);
		topics.addAll(MessageServiceImpl.getTopics( 
				groups, MessageType.BASE, 0, 10, false, pm));
	
		for( VoTopic topic: topics)
			groupTopicMap.put(topic.getUserGroupId(), topics);
		
		return groupTopicMap;
	}

	private String createGroupContent(PersistenceManager pm, Long ugId, Set<VoTopic> topics) {
		VoUserGroup ug = pm.getObjectById(VoUserGroup.class, ugId);
		
		String groupContent = "Пишут в группе '" + ug.getName() + "<br/>";
		Set<VoTopic> orderedTopics = new TreeSet<VoTopic>( topicCreatedDateComp );
		for (VoTopic tpc : orderedTopics) {
			String topicTxt = createTopicContent(pm, ug, tpc);
			groupContent += topicTxt;
		}
		return groupContent;
	}

	private String createTopicContent(PersistenceManager pm, VoUserGroup ug, VoTopic tpc) {
		String topicTxt = new Date(((long) tpc.getCreatedAt()) * 1000L) + " " + pm.getObjectById(VoUser.class, tpc.getAuthorId()).getName();
		topicTxt += (ug.getImportantScore() <= tpc.getImportantScore() ? "Важно!" : "") + "<br/>";
		topicTxt += StringEscapeUtils.escapeHtml4(tpc.getContent().substring( 0, Math.min(255, tpc.getContent().length())));
		if( tpc.getContent().length() > 255 ) topicTxt += "<a href=\"http://"+host+"/wall-single-"+tpc.getId()+"\">...</a>";
		topicTxt += "<br/>--------------------------------------------------------<br/><br/>";
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
