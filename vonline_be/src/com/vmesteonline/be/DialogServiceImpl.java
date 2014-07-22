package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.dialog.VoDialog;
import com.vmesteonline.be.jdo2.dialog.VoDialogMessage;
import com.vmesteonline.be.messageservice.Dialog;
import com.vmesteonline.be.messageservice.DialogMessage;
import com.vmesteonline.be.messageservice.DialogService.Iface;
import com.vmesteonline.be.utils.VoHelper;

public class DialogServiceImpl extends ServiceImpl implements Iface  {

	private static Logger logger = Logger.getLogger(DialogServiceImpl.class.getName());
	@Override
	public Dialog getDialog(List<Long> users, int after) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			//sort users 
			SortedSet userss = new TreeSet<Long>();
			userss.addAll(users);
			//add current user if not added
			if( !userss.contains( currentUserId ))
				userss.add(currentUserId);
			List<Long> usersaSorted = new ArrayList<Long>(userss); 
			
			String filterStr = "";
			for (Long userId : usersaSorted){
				filterStr += " && users=="+userId;
			}
			Query dlgQuery = pm.newQuery(VoDialog.class, "lastMessageDate>"+after + filterStr);
			dlgQuery.setOrdering("lastMessageDate");
			List<VoDialog> oldDialog = (List<VoDialog>) dlgQuery.execute();
			
			VoDialog dlg;
			if( oldDialog.size() == 0){ //there is no dialog exists, so create a new one
				dlg = new VoDialog(usersaSorted);
				pm.makePersistent(dlg);
			} else {
				dlg = oldDialog.get(0);
			}
			return dlg.getDialog(pm);

		} finally {
			pm.close();
		}
	}

	@Override
	public List<Dialog> getDialogs(int after) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			Query dlgQuery = pm.newQuery(VoDialog.class, "users=="+currentUserId+" && lastMessageDate>"+after);
			dlgQuery.setOrdering("lastMessageDate");
			List<VoDialog> oldDialog = (List<VoDialog>) dlgQuery.execute();
			
			return VoHelper.convertMutableSet(oldDialog, new ArrayList<Dialog>(), new Dialog());
		} finally {
			pm.close();
		}
	}

	@Override
	public List<DialogMessage> getDialogMessages(long dialogID, int afterDate, int lastCount) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			VoDialog vdlg = pm.getObjectById( VoDialog.class, dialogID );
			if( !new HashSet<Long>( vdlg.getUsers()).contains(currentUserId) )
				throw new InvalidOperation(VoError.IncorrectParametrs, "User not involved in this dialog.");
			
			return VoHelper.convertMutableSet( vdlg.getMessages( afterDate, lastCount, pm ), 
					new ArrayList<DialogMessage>(), new DialogMessage());
		
		} finally {
			pm.close();
		}
	}

	@Override
	public long postMessage(long dialogId, String content) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			VoDialog vdlg = pm.getObjectById( VoDialog.class, dialogId );
			if( !new HashSet<Long>( vdlg.getUsers()).contains(currentUserId) )
				throw new InvalidOperation(VoError.IncorrectParametrs, "User not involved in this dialog.");
			
			return vdlg.postMessage( currentUserId, content, pm );
		
		} finally {
			pm.close();
		}
	}

	@Override
	public void updateDialogMessage(long dlgMsgId, String content) throws InvalidOperation {
		
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			VoDialogMessage vdlg;
			try {
				vdlg = pm.getObjectById( VoDialogMessage.class, dlgMsgId );
			} catch (JDOObjectNotFoundException onfe) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "No dlialog message found by iD '"+dlgMsgId+"'");
				
			}
			if( currentUserId != vdlg.getAuthorId())
				throw new InvalidOperation(VoError.IncorrectParametrs, "Current User '"+currentUserId+"' not author of message.");
			
			vdlg.setContent(content);
			return;
		
		} finally {
			pm.close();
		}
	}

	@Override
	public void deleteDialogMessage(long dlgMsgId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			VoDialogMessage vdlg;
			try {
				vdlg = pm.getObjectById( VoDialogMessage.class, dlgMsgId );
			} catch (JDOObjectNotFoundException onfe) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "No dlialog message found by iD '"+dlgMsgId+"'");
				
			}
			if( currentUserId != vdlg.getAuthorId())
				throw new InvalidOperation(VoError.IncorrectParametrs, "Current User '"+currentUserId+"' not author of message.");
			
			pm.deletePersistent(vdlg);
			return;
		
		} finally {
			pm.close();
		}
	}

	@Override
	public void addUserToDialog(long dialogId, long userId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			VoDialog vdlg = pm.getObjectById( VoDialog.class, dialogId );
			HashSet<Long> usersSet = new HashSet<Long>( vdlg.getUsers());
			if( !usersSet.contains(currentUserId) )
				throw new InvalidOperation(VoError.IncorrectParametrs, "User not involved in this dialog.");
			
			if( !usersSet.contains( userId )){
				vdlg.getUsers().add(userId);
				pm.makePersistent(vdlg);
				logger.fine("USer "+userId+" added to dialog "+dialogId);
			} else {
				logger.fine("USer "+userId+" already involved into dialog "+dialogId);
			}
		
		} finally {
			pm.close();
		}		
	}

	@Override
	public void removeUserFromDialog(long dialogId, long userId) throws InvalidOperation {
		PersistenceManager pm = PMF.getPm();
		try {
			long currentUserId = getCurrentUserId();
			VoDialog vdlg = pm.getObjectById( VoDialog.class, dialogId );
			HashSet<Long> usersSet = new HashSet<Long>( vdlg.getUsers());
			if( !usersSet.contains(currentUserId) )
				throw new InvalidOperation(VoError.IncorrectParametrs, "User not involved in this dialog.");
			
			if( usersSet.remove( userId )){
				
				vdlg.setUsers(new ArrayList<Long>(usersSet));
				pm.makePersistent(vdlg);
				logger.fine("USer "+userId+" removed from dialog "+dialogId);
			} else {
				logger.fine("USer "+userId+" was not involved into dialog "+dialogId);
			}
		
		} finally {
			pm.close();
		}
		
	}

}
