package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.PersistenceCapable;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VoUserMessage extends VoUserObject {

	public VoUserMessage() {
	}

	public VoUserMessage(VoUser user, VoMessage message, boolean likes, boolean unlikes, boolean read) {
		super(user.getId(), message.getId().getId(), likes, unlikes, read);
	}

	public VoUserMessage(long userId, long messageId) {
		super(userId, messageId);
	}

/*	public static Key getObjectKey(VoUser user, VoMessage message) {
		return VoUserObject.<VoUserMessage> getObjectKey(VoUserMessage.class, user.getId(), message.getId().getId());
	}
*/
}
