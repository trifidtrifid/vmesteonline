package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VoUserTopic {

	/*	1: bool archieved,
	2: bool unlikes,
	3: bool likes, 
	4: bool notIntrested, 
	5: i64 lastReadMessageId,
	6: i64 lastWroteMeessgeId*/
	
	//Key is a combination of int value of userId and topicId
	@Persistent
	@PrimaryKey
	private Key userTopic;
	
	@Persistent
	@Unindexed 
	private long userId;
	
	@Persistent
	@Unindexed 
	private long topicId;
	
	@Persistent
	@Unindexed
	private boolean archieved;
	
	@Persistent
	@Unindexed
	private boolean unlikes;
	
	@Persistent
	@Unindexed
	private boolean likes;
	
	@Persistent
	@Unindexed
	private long lastReadMessageId;
	
	@Persistent
	@Unindexed
	private long lastWroteMeessgeId;
	
	
	public VoUserTopic( VoUser user, VoTopic topic ){
		this(user.getId(),topic.getId().getId());
	}
	public VoUserTopic( long userId, long topicId ){
		this.userId = userId;
		this.topicId = topicId;
		userTopic = KeyFactory.createKey(VoUserTopic.class.getSimpleName(), userId << 32 + (topicId & 0xFFFFFFFFL));
	}
	
	public static Key getKeyForObject(long userId, long topicId){
		return KeyFactory.createKey(VoUserTopic.class.getSimpleName(), userId << 32 + (topicId & 0xFFFFFFFFL));
	}
	
	
	public long getTopicId() {
		return userTopic.getId() & 0xFFFFFFFFL;
	}

	public void setUserTopicId( long userId, long topicId ){
		userTopic = KeyFactory.createKey(VoUserTopic.class.getSimpleName(), userId << 32 + (topicId & 0xFFFFFFFFL));
	}

	public long getUserId() {
		return userTopic.getId() & 0xFFFFFFFF00000000L >> 32;
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
	@Override
	public String toString() {
		return "VoUserTopic [userTopic=" + userTopic + ", userId=" + userId + ", topicId=" + topicId + ", archieved=" + archieved + ", unlikes="
				+ unlikes + ", likes=" + likes + ", lastReadMessageId=" + lastReadMessageId + ", lastWroteMeessgeId=" + lastWroteMeessgeId + "]";
	}
	
	
}
