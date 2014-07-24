package com.vmesteonline.be.jdo2.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.management.openmbean.InvalidOpenTypeException;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.ShortUserInfo;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.messageservice.Dialog;

@PersistenceCapable
public class VoDialog {
	
	public Dialog getDialog( PersistenceManager pm ) throws InvalidOperation {
		List< ShortUserInfo > usis = new ArrayList<ShortUserInfo>();
		for( Long uid : users){
			try {
				usis.add( pm.getObjectById(VoUser.class, uid).getShortUserInfo() );
			} catch (JDOObjectNotFoundException e) {
				
				throw new InvalidOperation(VoError.GeneralError, "Invalid dialog properties. USer registered in dialog but not found. Remove him!");
			}
		}
		return new Dialog(id, usis, createDate, lastMessageDate);
	}
	
	public VoDialog(List<Long> users) {
		super();
		this.users = users;
		this.createDate = (int)(System.currentTimeMillis() / 1000L);
		this.lastMessageDate = (int)(System.currentTimeMillis() / 1000L);
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	private List<Long> users;
	
	@Persistent
	@Unindexed
	private int createDate;
	
	@Persistent
	private int lastMessageDate;

	public List<Long> getUsers() {
		return users;
	}

	public void setUsers(List<Long> users) {
		this.users = users;
	}

	public int getCreateDate() {
		return createDate;
	}

	public void setCreateDate(int createDate) {
		this.createDate = createDate;
	}

	public int getLastMessageDate() {
		return lastMessageDate;
	}

	public void setLastMessageDate(int lastMessageDate) {
		this.lastMessageDate = lastMessageDate;
	}

	public long getId() {
		return id;
	}

	public List<VoDialogMessage> getMessages(int afterDate, int lastCount, PersistenceManager pm) {
		Query q = pm.newQuery(VoDialogMessage.class, "dialogId=="+id+
				(afterDate > 0 ? " && createDate>"+afterDate : ""));
		q.setOrdering("createDate desc");
		List<VoDialogMessage> msgs = (List<VoDialogMessage>) q.execute();
		if( msgs.size() > lastCount ){
			msgs = msgs.subList(0, lastCount);
		}
		return msgs;
	}

	public long postMessage(long currentUserId, String content, PersistenceManager pm) {
		VoDialogMessage dmsg = new VoDialogMessage(id, currentUserId, content);
		lastMessageDate = dmsg.getCreateDate();
		pm.makePersistent(dmsg);
		pm.makePersistent(this);
		return dmsg.getId();
	}
	
}
