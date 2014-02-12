package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.PersistenceCapable;

@PersistenceCapable
public class VoUserTopic extends VoUserObject {

	public VoUserTopic() {
	}

	public VoUserTopic(VoUser user, VoTopic topic, boolean likes, boolean unlikes, boolean read) {
		super(user.getId(), topic.getId().getId(), likes, unlikes, read);
	}

	public VoUserTopic(long userId, long TopicId) {
		super(userId, TopicId);
	}
}
