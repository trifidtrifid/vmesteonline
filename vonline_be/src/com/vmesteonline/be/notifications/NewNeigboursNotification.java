package com.vmesteonline.be.notifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.ShortUserInfo;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.VoHelper;


public class NewNeigboursNotification extends Notification {

	@Override
	public void makeNotification() {
	
		
		
	}

	//метод новые соседи зарегестрировавшиеся за последнюю неделю
	private Map< VoUserGroup, List<VoUser>> getNewNeighbors( ){
		
		Map< VoUserGroup, List<VoUser>> nuMap = new TreeMap<VoUserGroup, List<VoUser>>( super.ugComp );
		PersistenceManager pm = PMF.getPm();
		try {
			int weekAgo = (int) (System.currentTimeMillis() / 1000L) - 86400 * 2;
			List<VoUser> newUsers = (List<VoUser>)pm.newQuery(VoUser.class, "registered>="+weekAgo).execute();
			Set<VoUser> userSet = new TreeSet<VoUser>(vuComp);
			userSet.addAll(newUsers);
			Map<VoUserGroup, Set<VoUser>> usersInGroups = arrangeUsersInGroups(userSet);
			
			
	/*		VoUserGroup group = pm.getObjectById(VoUserGroup.class, groupId);
			List<VoUser> users = getUsersByLocation( currentUser, group.getRadius(), pm );
			return VoHelper.convertMutableSet( users, new ArrayList<ShortUserInfo>(), new ShortUserInfo());
*/		} finally {
			pm.close();
		}
		return nuMap;
	}
	

}
