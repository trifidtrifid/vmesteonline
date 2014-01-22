package com.vmesteonline.be.jdo2;

import java.io.Serializable;
import java.util.StringTokenizer;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.jdo2.VoUserMessage.PK;

@PersistenceCapable(objectIdClass = VoUserTopic.PK.class)
public class VoUserTopic {

	/*	1: bool archieved,
	2: bool unlikes,
	3: bool likes, 
	4: bool notIntrested, 
	5: i64 lastReadMessageId,
	6: i64 lastWroteMeessgeId*/
	
	@Persistent
	@PrimaryKey
	private long topicId;
	
	@Persistent
	@PrimaryKey
	private long userId;
	
	@Persistent
	@Unindexed
	private boolean archieved;
	
	@Persistent
	@Unindexed
	private boolean unlikes;
	
	@Persistent
	@Unindexed
	private boolean likes;
	
	public long getTopicId() {
		return topicId;
	}


	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}


	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}


	public boolean isArchieved() {
		return archieved;
	}


	public void setArchieved(boolean archieved) {
		this.archieved = archieved;
	}


	public boolean isUnlikes() {
		return unlikes;
	}


	public void setUnlikes(boolean unlikes) {
		this.unlikes = unlikes;
	}


	public boolean isLikes() {
		return likes;
	}


	public void setLikes(boolean likes) {
		this.likes = likes;
	}


	public long getLastReadMessageId() {
		return lastReadMessageId;
	}


	public void setLastReadMessageId(long lastReadMessageId) {
		this.lastReadMessageId = lastReadMessageId;
	}


	public long getLastWroteMeessgeId() {
		return lastWroteMeessgeId;
	}


	public void setLastWroteMeessgeId(long lastWroteMeessgeId) {
		this.lastWroteMeessgeId = lastWroteMeessgeId;
	}


	@Persistent
	@Unindexed
	private long lastReadMessageId;
	
	@Persistent
	@Unindexed
	private long lastWroteMeessgeId;
	

	public static class PK implements Serializable {
		private static final long serialVersionUID = 1L;
		public long userId;
		public long topicId;

		public PK() {
		}

		public PK(String value) {
			StringTokenizer token = new StringTokenizer(value, "::");
			token.nextToken(); // className
			this.userId = Long.parseLong(token.nextToken());
			this.topicId = Long.parseLong(token.nextToken());
		}

		public boolean equals(Object obj) {
			if (obj == this)
				return true;
			if (!(obj instanceof PK))
				return false;
			PK c = (PK) obj;
			return userId == c.userId && topicId == c.topicId;
		}

		public int hashCode() {
			return new Long(userId).hashCode() ^ new Long(topicId).hashCode();
		}

		public String toString() {
			return this.getClass().getName() + "::" + this.userId + "::" + this.topicId;
		}
	}
}
