package com.vmesteonline.be.jdo2.dialog;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.messageservice.Attach;
import com.vmesteonline.be.messageservice.DialogMessage;

@PersistenceCapable
public class VoDialogMessage {

	public DialogMessage getDialogMessage( PersistenceManager pm){
		List<Attach> docs = new ArrayList<Attach>();
		List<Attach> imgs = new ArrayList<Attach>();
		if( null!=attachs)
		for( Long farId : attachs ){
			VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, farId);
			( att.isImage() ?  imgs : docs ).add( att.getAttach() );
		}
		return new DialogMessage(id, dialogId, authorId, content, createDate, imgs, docs);
	} 
	
	public VoDialogMessage(long dialogId, long authorId, String content) {
		super();
		this.dialogId = dialogId;
		this.authorId = authorId;
		this.content = content;
		this.createDate = (int)(System.currentTimeMillis() / 1000L);
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Long id;
	
	@Persistent
	protected long dialogId;
	
	@Persistent
	protected long authorId;
	
	@Persistent
	@Unindexed
	protected String content;
	
	@Persistent
	@Unindexed
	private int createDate;
	
	@Persistent
	@Unindexed
	private List<Long> attachs;

	
	public List<Long> getAttachs() {
		return attachs;
	}

	public void setAttachs(List<Long> attachs) {
		this.attachs = attachs;
	}

	public long getDialogId() {
		return dialogId;
	}

	public void setDialogId(long dialogId) {
		this.dialogId = dialogId;
	}

	public long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(long authorId) {
		this.authorId = authorId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCreateDate() {
		return createDate;
	}

	public void setCreateDate(int createDate) {
		this.createDate = createDate;
	}

	public long getId() {
		return id;
	}

}
