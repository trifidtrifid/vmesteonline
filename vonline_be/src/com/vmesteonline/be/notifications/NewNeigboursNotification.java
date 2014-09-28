package com.vmesteonline.be.notifications;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import org.apache.commons.lang3.StringEscapeUtils;

import com.vmesteonline.be.GroupType;
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
			Map< Long, Set<VoUser>> groupUsersMap = getNewNeighbors(pm);

			// create message for each user
			String body = "<b><p>Новые соседи</p></b>";
			
			for (VoUser u : users) {
				Set<VoUser> neghbors = new TreeSet<VoUser>( vuComp );
				
				for (Long ug : u.getGroups()) {
					Set<VoUser> ggoupNeighbors = groupUsersMap.get(ug);
					ggoupNeighbors.removeAll(neghbors);
					if (ggoupNeighbors.size() != 0) {
						body += createNeighborsContent(pm, ug, ggoupNeighbors);
					}
					neghbors.addAll(ggoupNeighbors);
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
	
	private String createNeighborsContent(PersistenceManager pm, Long ugId, Set<VoUser> neghbors) {
		VoUserGroup ug = pm.getObjectById(VoUserGroup.class, ugId);
		String groupContent = "<p>В группе '" + ug.getName() + "'<br/>";
		for (VoUser vuc : neghbors) {
			String contactTxt = createUserContactContent(pm, ug, vuc);
			groupContent += contactTxt;
		}
		groupContent += "</p>";
		return groupContent;
	}

	private String createUserContactContent(PersistenceManager pm, VoUserGroup ug, VoUser vuc) {
		
		VoPostalAddress address = pm.getObjectById(VoPostalAddress.class,vuc.getAddress());
		String contactTxt = "<a href=\"https://"+host+"/profile-"+vuc.getId()+"\">"+StringEscapeUtils.escapeHtml4(vuc.getName() + " " + vuc.getLastName())+"</a>";
		
		if( ug.getGroupType() <= GroupType.BUILDING.getValue() && 0!=address.getStaircase()) 
				contactTxt += " живет в подъезде " + address.getStaircase();
		
		if(  ug.getGroupType() <= GroupType.STAIRCASE.getValue() && 0!=address.getFlatNo()) 
				contactTxt += " в квартире " + address.getFlatNo() + ( 0!=address.getFloor() ? " на "+address.getFloor()+" этаже":"");
		
		if( ug.getGroupType() == GroupType.NEIGHBORS.getValue() ){
			
			VoBuilding vb = pm.getObjectById(VoBuilding.class, address.getBuilding());
			VoStreet vs = pm.getObjectById(VoStreet.class, vb.getStreet());
			contactTxt += " из дома " + vb.getFullNo() +" по " + vs.getName();
		} 
		
		if( ug.getGroupType() == GroupType.BLOCK.getValue())  {
				contactTxt += " из вашего района"; 
		}
	
		contactTxt += "<br/>";
		return contactTxt;
	}

	//новые соседи зарегестрировавшиеся за последнюю неделю
	private Map< Long, Set<VoUser>> getNewNeighbors( PersistenceManager pm ){
		
		Map< VoUserGroup, List<VoUser>> nuMap = new TreeMap<VoUserGroup, List<VoUser>>( super.ugComp );

		int weekAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
		List<VoUser> newUsers = (List<VoUser>)pm.newQuery(VoUser.class, "registered>="+weekAgo).execute();
		Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
		userSet.addAll(newUsers);
		return arrangeUsersInGroups(userSet);
	}
}
