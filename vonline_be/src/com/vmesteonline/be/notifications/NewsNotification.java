package com.vmesteonline.be.notifications;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;

public class NewsNotification extends Notification {
	private static Logger logger = Logger.getLogger(NewsNotification.class.getSimpleName());

	@Override
	public void makeNotification(Set<VoUser> users) {}
	
	public void sendNotifications( ) throws InvalidOperation{

		PersistenceManager pm = PMF.getPm();
		int now = (int) (System.currentTimeMillis()/1000L);
		try {
			Set<VoUser> users = createRecipientsList(pm);
			logger.fine("Start NEWS notification. THere are "+users+" to notify");
			
			if(users.size()>0){
				new NewTopicsNotification(messagesToSend).makeNotification(users); 
				logger.fine("Got "+messagesToSend.size()+" to send new Topics");
				new NewNeigboursNotification(messagesToSend).makeNotification(users);
				logger.fine("Got +"+messagesToSend.size()+" to send new News");
				
				for( Entry<VoUser, List<NotificationMessage>> un :messagesToSend.entrySet()){
					VoUser user = un.getKey();
					String body = "Новости ВместеОнлайн.ру<br/><br/>";
					for( NotificationMessage nm : un.getValue())
						body += nm.message + "<br/><br/>";
					
					body += "Подробности на сайте<a href=\"https://"+host+"\"> ВместеОнлайн.ру</a>";
					body += "<br/><i>Вы можете изменить рассылку новостей в</i><a href=\"https://"+host+"/settings\"/>настройках профиля</a>";
					logger.fine("Got +"+messagesToSend.size()+" to send new News");	
					decorateAndSendMessage(user, " новости рядом с вами", body);
					logger.fine("News sent to:" + user);
					user.setLastNotified(now);
					
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			logger.fine("Got exception:" + (e instanceof InvalidOperation ? ((InvalidOperation)e).why : e.getMessage()) );
			throw new InvalidOperation(VoError.GeneralError, e instanceof InvalidOperation ? ((InvalidOperation)e).why : e.getMessage());
		} finally { 
			pm.close();
		}
	}
}
