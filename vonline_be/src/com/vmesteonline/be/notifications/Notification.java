package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.GroupType;
import com.vmesteonline.be.NotificationFreq;
import com.vmesteonline.be.UserServiceImpl;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.dialog.VoDialog;
import com.vmesteonline.be.jdo2.dialog.VoDialogMessage;
import com.vmesteonline.be.utils.EMailHelper;

public abstract class Notification {

	public static class NotificationMessage {
		public String to;
		public String from;
		public String cc;
		public String subject;
		public String message;
	}

	public abstract void makeNotification( Set<VoUser> users ); 
	protected Map< VoUser, List<NotificationMessage>> messagesToSend = new HashMap<VoUser, List<NotificationMessage>>();
	
	protected Set<VoUser> createRecipientsList(PersistenceManager pm) {

		List<VoUser> userList = new ArrayList<VoUser>();

			int twoDaysAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
			int weekAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
			List<VoSession> vsl = (List<VoSession>) pm.newQuery(VoSession.class, "lastActivityTs < " + twoDaysAgo).execute();
			for (VoSession vs : vsl) {
				VoUser vu;
				try {
					vu = pm.getObjectById(VoUser.class, vs.getUserId());
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
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
		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll(userList);
		return userSet;
	}

	protected void sendMessage(NotificationMessage mn, VoUser u) throws IOException {
		List<NotificationMessage> uns = messagesToSend.get(u);
		if( null == uns)  uns = new ArrayList<NotificationMessage>();
		uns.add(mn);
		messagesToSend.put(u, uns);
	}
	
	protected static Map<VoUserGroup, Set<VoUser>> arrangeUsersInGroups(Set<VoUser> users) {
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
	
	public static void messageBecomeImportantNotification( VoTopic it, VoUserGroup group ){

		PersistenceManager pm = PMF.getPm();
		try {
			List<VoUser> usersForMessage = UserServiceImpl.getUsersByLocation(it, group.getRadius(), pm);
			
			String subject = "ВместеОнлайн.ру: важное сообщение";
			String body;
			try {
				body = new String( it.getContent(), "UTF-8" );
			} catch (UnsupportedEncodingException e1) {
				body = new String( it.getContent() );
				e1.printStackTrace();
			}
			body += "<br/> Ваши соседи считают это сообщение важным. Важность: "+it.getImportantScore();
			for(VoUser rcpt: usersForMessage){
				try {
					EMailHelper.sendSimpleEMail( 
							rcpt, subject, body );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} finally {
			pm.close();
		}
	}
	
	public static void dialogMessageNotification( VoDialog dlg, VoUser author, VoUser rcpt ){
		PersistenceManager pm = PMF.getPm();
		try {
			Collection<VoDialogMessage> messages = dlg.getMessages(0, 2, pm);
			VoDialogMessage lastMsg;
			if( messages.size() > 1 && 
					(lastMsg = messages.iterator().next()).getAuthorId() != messages.iterator().next().getAuthorId() ){
				
				try {
					EMailHelper.sendSimpleEMail( 
							rcpt, "ВместеОнлайн.ру: сообщение от "+author.getName(), 
							author.getName() + " " + author.getLastName() + " написал вам: <br/><i>"
									+ lastMsg.getContent()+"</i>");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} finally {
			pm.close();
		}
	}
	
	public static void welcomeMessageNotification( VoUser newUser ){
		String body = "Добро пожаловть на сайт Вашего дома!<br/><br/> ";
		List<VoUserGroup> groups = newUser.getGroups();
		PersistenceManager pm = PMF.getPm();
		try {
			Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
			userSet.addAll((List<VoUser>) pm.newQuery(VoUser.class, "").execute());
			Map<VoUserGroup, Set<VoUser>> usersMap = arrangeUsersInGroups(userSet);
			
			body += "На сайте уже зарегистрированно: "+userSet.size()+" человек<br/>";
			for(VoUserGroup group: groups ){
				int usersInGroup;
				if( null!=usersMap.get(group) && (usersInGroup = usersMap.get(group).size()) > 0 ){
					
					if( GroupType.FLOOR.getValue() == group.getGroupType()){
						body += "\tНа вашем этаже: ";
					} else if( GroupType.STAIRCASE.getValue() == group.getGroupType()){
						body += "\tВ вашем подъезде: ";
					} else if( GroupType.BUILDING.getValue() == group.getGroupType()){
						body += "\tВ доме: ";
					} else if( GroupType.BLOCK.getValue() == group.getGroupType()){
						body += "\tВ вашем квартале: ";
					} else if( GroupType.DISTRICT.getValue() == group.getGroupType()){
						body += "\tВ вашем районе: ";
					} else if( GroupType.TOWN.getValue() == group.getGroupType()){
						body += "\tВ вашем городе: ";
					} else {
						continue;
					}
					body += usersInGroup +" <br/>";
				}
	 		}
			body += "<br/> Мы создали этот сайт, чтобы Ваша жизнь стала чуть комфортней, от того что вы будете в курсе что происходит в вашем доме. <br/>"
					+ "Мы - Вместеонлайн.ру.<br/>Вы всегда можете связаться с нами по телефону, отправить нам письмо по адресу.<br/><br/>";
			
			body += "На страницах сайта вы найдете все новости и полезную информацию от управляющей компании, обсудить их с соседями вашего дома...<br/><br/>";
			
			if( !newUser.isEmailConfirmed() ){
				body += "Чтобы получать всю актуальну информацию о вашем доме и ваших соседях, подтвердите ваш email перейдя со ссылке: http://vmesteonline.ru/profile-"+newUser.getId()+","+newUser.getConfirmCode()+"<br/>";
			}
			
			body += "Спасибо что вы вместе, онлайн!";
			try {
				EMailHelper.sendSimpleEMail( newUser.getEmail(), "ВместеОнлайн.ру: добро пожаловать в ваш дом онлайн!",body);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} finally {
			pm.close();
		}		
	}
	
	public static Comparator<VoUser> vuComp = new Comparator<VoUser>() {
		@Override
	public int compare(VoUser o1, VoUser o2) {
			return Long.compare( o1.getId(), o2.getId());
		}
	};
	public static Comparator<VoUserGroup> ugComp = new Comparator<VoUserGroup>() {
		@Override
		public int compare(VoUserGroup o1, VoUserGroup o2) {
			Long.compare(o1.getId(), o2.getId());
			return 0;
		}
	};
	
	public static Comparator<VoSession> lastActivityComparator = new Comparator<VoSession>(){

		@Override
		public int compare(VoSession o1, VoSession o2) {
			return Integer.compare(o1.getLastActivityTs(), o2.getLastActivityTs());
		}
	};
}
