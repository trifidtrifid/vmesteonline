package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.apache.commons.lang3.StringEscapeUtils;

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

	private static Logger logger = Logger.getLogger(Notification.class.getName());

	public static class NotificationMessage {
		public String to;
		public String from;
		public String cc;
		public String subject;
		public String message;
	}

	protected static String host;
	static {
		host = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production ? "vmesteonline.ru" : "localhost:8888";
	}

	public abstract void makeNotification(Set<VoUser> users);

	protected Map<VoUser, List<NotificationMessage>> messagesToSend = new HashMap<VoUser, List<NotificationMessage>>();

	protected Set<VoUser> createRecipientsList(PersistenceManager pm) {

		List<VoUser> userList = new ArrayList<VoUser>();

		int now = (int) (System.currentTimeMillis() / 1000L);
		int twoDaysAgo = (int) now - 86400 * 2;
		int weekAgo = (int) now - 86400 * 2;
		List<VoSession> vsl = (List<VoSession>) pm.newQuery(VoSession.class, "lastActivityTs < " + twoDaysAgo).execute();
		for (VoSession vs : vsl) {
			VoUser vu;
			try {
				vu = pm.getObjectById(VoUser.class, vs.getUserId());
			} catch (JDOObjectNotFoundException onfe) {
				logger.warning("No user of session found by ID:" + vs.getUserId() + " discard the session " + vs);
				pm.deletePersistent(vs);
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}

			if (vu.isEmailConfirmed()) {
				// найдем самую псоледнюю сессию ползователя
				List<VoSession> uSessions = (List<VoSession>) pm.newQuery(VoSession.class, "userId==" + vu.getId()).execute();
				Collections.sort(uSessions, lastActivityComparator);
				int lastActivityTs = uSessions.get(uSessions.size() - 1).getLastActivityTs();
				boolean activityWasMoreThenTwoDaysAgo = lastActivityTs < twoDaysAgo;
				for (VoSession ns : uSessions) {
					if (ns.getLastActivityTs() < weekAgo) // пора удалять неактивную сессию
						pm.deletePersistent(ns);
					else
						break;
				}

				if (activityWasMoreThenTwoDaysAgo) {

					int timeAgo = (int) now - vu.getLastNotified();
					NotificationFreq nf = vu.getNotificationFreq().freq;
					if (NotificationFreq.DAYLY == nf && timeAgo >= 86400 || NotificationFreq.TWICEAWEEK == nf && timeAgo >= 3 * 86400
							|| NotificationFreq.WEEKLY == nf && timeAgo >= 7 * 86400) {
						logger.fine("User:" + vu + " would be notified with news");
						userList.add(vu);
					} else {
						logger.fine("USer:" + vu + " was notified " + timeAgo + " days ago and he perefers to be notified " + nf.name()
								+ " so he would not been notified this time");
					}

				} else {
					logger.fine("USer:" + vu + " visited the site at " + new Date(((long) lastActivityTs) * 1000L)
							+ " less the two days ago so he/she would not been notified with news");
				}
			} else {
				logger.fine("USer:" + vu + " not confirmed email, so new would not been sent.");
			}
		}
		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll(userList);
		return userSet;
	}

	protected void sendMessage(NotificationMessage mn, VoUser u) throws IOException {
		List<NotificationMessage> uns = messagesToSend.get(u);
		if (null == uns)
			uns = new ArrayList<NotificationMessage>();
		uns.add(mn);
		messagesToSend.put(u, uns);
	}

	protected static Map<Long, Set<VoUser>> arrangeUsersInGroups(Set<VoUser> users) {
		// group users by groups and group types
		Map<Long, Set<VoUser>> groupUserMap = new TreeMap<Long, Set<VoUser>>();
		for (VoUser u : users) {
			for (Long ug : u.getGroups()) {
				Set<VoUser> ul;
				if (null == (ul = groupUserMap.get(ug))) {
					ul = new TreeSet<VoUser>(vuComp);
					groupUserMap.put(ug, ul);
				}
				ul.add(u);
			}
		}
		return groupUserMap;
	}

	public static void messageBecomeImportantNotification(VoTopic it, VoUserGroup group) {

		PersistenceManager pm = PMF.getPm();
		try {
			List<VoUser> usersForMessage = UserServiceImpl.getUsersByLocation(it, group.getRadius(), pm);

			String subject = "важное сообщение";
			String body = "Ваши соседи считают это сообщение достойным внимания (важность: " + it.getImportantScore() + ")";

			body += "<i>" + StringEscapeUtils.escapeHtml4(it.getContent()) + "</i>";

			body += "<br/><br/><a href=\"http://" + host + "/wall-single-" + it.getId() + "\">Обсудить, ответить ...</a>";
			for (VoUser rcpt : usersForMessage) {
				decorateAndSendMessage(rcpt, subject, body);
			}

		} finally {
			pm.close();
		}
	}

	public static void dialogMessageNotification(VoDialog dlg, VoUser author, VoUser rcpt) {
		PersistenceManager pm = PMF.getPm();
		try {
			Collection<VoDialogMessage> messages = dlg.getMessages(0, 2, 0, pm);
			VoDialogMessage lastMsg;
			Iterator<VoDialogMessage> mi = messages.iterator();
			if (messages.size() > 0) {
				lastMsg = mi.next();

				if (messages.size() == 1 || // else check that the last message has different author
						lastMsg.getAuthorId() != mi.next().getAuthorId()) {

					try {
						String body = author.getName() + " " + author.getLastName() + " написал вам: <br/><i>" + lastMsg.getContent()
								+ "</i><br/><br/><a href=\"http://" + host + "/dialog-single-" + dlg.getId() + "\">Ответить...</a>";

						decorateAndSendMessage(rcpt, "сообщение от " + author.getName(), body);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} finally {
			pm.close();
		}
	}

	public static void welcomeMessageNotification(VoUser newUser, PersistenceManager pm) {

		String body = newUser.getName() + " " + newUser.getLastName() + ", добро пожаловать на сайт Вашего дома!<br/><br/> ";

		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll((List<VoUser>) pm.newQuery(VoUser.class, "").execute());

		body += "На сайте уже зарегистрированно: " + userSet.size() + " Ваших соседей<br/>";

		body += "<br/> Мы создали этот сайт, чтобы Ваша жизнь стала чуть комфортней, от того что вы будете в курсе что происходит в вашем доме. <br/><br/>";
		if (!newUser.isEmailConfirmed()) {
			body += "Для доступа к сайту, подтвердите ваш email перейдя по этой <a href=\"http://" + host + "/confirm/profile-" + newUser.getId() + ","
					+ newUser.getConfirmCode() + "\">ссылке</a><br/></br>";
			pm.makePersistent(newUser);// to save confirm code
		}

		body += "На страницах сайта вы найдете новости, полезную информацию от управляющей компании и сможете обсудить их с соседями...<br/><br/>";

		decorateAndSendMessage(newUser, "подтвердите email", body);

	}

	static void decorateAndSendMessage(VoUser user, String subject, String body) {
		body += "<p>Спасибо что вы с нами!<br/>Новости проекта в нашем <a href=\"http://" + host + "/blog\">блоге</a></p>";
		try {
			EMailHelper.sendSimpleEMail(URLEncoder.encode(user.getName() + " " + user.getLastName(), "UTF-8") + " <" + user.getEmail() + ">",
					"ВместеОнлайн.ру: " + subject, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Comparator<VoUser> vuComp = new Comparator<VoUser>() {
		@Override
		public int compare(VoUser o1, VoUser o2) {
			return Long.compare(o1.getId(), o2.getId());
		}
	};
	public static Comparator<VoUserGroup> ugComp = new Comparator<VoUserGroup>() {
		@Override
		public int compare(VoUserGroup o1, VoUserGroup o2) {
			Long.compare(o1.getId(), o2.getId());
			return 0;
		}
	};

	public static Comparator<VoSession> lastActivityComparator = new Comparator<VoSession>() {

		@Override
		public int compare(VoSession o1, VoSession o2) {
			return Integer.compare(o1.getLastActivityTs(), o2.getLastActivityTs());
		}
	};

	public static void sendRemindCodeMessage(VoUser user) {
		try {
			String body = user.getName() + " " + user.getLastName() + ", <br/>"
					+ "<p>На сайте Вашего дома было запрошено восстановление пароля доступа для адреса вашей электронной почты. "
					+ "Если вы хотите выполнить эту действие, воспользуйтесь " + "<a href=\"http://" + host + "/remember_passw.html#" + +user.getConfirmCode()
					+ "-" + URLEncoder.encode(user.getEmail(), "UTF-8") + "\">этой ссылкой</a>.</p>"
					+ "<p>Если у вас возникли трудности с доступом к сайту или есть вопросы, вы можете задать их нам в ответном письме.</p>";
			decorateAndSendMessage(user, "восстановление пароля", body);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}
}
