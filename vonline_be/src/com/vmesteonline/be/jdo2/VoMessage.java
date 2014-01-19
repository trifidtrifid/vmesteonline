package com.vmesteonline.be.jdo2;

import java.util.Map;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.MessageType;
import com.vmesteonline.be.data.PMF;

/**
 * Created by brozer on 1/12/14.
 */
@PersistenceCapable
public class VoMessage {

//id, (parent), type, createdAt, editedAt, approvedId, topicId, createdId, content, likes, unlikes, recipient, longitude, latitude, radius, community,TAGS,LINKS
	
	public VoMessage(com.vmesteonline.be.Message msg, boolean checkConsistency) {
		if (0 != msg.getParentId()) {

			PersistenceManagerFactory pmf = PMF.get();
			PersistenceManager pm = pmf.getPersistenceManager();
			try {
				// Key parentKey =
				// KeyFactory.createKey(VoMessage.class.getSimpleName(),
				// msg.getParentId());
				VoMessage parentMsg = pm.getObjectById(VoMessage.class,
						msg.getParentId());
			} catch (Exception e) {
				// throw new
			}
		}
	}
	
	

	public int getEditedAt() {
		return editedAt;
	}

	public void setEditedAt(int editedAt) {
		this.editedAt = editedAt;
	}

	public long getApprovedId() {
		return approvedId;
	}

	public void setApprovedId(long approvedId) {
		this.approvedId = approvedId;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public long getUnlikes() {
		return unlikes;
	}

	public void setUnlikes(long unlikes) {
		this.unlikes = unlikes;
	}

	public long getRecipient() {
		return recipient;
	}

	public void setRecipient(long recipient) {
		this.recipient = recipient;
	}

	public Map<Long,String> getTags() {
		return tags;
	}

	public void setTags(Map<Long,String> tags) {
		this.tags = tags;
	}

	public Map<MessageType, Long> getLinks() {
		return links;
	}

	public void setLinks(Map<MessageType, Long> links) {
		this.links = links;
	}

	public Key getId() {
		return id;
	}

	public MessageType getType() {
		return type;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public Key getTopicId() {
		return topicId;
	}

	public Key getAuthorId() {
		return authorId;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getRadius() {
		return radius;
	}

	public long getCommunity() {
		return community;
	}



	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Unindexed
	private MessageType type;
	
	@Persistent
	@Unindexed
	private int createdAt;
	
	@Persistent
	@Unindexed
	private int editedAt;
	
	@Persistent
	@Unindexed
	private long approvedId;
	
	@Persistent
	private Key topicId;
	
	@Persistent
	private Key authorId;
	
	@Persistent
	@Unindexed
	private byte[] content;
	
	@Persistent
	@Unindexed
	private int likes;
	
	@Persistent
	@Unindexed
	private long unlikes;
	
	@Persistent
	@Unindexed
	private long recipient;
	
	@Persistent
	private float longitude;
	@Persistent
	private float latitude;
	@Persistent
	private float radius;
	@Persistent
	private long community;
	
	@Persistent
	private Map<Long,String> tags;
	
	@Persistent
	@Unindexed
	private Map<MessageType,Long> links;
}
