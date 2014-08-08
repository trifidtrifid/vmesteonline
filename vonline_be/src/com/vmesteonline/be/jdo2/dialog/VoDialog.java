package com.vmesteonline.be.jdo2.dialog;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.management.openmbean.InvalidOpenTypeException;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.ShortUserInfo;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.messageservice.Attach;
import com.vmesteonline.be.messageservice.Dialog;
import com.vmesteonline.be.utils.StorageHelper;
import com.vmesteonline.be.utils.StorageHelper.FileSource;

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

	public Collection<VoDialogMessage> getMessages(int afterDate, int lastCount, PersistenceManager pm) {
		Query q = pm.newQuery(VoDialogMessage.class, "dialogId=="+id+
				(afterDate > 0 ? " && createDate>"+afterDate : ""));
		//q.setOrdering("createDate DESC"); List will be empty if sorting is enabled :(
		List<VoDialogMessage> msgs = (List<VoDialogMessage>) q.execute();
		SortedSet<VoDialogMessage> msgsSorted = new TreeSet<VoDialogMessage>( new Comparator<VoDialogMessage>(){
			@Override
			public int compare(VoDialogMessage o1, VoDialogMessage o2) {
				return -Integer.compare(o1.getCreateDate(), o2.getCreateDate());
			}});
		msgsSorted.addAll(msgs);
		if( lastCount>0 && msgsSorted.size() > lastCount ){
			Iterator<VoDialogMessage> mi = msgsSorted.iterator();
			while( lastCount-- != 0 ) {
				mi.next();
			}
			msgsSorted.tailSet(mi.next());
		}
		return msgsSorted;
	}

	public VoDialogMessage postMessage(long currentUserId, String content, List<Attach> attachs, PersistenceManager pm) {
		VoDialogMessage dmsg = new VoDialogMessage(id, currentUserId, content);
		List<Long> attchs = new ArrayList<Long>();
		for( Attach att: attachs ){
			try {
				FileSource fs = StorageHelper.createFileSource( att );
				VoFileAccessRecord cfar;
				if( fs == null ){
					cfar = pm.getObjectById(VoFileAccessRecord.class, StorageHelper.getFileId(att.getURL()));
					cfar.updateContentParams(att.contentType, att.fileName);
				}	else {	
					cfar = StorageHelper.saveAttach( fs.fname, fs.contentType, currentUserId, true, fs.is, pm);
				}
				attchs.add( cfar.getId());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		dmsg.setAttachs(attchs);
		lastMessageDate = dmsg.getCreateDate();
		pm.makePersistent(dmsg);
		pm.makePersistent(this);
		
		Queue queue = QueueFactory.getDefaultQueue();
    for( Long recipient : users )
    	if( recipient != currentUserId)
				queue.add(withUrl("/tasks/notification").param("rt", "ndm")
		    		.param("dg", ""+getId())
		    		.param("ar", ""+currentUserId)
		    		.param("rt", ""+recipient));
		return dmsg;
	}
	
}
