package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.NotificationFreq;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.EMailHelper;

public abstract class Notification {

	public static class NotificationMessage {
		public String to;
		public String from;
		public String cc;
		public String subject;
		public String message;
	}

	public abstract void makeNotification(); 
	
	protected Set<VoUser> createRecipientsList() {

		List<VoUser> userList = new ArrayList<VoUser>();
		PersistenceManager pm = PMF.getPm();
		try {
			int twoDaysAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
			int weekAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
			List<VoSession> vsl = (List<VoSession>) pm.newQuery(VoSession.class, "lastActivityTs < " + twoDaysAgo).execute();
			for (VoSession vs : vsl) {
				VoUser vu = pm.getObjectById(VoUser.class, vs.getUserId());
				
				if (vu.isEmailConfirmed()) {
					//найдем самую псоледнюю сессию ползователя
					List<VoSession> uSessions = (List<VoSession>)pm.newQuery(VoSession.class, "userId=="+vu.getId()).execute();
					Collections.sort(uSessions, lastActivityComparator );
					boolean activityWasMoreThenTwoDaysAgo = uSessions.get(uSessions.size()-1).getLastActivityTs() < twoDaysAgo;
					for( VoSession ns: uSessions ){
						if( ns.getLastActivityTs() < weekAgo ) //пора удалять неактивную сессию
							pm.deletePersistent(ns);
						else break;
					}
					
					if(activityWasMoreThenTwoDaysAgo){
						
						int timeAgo = (int) (System.currentTimeMillis() / 1000L) - vu.getLastNotified();
						NotificationFreq nf = vu.getNotificationFreq().freq;
						if (NotificationFreq.DAYLY == nf && timeAgo >= 86400 || NotificationFreq.TWICEAWEEK == nf && timeAgo >= 3 * 86400
								|| NotificationFreq.WEEKLY == nf && timeAgo >= 7 * 86400)
	
							userList.add(vu);
					}
				}
			}
		} finally {
			pm.close();
		}
		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll(userList);
		return userSet;
	}

	protected void sendMessage(NotificationMessage mn, VoUser u) throws IOException {
		EMailHelper.sendSimpleEMail("Vmesteonline.ru <info@vmesteonline.ru>", mn.to, mn.subject, mn.message);
	}
	
	protected Map<VoUserGroup, Set<VoUser>> arrangeUsersInGroups(Set<VoUser> users) {
		// group users by groups and group types
		Map<VoUserGroup, Set<VoUser>> groupUserMap = new TreeMap<VoUserGroup, Set<VoUser>>(ugComp);
		for (VoUser u : users) {
			for (VoUserGroup ug : u.getGroups()) {
				Set<VoUser> ul;
				if (null == (ul = groupUserMap.get(ug))) {
					ul = new TreeSet<VoUser>( vuComp);
					groupUserMap.put(ug, ul);
				}
				ul.add(u);
			}
		}
		return groupUserMap;
	}
	
	protected Comparator<VoUser> vuComp = new Comparator<VoUser>() {
		@Override
		public int compare(VoUser o1, VoUser o2) {
			return Long.compare( o1.getId(), o2.getId());
		}
	};
	protected Comparator<VoUserGroup> ugComp = new Comparator<VoUserGroup>() {
		@Override
		public int compare(VoUserGroup o1, VoUserGroup o2) {
			Long.compare(o1.getId(), o2.getId());
			return 0;
		}
	};
	
	Comparator<VoSession> lastActivityComparator = new Comparator<VoSession>(){

		@Override
		public int compare(VoSession o1, VoSession o2) {
			return Integer.compare(o1.getLastActivityTs(), o2.getLastActivityTs());
		}
	};
}
