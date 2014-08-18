package com.vmesteonline.be.jdo2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.data.VoDatastoreHelper;
import com.vmesteonline.be.messageservice.Attach;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.MessageType;

/**
 * Created by brozer on 1/12/14.
 */
@PersistenceCapable
public class VoMessage extends VoBaseMessage {

	// private static final Logger logger = Logger.getLogger(VoMessage.class);
	// id, (parent), type, createdAt, editedAt, approvedId, topicId, createdId,
	// content, likes, unlikes, recipient, longitude, latitude, radius,
	// community,TAGS,LINKS

	/**
	 * Construct VoMessage object from MEssage representation
	 * 
	 * @param msg
	 *          Message. if msg.id > 0, then an update would be processed, new VoMessage would be created otherwise
	 * @param checkConsistency
	 *          - if set, all parameters would be checked for consistency
	 * @param updateLinkedCounters
	 *          - if set, counter of topic and author would be updated according to the posted message parameters
	 * @throws InvalidOperation
	 *           if consistency check fails or other exception happens
	 */

	public VoMessage() {
	}

	public VoMessage(Message msg, MessageType type) {
		this.topicId = msg.getTopicId();
		// todo shoud be long
		if (msg.getAuthorId() != 0)
			this.authorId = KeyFactory.createKey(VoUser.class.getSimpleName(), msg.getAuthorId());

		this.userNameForBlog = msg.getAnonName();
		this.content = msg.getContent();
		createdAt = msg.getCreated();
		images = new ArrayList<Long>();
		documents = new ArrayList<Long>();

	}

	// TODO do smthing with this. constructor should not be like this. create factory or smth else
	public VoMessage(Message msg, PersistenceManager pm) throws InvalidOperation, IOException {

		super(msg, pm);
		this.topicId = msg.getTopicId();
		this.parentId = msg.getParentId();
		this.recipient = msg.getRecipientId();

		
		if (0 != msg.getParentId()) {
			try {
				VoMessage parentMsg = pm.getObjectById(VoMessage.class, msg.getParentId());
				pm.retrieve(parentMsg);
				pm.makePersistent(parentMsg);
			} catch (JDOObjectNotFoundException e) {
				e.printStackTrace();
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "parent Message not found by ID=" + msg.getParentId());
			}
		}

		VoTopic topic = pm.getObjectById(VoTopic.class, msg.getTopicId());
	
		// вставка времени последнего апдейта
		topic.setLastUpdate((int) (System.currentTimeMillis() / 1000L));

		try {
			/* CHeck the recipient */
			if (0 != msg.getRecipientId()) {
				VoDatastoreHelper.exist(VoUser.class, msg.getRecipientId(), pm);
				recipient = msg.getRecipientId();
			}

			VoUser author = pm.getObjectById(VoUser.class, msg.getAuthorId());
			author.incrementMessages(1);;
			this.approvedId = msg.getApprovedBy();

			pm.makePersistent(author);
			pm.makePersistent(this);

			msg.setId(this.id.getId());

		} catch (InvalidOperation e) {
			throw e;
		} catch (Exception e2) {
			e2.printStackTrace();
			throw new InvalidOperation(com.vmesteonline.be.VoError.GeneralError, "Failed to validate Message parameters:" + e2.getMessage());
		}
	}

	public boolean isVisibleFor(long userId) {
		return getRecipient() == 0 || getRecipient() == userId || getAuthorId().getId() == userId;
	}

	public Message getMessage(long userId, PersistenceManager pm) {

		List<Attach> imgs = new ArrayList<Attach>();
		for (Long farId : images) {
			VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, farId);
			imgs.add(att.getAttach());
		}
		List<Attach> docs = new ArrayList<Attach>();
		for (Long farId : documents) {
			VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, farId);
			docs.add(att.getAttach());
		}

		if (authorId == null)
			return new Message(id.getId(), getParentId(), type, topicId, 0L, 0, createdAt, editedAt, new String(content), getLikes(), 0, links, null, null,
					visibleOffset, null, imgs, docs, userNameForBlog, isImportant(userId), isLiked(userId));
		else
			return new Message(id.getId(), getParentId(), type, topicId, 0L, authorId.getId(), createdAt, editedAt, new String(content), getLikes(), 0,
					links, null, null, visibleOffset, null, imgs, docs, userNameForBlog, isImportant(userId), isLiked(userId));
	}

	public long getApprovedId() {
		return approvedId;
	}

	public void setApprovedId(long approvedId) {
		this.approvedId = approvedId;
	}

	public long getRecipient() {
		return recipient;
	}

	public void setRecipient(long recipient) {
		this.recipient = recipient;
	}

	@Persistent
	@Unindexed
	private long approvedId;

	@Persistent
	@Unindexed
	protected long recipient;

	@Persistent
	@Unindexed
	protected int minimunVisibleRadius;

	@Persistent
	protected int score;

	public int getMinimunVisibleRadius() {
		return minimunVisibleRadius;
	}

	@Persistent
	protected long topicId;

	@Persistent
	@Unindexed
	private long parentId;

	@Persistent
	@Unindexed
	private String userNameForBlog;

	protected int visibleOffset;

	public int getVisibleOffset() {
		return visibleOffset;
	}

	public void setVisibleOffset(int visibleOffset) {
		this.visibleOffset = visibleOffset;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public long getTopicId() {
		return topicId;
	}

	public void setTopicId(long topicId) {
		this.topicId = topicId;
	}

	@Override
	public String toString() {
		return "VoMessage [id=" + id + ", type=" + type + ", authorId=" + authorId + ", recipient=" + recipient + "]";
	}

	
	public static class ComparatorByCreateDate implements Comparator<VoMessage> {

		@Override
		public int compare(VoMessage o1, VoMessage o2) {
			return Integer.compare(o1.getCreatedAt(), o2.getCreatedAt());
		}

	}
}
