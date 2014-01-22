package com.vmesteonline.be.jdo2;

import java.io.Serializable;
import java.util.StringTokenizer;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable(objectIdClass = VoUserMessage.PK.class)
public class VoUserMessage {

	/*
	 * 1: bool read, флаг прочитанности сообщения пользователем 
	 * 2: bool likes,
	 * 3: bool unlikes
	 */

	@PrimaryKey
	@Persistent
	private long userId;

	@Persistent
	@PrimaryKey
	private long messageId;

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

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessage(long messageId) {
		this.messageId = messageId;
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

	public static class PK implements Serializable {
		private static final long serialVersionUID = 1L;
		public long userId;
		public long messageId;

		public PK() {
		}

		public PK(String value) {
			StringTokenizer token = new StringTokenizer(value, "::");
			token.nextToken(); // className
			this.userId = Long.parseLong(token.nextToken());
			this.messageId = Long.parseLong(token.nextToken());
		}

		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof PK))
				return false;
			PK c = (PK) obj;
			return userId == c.userId && messageId == c.messageId;
		}

		public int hashCode() {
			return new Long(userId).hashCode() ^ new Long(messageId).hashCode();
		}

		public String toString() {
			return this.getClass().getName() + "::" + this.userId + "::" + this.messageId;
		}
	}

}
