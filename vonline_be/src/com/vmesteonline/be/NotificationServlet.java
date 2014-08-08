package com.vmesteonline.be;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoTopic;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.jdo2.dialog.VoDialog;
import com.vmesteonline.be.notifications.NewNeigboursNotification;
import com.vmesteonline.be.notifications.NewTopicsNotification;
import com.vmesteonline.be.notifications.NewsNotification;
import com.vmesteonline.be.notifications.Notification;

@SuppressWarnings("serial")
public class NotificationServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
		try {
			String reqType = req.getParameter("rt");
			if( "swm".equals(reqType)){
				sendWelcomeMsg(req);
			
			} else if( "mbi".equals(reqType)){
				sendNewImportantMsg(req);
			
			} else if( "ndm".equals(reqType)){
				sendNewDialogMsg(req);
			
			} else if( "news".equals(reqType)){
				PersistenceManager pm = PMF.getPm();
				try {
					new NewsNotification().sendNotifications();
				} finally {
					pm.close();
				}	
			}  
			rsp.setStatus(HttpServletResponse.SC_OK);
		} catch (InvalidOperation e) {

			e.printStackTrace();
			rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST, e.why);
		}
	}

	private void sendNewImportantMsg(HttpServletRequest req) throws InvalidOperation {
		
		PersistenceManager pm = PMF.getPm();
		try {
			Notification.messageBecomeImportantNotification( 
							(VoTopic)getVoObjectByParam( req, "it", VoTopic.class.getName(), pm),
							(VoUserGroup)getVoObjectByParam( req, "ug", VoUserGroup.class.getName(), pm));
			
		} finally {
			pm.close();
		}
	}
	
private void sendNewDialogMsg(HttpServletRequest req) throws InvalidOperation {
		
		PersistenceManager pm = PMF.getPm();
		try {
			Notification.dialogMessageNotification(
							(VoDialog)getVoObjectByParam( req, "dg", VoDialog.class.getName(), pm),
							(VoUser)getVoObjectByParam( req, "ar", VoUser.class.getName(), pm),
							(VoUser)getVoObjectByParam( req, "rt", VoUser.class.getName(), pm));
			
		} finally {
			pm.close();
		}
	}

	private void sendWelcomeMsg(HttpServletRequest req) {
		String uid = req.getParameter("uid");
		if( null!=uid ){
			PersistenceManager pm = PMF.getPm();
			try {
				Notification.welcomeMessageNotification(pm.getObjectById(VoUser.class, Long.parseLong(uid)));
				
			} finally {
				pm.close();
			}
		}
	}
	
	private Object getVoObjectByParam( HttpServletRequest req, String pName, String className, PersistenceManager pm ) throws InvalidOperation{
		try {
			return pm.getObjectById( Class.forName(className), Long.parseLong( req.getParameter(pName)));
		} catch (Exception e) {
			throw new InvalidOperation( VoError.IncorrectParametrs, "failed to get");
		}
	}

}
