package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.utils.SystemProperty;
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

	private static String host;
	static {
		host = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production ? "vmesteonline.ru" : "localhost:8888";
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
			
			String subject = "важное сообщение";
			String body;
			body = it.getContent();
			
			body += "<br/> Ваши соседи считают это сообщение важным. Важность: "+it.getImportantScore();
			for(VoUser rcpt: usersForMessage){
					decorateAndSendMessage(rcpt, subject, body); 
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
			Iterator<VoDialogMessage> mi = messages.iterator();
			if( messages.size() > 0 ){
				lastMsg = mi.next();
			
				if(messages.size() == 1  ||  //else check that the last message has different author 
						lastMsg.getAuthorId() != mi.next().getAuthorId() ){
					
					try {
						String body = 
								author.getName() + " " + author.getLastName() + " написал вам: <br/><i>"
										+ lastMsg.getContent()+"</i><br/><br/><a href=\"http://"+host+"/dialog-single-"+dlg.getId()+"\">Ответить...</a>";
						
						decorateAndSendMessage(rcpt, "сообщение от "+author.getName(), body);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		} finally {
			pm.close();
		}
	}
	
	public static void welcomeMessageNotification( VoUser newUser, PersistenceManager pm ){
		
		String body = newUser.getName() + " " + newUser.getLastName() + ", добро пожаловть на сайт Вашего дома!<br/><br/> ";
		List<VoUserGroup> groups = newUser.getGroups();

		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll((List<VoUser>) pm.newQuery(VoUser.class, "").execute());
		Map<VoUserGroup, Set<VoUser>> usersMap = arrangeUsersInGroups(userSet);
		
		body += "На сайте уже зарегистрированно: "+userSet.size()+" человек<br/>";
		for(VoUserGroup group: groups ){
			int usersInGroup;
			if( null!=usersMap.get(group) && (usersInGroup = usersMap.get(group).size()) > 0 ){
				
				if( GroupType.FLOOR.getValue() == group.getGroupType() && 0!=newUser.getAddress().getFloor() ){
					body += "\tНа вашем этаже: ";
				} else if( GroupType.STAIRCASE.getValue() == group.getGroupType() && 0!=newUser.getAddress().getStaircase()){
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
				+ "Мы - Вместеонлайн.ру.<br/>Вы всегда можете связаться с нами по адресу <a href=\"mailto:info@vmesteonline.ru\">info@vmesteonline.ru</a> нам письмо по адресу.<br/><br/>";
		
		body += "На страницах сайта вы найдете все новости и полезную информацию от управляющей компании, обсудить их с соседями вашего дома...<br/><br/>";
		
		if( !newUser.isEmailConfirmed() ){
			body += "Чтобы получать всю актуальну информацию о вашем доме и ваших соседях, подтвердите ваш email перейдя по этой <a href=\"http://"+host+"/confirm/profile-"+newUser.getId()+","+newUser.getConfirmCode()+"\">ссылке</a><br/>";
			pm.makePersistent(newUser);//to save confirm code
		}
		
		decorateAndSendMessage(newUser, "добро пожаловать в ваш дом онлайн!", body);
		
	}

	static void decorateAndSendMessage(VoUser user, String subject, String body) {
		body += "<p>Спасибо что вы вместе, онлайн!<br/>Будем рады прочитать ваши пожелания и предложения на <a href=\"http://"+host+"/blog\">странице обсуждения</a></p>";
		try {
			EMailHelper.sendSimpleEMail( 
					URLEncoder.encode(user.getName() + " " + user.getLastName(), "UTF-8") + " <"+user.getEmail()+">",
					"ВместеОнлайн.ру: " + subject ,body);
		} catch (IOException e) {
			e.printStackTrace();
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

	public static void sendRemindCodeMessage(VoUser user) {
		try {
			String body = user.getName() + " " + user.getLastName() + ", <br/>"
					+ "<p>На сайте Вашего дома было запрошено восстановление пароля доступа для адреса вашей электронной почты. "
					+ "Если вы хотите выполнить эту действие, воспользуйтесь <a href=\"http://"+host+"/remindPassword-"
							+ user.getConfirmCode()+","+URLEncoder.encode(user.getEmail(),"UTF-8")+"\">этой ссылкой</a>.</p>"
					+ "<p>Если у вас возникли трудности с доступом к сайту или есть вопросы, вы можете задать их нам в ответном письме.</p>";
			decorateAndSendMessage(user, "восстановление пароля", body);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
