package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VoUserMessage {

	/*
	 * 1: bool read, флаг прочитанности сообщения пользователем 2: bool likes, 3:
	 * bool unlikes
	 */

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String userMessageId;

	@Persistent
	@Unindexed
	private long messageId;

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

	public long getUserId() {
		return userId;
	}

	public VoUserMessage(VoUser user, VoMessage message, boolean likes, boolean unlikes, boolean read) {
		this(user.getId(), message.getId().getId());
		this.likes = likes;
		this.unlikes = unlikes;
		this.read = read;
	}

	public VoUserMessage(VoUser user, VoMessage message) {
		this(user.getId(), message.getId().getId());
	}

	public VoUserMessage(long userId, long messageId) {
		this.messageId = messageId;
		this.userId = userId;
		this.userMessageId = "" + userId + "X" + messageId; // KeyFactory.createKey(VoUserMessage.class.getSimpleName(),
																												// userId << 32 +
																												// (messageId &
																												// 0xFFFFFFFFL));
	}

	public static Key getObjectKey(long userId, long messageId) {
		return KeyFactory.createKey(VoUserMessage.class.getSimpleName(), userId << 32 + (messageId & 0xFFFFFFFFL));
	}

	public static Key getObjectKey(VoUser user, VoMessage message) {
		return getObjectKey(user.getId(), message.getId().getId());
	}

	public void setUserId(long userId) {
		this.userId = userId;
		this.userMessageId = "" + userId + "X" + messageId;
		; // KeyFactory.createKey(VoUserMessage.class.getSimpleName(), userId << 32
			// + (messageId & 0xFFFFFFFFL));
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessage(long messageId) {
		this.messageId = messageId;
		this.userMessageId = "" + userId + "X" + messageId;
		// KeyFactory.createKey(VoUserMessage.class.getSimpleName(), userId << 32 +
		// (messageId & 0xFFFFFFFFL));
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
		return "VoUserMessage [userMessageId=" + userMessageId + ", messageId=" + messageId + ", userId=" + userId + ", likes=" + likes + ", unlikes="
				+ unlikes + ", read=" + read + "]";
	}

	public static Key createKey(long userId, long messageId) {
		return KeyFactory.createKey(VoUserMessage.class.getSimpleName(), "" + userId + "X" + messageId);
	}
}
