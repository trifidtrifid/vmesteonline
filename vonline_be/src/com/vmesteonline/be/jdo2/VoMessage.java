package com.vmesteonline.be.jdo2;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Message;
import com.vmesteonline.be.MessageType;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.data.VoDatastoreHelper;
import com.vmesteonline.be.utils.VoHelper;

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

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public VoMessage(Message msg) throws InvalidOperation {

		super(msg);
		this.topicId = msg.getTopicId();
		this.parentId = msg.getParentId();
		this.parentId = msg.getParentId();
		this.recipient = msg.getRecipientId();

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
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

			VoUserGroup ug = pm.getObjectById(VoUserGroup.class, msg.getGroupId());
			this.radius = ug.getRadius();

			VoTopic topic = pm.getObjectById(VoTopic.class, msg.getTopicId());
			setLongitude(topic.getLongitude());
			setLatitude(topic.getLatitude());

			try {
				/* CHeck the recipient */
				if (0 != msg.getRecipientId()) {
					VoDatastoreHelper.exist(VoUser.class, msg.getRecipientId(), pm);
					recipient = msg.getRecipientId();
				}

				VoUser author = pm.getObjectById(VoUser.class, msg.getAuthorId());
				if (null == author) {
					throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Author of Message not found by ID=" + msg.getAuthorId());
				}
				// TODO сделать проверку на права создания сообщений не зависящей от
				// наличия домашней группы.
				if (author.getLongitude().equals("0") || author.getLatitude().equals("0"))
					throw new InvalidOperation(com.vmesteonline.be.VoError.GeneralError, "User without HomeGroup must not create a message");

				author.incrementMessages(1);

				minimunVisibleRadius = VoHelper.findMinimumGroupRadius(topic, author);

				/*
				 * Check that all of linked messages exists and has type that is required
				 */
				this.links = new HashMap<MessageType, Long>();

				for (Entry<MessageType, Long> entry : msg.getLinkedMessages().entrySet()) {
					VoMessage linkedMsg = pm.getObjectById(VoMessage.class, entry.getValue());
					if (null == linkedMsg)
						throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Linked message not found by ID:" + entry.getValue());
					if (!entry.getKey().equals(linkedMsg.getType()))
						throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Linked message with ID:" + entry.getValue()
								+ " type missmatch. Stored type:" + linkedMsg.getType().name() + " but linked as:" + entry.getKey().name());
					links.put(entry.getKey(), entry.getValue());
				}

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
		} finally {
			pm.close();
		}
	}

	public Message getMessage() {
		return new Message(id.getId(), getParentId(), type, topicId, 0L, authorId.getId(), createdAt, editedAt, new String(content), getLikes(),
				getUnlikes(), links, tags, null, visibleOffset, null);
	}

	public long getApprovedId() {
		return approvedId;
	}

	public void setApprovedId(long approvedId) {
		this.approvedId = approvedId;
	}

	public int getRadius() {
		return radius;
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
	private int radius;

	@Persistent
	@Unindexed
	protected long recipient;

	@Persistent
	@Unindexed
	protected int minimunVisibleRadius;

	public int getMinimunVisibleRadius() {
		return minimunVisibleRadius;
	}

	@Persistent
	protected long topicId;

	@Persistent
	@Unindexed
	private long parentId;

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
		return "VoMessage [id=" + id + ", type=" + type + ", authorId=" + authorId + ", recipient=" + recipient + ", longitude="
				+ getLongitude().toPlainString() + ", latitude=" + getLatitude().toPlainString() + ", radius=" + radius + "]";
	}

}
