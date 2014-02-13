package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public class VoUserObject {

	public long getUserId() {
		return userId;
	}

	public VoUserObject() {
	}

	public VoUserObject(long userId, long messageId, boolean likes, boolean unlikes, boolean read) {
		this(userId, messageId);
		this.likes = likes;
		this.unlikes = unlikes;
		this.read = read;
	}

	public VoUserObject(long userId, long messageId) {
		this.id = messageId;
		this.userId = userId;
		this.userMessageId = createUserMessageId(userId, messageId);
	}

	public void setUserId(long userId) {
		this.userId = userId;
		this.userMessageId = createUserMessageId(userId, id);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
		this.userMessageId = createUserMessageId(userId, id);
	}

	public boolean isLikes() {
		return likes;
	}

	public void setLikes(boolean likes) {
		this.likes = likes;
	}

	public boolean isUnlikes() {
		return unlikes;
	}

	public void setUnlikes(boolean unlikes) {
		this.unlikes = unlikes;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public String toString() {
		return "VoUserMessage [userMessageId=" + userMessageId + ", messageId=" + id + ", userId=" + userId + ", likes=" + likes + ", unlikes=" + unlikes
				+ ", read=" + read + "]";
	}

	public static <T> Key createKey(Class<T> className, long userId, long messageId) {
		return KeyFactory.createKey(className.getSimpleName(), createUserMessageId(userId, messageId));
	}

	/*
	 * public static <T> Key getObjectKey(Class<T> className, long userId, long
	 * messageId) { return KeyFactory.createKey(className.getSimpleName(), userId
	 * << 32 + (messageId & 0xFFFFFFFFL)); }
	 */
	private static String createUserMessageId(long userId, long messageId) {
		return new String("" + userId + "X" + messageId);
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String userMessageId;

	@Persistent
	@Unindexed
	private long id;

	@Persistent
	@Unindexed
	private long userId;

	@Persistent
	@Unindexed
	private boolean likes;

	@Persistent
	@Unindexed
	private boolean unlikes;

	@Persistent
	@Unindexed
	private boolean read;
}
