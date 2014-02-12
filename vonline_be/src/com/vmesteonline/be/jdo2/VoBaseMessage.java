package com.vmesteonline.be.jdo2;

import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.Message;
import com.vmesteonline.be.MessageType;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class VoBaseMessage extends VoUserAttitude {

	public VoBaseMessage(Message msg) {
		super(msg.getLikesNum(), msg.getUnlikesNum());
		content = msg.getContent().getBytes();
		tags = msg.getTags();
		links = msg.getLinkedMessages();
		type = msg.getType();
		authorId = KeyFactory.createKey(VoUser.class.getSimpleName(), msg.getAuthorId());
		createdAt = msg.getCreated();
	}

	public void setCreatedAt(int createdAt) {
		this.createdAt = createdAt;
	}

	public VoBaseMessage() {
	}

	public Key getId() {
		return id;
	}

	public Key getAuthorId() {
		return authorId;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public void setId(Key key) {
		this.id = key;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public int getEditedAt() {
		return editedAt;
	}

	public void setEditedAt(int editedAt) {
		this.editedAt = editedAt;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key id;

	@Persistent
	@Unindexed
	protected byte[] content;

	@Persistent
	protected Map<Long, String> tags;

	@Persistent
	@Unindexed
	protected Map<MessageType, Long> links;
	@Persistent
	@Unindexed
	protected MessageType type;

	@Persistent
	@Unindexed
	protected Key authorId;
	@Persistent
	@Unindexed
	protected int createdAt;

	@Persistent
	@Unindexed
	protected int editedAt;
}
