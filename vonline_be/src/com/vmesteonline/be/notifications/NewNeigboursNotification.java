package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;
import com.vmesteonline.be.jdo2.postaladdress.VoPostalAddress;
import com.vmesteonline.be.jdo2.postaladdress.VoStreet;
import com.vmesteonline.be.notifications.Notification.NotificationMessage;


public class NewNeigboursNotification extends Notification {

	public NewNeigboursNotification( Map< VoUser, List<NotificationMessage>> ntf ) {
		this.messagesToSend = ntf;
	}

	@Override
	public void makeNotification( Set<VoUser> users ) {
		int now = (int)(System.currentTimeMillis()/1000L);

		PersistenceManager pm = PMF.getPm();
		try {
			Map< VoUserGroup, Set<VoUser>> groupUsersMap = getNewNeighbors(pm);

			// create message for each user
			String body = "Новые соседи<br/><br/>";
			
			for (VoUser u : users) {
				Set<VoUser> neghbors = new TreeSet<VoUser>( vuComp );
				
				for (VoUserGroup ug : u.getGroups()) {
					Set<VoUser> ggoupNeighbors = groupUsersMap.get(ug);
					ggoupNeighbors.removeAll(neghbors);
					if (ggoupNeighbors.size() != 0) {
						body += createNeighborsContent(pm, ug, ggoupNeighbors);
					}
					ggoupNeighbors.addAll(ggoupNeighbors);
				}
				NotificationMessage mn = new NotificationMessage();
				mn.message = body;
				mn.subject = "Новые соседи";
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
	
	private String createNeighborsContent(PersistenceManager pm, VoUserGroup ug, Set<VoUser> neghbors) {
		String groupContent = "В группе '" + ug.getName() + "' подкрепление <br/>";
		for (VoUser vuc : neghbors) {
			String contactTxt = createUserContactContent(pm, ug, vuc);
			groupContent += contactTxt;
		}
		return groupContent;
	}

	private String createUserContactContent(PersistenceManager pm, VoUserGroup ug, VoUser vuc) {
		
		VoPostalAddress address = vuc.getAddress();
		String contactTxt = "<a href=\"http://"+host+"/profile-"+vuc.getId()+"\">"+StringEscapeUtils.escapeHtml4(vuc.getName() + " " + vuc.getLastName())+"</a>";
		
		if( ug.getRadius() == 0 ) 
			contactTxt += " живет в квартире " + address.getFlatNo();
		else {
			VoBuilding vb = pm.getObjectById(VoBuilding.class, address.getBuilding());
			VoStreet vs = pm.getObjectById(VoStreet.class, vb.getStreet());
			
			if( ug.getRadius() < 50 )
				contactTxt += " из дома " + vb.getFullNo() +" по " + vs.getName();
			else {
				contactTxt += " из вашего района"; 
			}
		}
	
		contactTxt += "<br/>";
		return contactTxt;
	}

	//новые соседи зарегестрировавшиеся за последнюю неделю
	private Map< VoUserGroup, Set<VoUser>> getNewNeighbors( PersistenceManager pm ){
		
		Map< VoUserGroup, List<VoUser>> nuMap = new TreeMap<VoUserGroup, List<VoUser>>( super.ugComp );

		int weekAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
		List<VoUser> newUsers = (List<VoUser>)pm.newQuery(VoUser.class, "registered>="+weekAgo).execute();
		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll(newUsers);
		return arrangeUsersInGroups(userSet);
	}
}
