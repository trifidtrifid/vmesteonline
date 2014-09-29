package com.vmesteonline.be;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;


public class UPDATEServlet extends QueuedServletWithKeyHelper {

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		
		long now = System.currentTimeMillis();
		
		if( keyRequestAndQueuePush(arg0, arg1) ){
			

			PersistenceManager pm = PMF.getPm();
			pm.setMultithreaded(false);
			pm.setIgnoreCache(true);
			try {
				
				Extent<VoUser> users = pm.getExtent(VoUser.class);
				for (VoUser voUser : users) {
					voUser.setLastNotified(0);
				}
				pm.makePersistent(users);
						
				/*Extent<VoUserGroup> userGroupE = pm.getExtent(VoUserGroup.class);
				for (VoUserGroup voUserGroup : userGroupE) {
					voUserGroup.setVisibleGroups(null);
					List<Long> visibleGroups = voUserGroup.getVisibleGroups(pm);
					
					List<VoTopic> topics = (List<VoTopic>) pm.newQuery(VoTopic.class, "userGroupId=="+voUserGroup.getId()).execute();
					for (VoTopic voTopic : topics) {
						voTopic.setVisibleGroups( new ArrayList<Long>(visibleGroups) );
					}
					pm.makePersistentAll(topics);
					pm.makePersistent(voUserGroup);
				}*/
				
				
			} catch( Exception e){
				e.printStackTrace();
				arg1.getOutputStream().write(("Failed to initialize! "+e.getMessage()).getBytes());
			} finally {
				pm.close();
			}
			
			sendTheResultNotification(arg0, arg1, now, "OK");
		}
	}
}
