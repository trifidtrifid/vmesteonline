package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.PersistenceCapable;

import com.vmesteonline.be.UserTopic;

@PersistenceCapable
public class VoUserTopic extends VoUserObject {

	public VoUserTopic() {
	}

	public VoUserTopic(VoUser user, VoTopic topic, boolean likes, boolean unlikes, boolean read) {
		super(user.getId(), topic.getId(), likes, unlikes, read);
	}

	public VoUserTopic(long userId, long TopicId) {
		super(userId, TopicId);
	}

	public UserTopic getUserTopic() {
		return new UserTopic(false, isUnlikes(), isLikes(), false, 0, 0, isRead());
	}
	
	
	public int getMessagesCount() {
		return messagesCount;
	}

	public void setMessagesCount(int messagesCount) {
		this.messagesCount = messagesCount;
	}

	public long getLastUpdateMessageCount() {
		return lastUpdateMessageCount;
	}

	public void setLastUpdateMessageCount(long lastUpdateMessageCount) {
		this.lastUpdateMessageCount = lastUpdateMessageCount;
	}


	int messagesCount;
	long lastUpdateMessageCount;
}
