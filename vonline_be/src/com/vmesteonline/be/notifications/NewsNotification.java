package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.notifications.Notification.NotificationMessage;
import com.vmesteonline.be.utils.EMailHelper;

public class NewsNotification extends Notification {

	@Override
	public void makeNotification(Set<VoUser> users) {
	}
	
	public void sendNotifications( ) throws InvalidOperation{

		PersistenceManager pm = PMF.getPm();
		try {
			Set<VoUser> users = createRecipientsList(pm);
			if(users.size()>0){
				new NewTopicsNotification(messagesToSend).makeNotification(users); 
				new NewNeigboursNotification(messagesToSend).makeNotification(users);
				
				for( Entry<VoUser, List<NotificationMessage>> un :messagesToSend.entrySet()){
					VoUser user = un.getKey();
					String body = "Новости ВместеОнлайн.ру<br/><br/>";
					for( NotificationMessage nm : un.getValue())
						body += nm.message + "<br/><br/>";
					
					body += "Подробности на http://vmesteonline.ru";
						
					decorateAndSendMessage(user, " новости о рядом с вами", body);

				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, e instanceof InvalidOperation ? ((InvalidOperation)e).why : e.getMessage());
		} finally { 
			pm.close();
		}
	}
}
