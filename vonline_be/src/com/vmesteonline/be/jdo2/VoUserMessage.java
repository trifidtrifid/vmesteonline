package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.PersistenceCapable;

import com.vmesteonline.be.messageservice.UserMessage;

@PersistenceCapable
public class VoUserMessage extends VoUserObject {

	public VoUserMessage() {
	}

	public VoUserMessage(VoUser user, VoMessage message, boolean likes, boolean unlikes, boolean read) {
		super(user.getId(), message.getId(), likes, unlikes, read);
	}

	public VoUserMessage(long userId, long messageId) {
		super(userId, messageId);
	}

	public UserMessage getUserMessage() {
		return new UserMessage(isRead(), isLikes(), isUnlikes());
	}
}
