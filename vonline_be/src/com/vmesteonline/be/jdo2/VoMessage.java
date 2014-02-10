package com.vmesteonline.be.jdo2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Message;
import com.vmesteonline.be.MessageType;
import com.vmesteonline.be.UserMessage;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.data.VoDatastoreHelper;

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
	 *          Message. if msg.id > 0, then an update would be processed, new
	 *          VoMessage would be created otherwise
	 * @param checkConsistency
	 *          - if set, all parameters would be checked for consistency
	 * @param updateLinkedCounters
	 *          - if set, counter of topic and author would be updated according
	 *          to the posted message parameters
	 * @throws InvalidOperation
	 *           if consistency check fails or other exception happens
	 */
	public VoMessage(Message msg) throws InvalidOperation {
		this(msg, null);
	}

	VoMessage(Message msg, VoTopic ownerTopic) throws InvalidOperation {

		this.childMessages = new TreeSet<VoMessage>();
		int now = (int) (System.currentTimeMillis() / 1000);

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		VoMessage parentMsg = null;
		try {
			long parentId = msg.getParentId();
			if (0 == parentId) {
				this.topic = ownerTopic;
			} else {
				try {
					parentMsg = pm.getObjectById(VoMessage.class, parentId);
					pm.retrieve(parentMsg);
					parentMsg.addChildMessage(this);
					this.topic = parentMsg.getTopic();

				} catch (JDOObjectNotFoundException e) {
					e.printStackTrace();
					throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "parent Message not found by ID=" + parentId);
				}
			}
			if (null == this.topic)
				throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Topic of parent Message not found");

			try {
				/* CHeck the group to post, or move the message to */

				// VoUserGroup voGroup = pm.getObjectById(VoUserGroup.class,
				// msg.getGroupId());
				/* CHeck the recipient */
				if (0 != msg.getRecipientId()) {
					VoDatastoreHelper.exist(VoUser.class, msg.getRecipientId(), pm);
					recipient = msg.getRecipientId();
				}

				/*
				 * message inserted to the second level, so list representation should
				 * be updated
				 */
				// this.topic.addChildMessage(msg.getParentId(), msg.getId());

				VoUser author = pm.getObjectById(VoUser.class, msg.getAuthorId());
				if (null == author) {
					throw new InvalidOperation(com.vmesteonline.be.VoError.IncorrectParametrs, "Author of Message not found by ID=" + msg.getAuthorId());
				}
				this.authorId = KeyFactory.createKey(VoUser.class.getSimpleName(), msg.getAuthorId());
				this.type = msg.getType();
				this.createdAt = msg.getCreated();

				if (null == author.getHomeGroup())
					throw new InvalidOperation(com.vmesteonline.be.VoError.GeneralError, "User without HomeGroup must not create a message");

				topic.setMessageNum(topic.getMessageNum() + 1);
				topic.setLastUpdate(now);
				author.incrementMessages(1);

				/*
				 * Check that all of linked messages exists and has type that is
				 * required
				 */
				this.tags = new HashMap<Long, String>();
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
				this.tags = msg.getTags();
				this.content = msg.getContent().getBytes();
				this.likesNum = 0;
				this.unlikesNum = 0;
				this.approvedId = msg.getApprovedBy();

				pm.makePersistent(topic);
				if (null != parentMsg)
					pm.makePersistent(parentMsg);
				pm.makePersistent(author);
				pm.makePersistent(this);

				this.userMessage = new VoUserMessage(author, this, false, false, true);
				this.idValue = this.id.getId();
				pm.makePersistent(this.userMessage);
				pm.makePersistent(this);

				msg.setId(this.id.getId());
				msg.setTopicId(topic.getId().getId());

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
		Key parentKey = id.getParent();
		return new Message(id.getId(), null == parentKey ? 0L : parentKey.getId(), type, getTopic().getId().getId(), 0L, authorId.getId(), createdAt,
				editedAt, new String(content), likesNum, unlikesNum, links, tags, new UserMessage(getUserMessage().isRead(), getUserMessage().isLikes(),
						getUserMessage().isUnlikes()));
	}

	/**
	 * Method returns child messages of one level below the current message. Set
	 * is limited by size parameter and shifted by order
	 * 
	 * @param offset
	 *          how many of childs should be skipped
	 * @param size
	 *          how big list should be returned
	 * @return set of childs
	 */
	public Set<Message> getDirectChildMessages(int offset, int size) {
		Set<Message> childs = new HashSet<Message>();
		int count = 0;
		for (VoMessage child : this.getChildMessages()) {
			if (count >= offset) {
				childs.add(child.getMessage());
				if (--size == 0)
					break;
			}
		}
		return childs;
	}

	/**
	 * Method returns a list representation of child tree f messages. The size of
	 * the tree limited by size parameter and offset messages are skipped.
	 * 
	 * @param setToFill
	 *          a set to fill list to
	 * @param offset
	 *          - skip first of messages
	 * @param size
	 *          - maximum size of list to return
	 * @return filled size
	 */
	public int getChildMessagesTree(Set<Message> setToFill, int offset, int size) {
		int count = 0;
		for (VoMessage child : this.getChildMessages()) {
			if (--size == 0)
				break;
			count += child.getChildMessagesTree(setToFill, offset - count, size);
			if (count > offset)
				setToFill.add(child.getMessage());
			count++;
		}
		return count - offset;
	}


	public Set<VoMessage> getChildMessages() {
		return childMessages;
	}

	public void addChildMessage(VoMessage childMsg) {
		childMessages.add(childMsg);
	}



	public long getApprovedId() {
		return approvedId;
	}

	public void setApprovedId(long approvedId) {
		this.approvedId = approvedId;
	}

	public void setTopic(VoTopic topic) {
		this.topic = topic;
	}

	public VoTopic getTopic() {
		return topic;
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

	public long getRecipient() {
		return recipient;
	}

	public VoUserMessage getUserMessage() {
		if (null == userMessage) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				javax.jdo.Query q = pm.newQuery(VoUserMessage.class);
				/*
				 * q.setFilter(arg0); q.setFilter("tag == tagId");
				 * q.declareParameters("long tagId");
				 */
			} finally {
				pm.close();
			}
		}
		return userMessage;
	}

	public void setUserMessage(VoUserMessage userMessage) {
		this.userMessage = userMessage;
	}

	@Persistent
	@Index
	private long idValue;


	@Persistent
	/*
	 * @Extensions({ @Extension(vendorName = "datanucleus", key =
	 * "cascade-update", value = "false"),
	 * 
	 * @Extension(vendorName = "datanucleus", key = "collection", value =
	 * "dependent-element") })
	 * 
	 * @Order(extensions = @Extension(vendorName = "datanucleus", key =
	 * "list-ordering", value = "createdAt asc"))
	 */
	@Unowned
	private Set<VoMessage> childMessages;




	@Persistent
	@Unindexed
	private long approvedId;

	@Persistent
	@Unowned
	private VoTopic topic;



	@Persistent
	private float longitude;
	@Persistent
	private float latitude;
	@Persistent
	private float radius;
	@Persistent
	@Unindexed
	protected long recipient;

	@Persistent
	@Unindexed
	@Unowned
	protected VoUserMessage userMessage;

	@Override
	public String toString() {
		return "VoMessage [id=" + id + ", idValue=" + idValue + ", type=" + type + ", authorId=" + authorId + ", recipient=" + recipient + ", longitude="
				+ longitude + ", latitude=" + latitude + ", radius=" + radius + "]";
	}

}
