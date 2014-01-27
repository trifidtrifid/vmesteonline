package com.vmesteonline.be.jdo2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Extensions;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import tagcloud.RubricTag;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Message;
import com.vmesteonline.be.MessageType;
import com.vmesteonline.be.UserMessage;
import com.vmesteonline.be.data.PMF;

/**
 * Created by brozer on 1/12/14.
 */
@PersistenceCapable
public class VoMessage {

	// id, (parent), type, createdAt, editedAt, approvedId, topicId, createdId,
	// content, likes, unlikes, recipient, longitude, latitude, radius,
	// community,TAGS,LINKS

	/**
	 * Construct VoMessage object from MEssage representation
	 * @param msg Message. if msg.id > 0, then an update would be processed, new VoMessage would be created otherwise
	 * @param checkConsistency - if set, all parameters would be checked for consistency  
	 * @param updateLinkedCounters - if set, counter of topic and author would be updated according to the posted message parameters
	 * @throws InvalidOperation if consistency check fails or other exception happens
	 */
	public VoMessage( Message msg, boolean checkConsistency, boolean updateLinkedCounters, boolean makePersistent) throws InvalidOperation {

		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();
		try {
			VoMessage parentMsg = null;
			VoUser author = null;
			VoUser recpt;
			VoTopic topic = null;
			VoMessage storedMsg;
			
			
			int now = (int)(System.currentTimeMillis() / 1000);
			/* CHeck the group to post, or move the message to */
			VoGroup group = pm.getObjectById(VoGroup.class, msg.getGroupId());
			if (null == group) {
				throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Group of Message not found by ID=" + msg.getGroupId());
			}
			/* CHeck the recipient */
			if (0 != msg.getRecipientId()) {
				recpt = pm.getObjectById(VoUser.class, msg.getRecipientId());
				if (null == recpt) {
					throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Recipient of Message not found by ID=" + msg.getRecipientId());
				}
			}
			
			if( msg.getId() > 0 ) { //update a stored message 
				
				storedMsg = pm.getObjectById(VoMessage.class, msg.getId());
				if( null == storedMsg) 
					throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Message not found by ID=" + msg.getId());
				
				/*Check if content changed, then update edit date*/
				if( !Arrays.equals(storedMsg.getContent(), msg.getContent().getBytes())){
					storedMsg.setEditedAt( 0==msg.getEdited() ? now : msg.getEdited() );
				}
				
				if( updateLinkedCounters ) { 
					topic = storedMsg.getTopic();
					if( null!=topic ) {
						topic.updateLikes( msg.getLikesNum() - storedMsg.getLikes() );
						topic.updateUnlikes( msg.getUnlikesNum() - storedMsg.getUnlikes() );
						topic.setLastUpdate(now);
					} else {
						throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "No topic found by id="+storedMsg.getTopic().getId()+" that stored in Message ID=" + msg.getId());
					}
					author = pm.getObjectById(VoUser.class, storedMsg.getAuthorId());
					if( null!=author) {
						author.updateLikes( msg.getLikesNum() - storedMsg.getLikes() );
						author.updateUnlikes( msg.getUnlikesNum() - storedMsg.getUnlikes() );
					} else {
						throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "No AUTHOR found by id="+storedMsg.getAuthorId()+" that stored in Message ID=" + msg.getId());
					}
				} 
				if( storedMsg.getTopic().getId().getId() !=  msg.getTopicId() || storedMsg.getAuthorId().getId() != msg.getAuthorId() ||
						storedMsg.recipient !=  msg.getRecipientId() || storedMsg.createdAt != msg.getCreated() || storedMsg.type != msg.getType()) 
					throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Parameters: topic, author, recipient, createdAt, type could not be changed!");
				
			} else { //create new message
				
				try {
					if (0 != msg.getParentId()) {
						parentMsg = pm.getObjectById(VoMessage.class, msg.getParentId());
						if (null == parentMsg) {
							throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "parent Message not found by ID=" + msg.getParentId());
						}
					}
					author = pm.getObjectById(VoUser.class, msg.getAuthorId());
					if (null == author) {
						throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Author of Message not found by ID=" + msg.getAuthorId());
					}
					
					if (0 != msg.getRecipientId()) {
						recpt = pm.getObjectById(VoUser.class, msg.getRecipientId());
						if (null == recpt) {
							throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Recipient of Message not found by ID=" + msg.getRecipientId());
						}
					}
					topic = parentMsg.getTopic();
					if (null == topic) {
						throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Topic of PArent Message not found");
					}
					
					/*message inserted to the second level, so list representation should be updated*/
					topic.addChildMessage(msg.getParentId(), msg.getId());
					
				} catch (InvalidOperation e) {
					throw e;
				} catch (Exception e2) {
					throw new InvalidOperation(com.vmesteonline.be.Error.GeneralError, "Failed to validate Message parameters:" + e2.getMessage());
				}

				storedMsg = this;
				//set parameters that could not be changed in update
				storedMsg.setTopic(topic);
				storedMsg.setType(parentMsg.getType());
				storedMsg.recipient =  msg.getRecipientId();
				storedMsg.createdAt = msg.getCreated();
				VoUserGroup homeGroup = author.getHomeGroup();
				if(null!=homeGroup) {
					storedMsg.latitude = homeGroup.getLatitude();
					storedMsg.longitude = homeGroup.getLongitude();
					storedMsg.radius = group.getRadius();
				} else {
					throw new InvalidOperation(com.vmesteonline.be.Error.GeneralError, "User without HomeGroup must not create a message");
				}
				storedMsg.type = msg.getType();
				if(updateLinkedCounters){
					topic.setMessageNum( topic.getMessageNum() + 1 );
					topic.setLastUpdate(now);
					author.incrementMessages(1);
				}
			}  
			if(checkConsistency) { /*Check that all of linked messages exists and has type that is required*/
				for( Entry<MessageType, Long> entry:  msg.getLinkedMessages().entrySet()){
					VoMessage linkedMsg = pm.getObjectById(VoMessage.class, entry.getValue());
					if( null == linkedMsg ) 
						throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "Linked message not found by ID:" + entry.getValue());  
					if(!entry.getKey().equals(linkedMsg.getType()))
						throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, 
								"Linked message with ID:" + entry.getValue() + " type missmatch. Stored type:" + linkedMsg.getType().name() + " but linked as:" + entry.getKey().name());
				}
			}
			
			//now update parameters that could be changed 
			storedMsg.setContent( msg.getContent().getBytes() );
			storedMsg.setLikes(msg.getLikesNum());
			storedMsg.setUnlikes(msg.getUnlikesNum());
			storedMsg.setApprovedId(msg.getApprovedBy());
			parentMsg.addChildMessage(storedMsg);
			try {
				if(makePersistent) 
					pm.makePersistent(storedMsg);
				if(updateLinkedCounters){
					pm.makePersistent(topic);
					pm.makePersistent(author);
					pm.makePersistent(parentMsg);
				}
			} catch (Exception e) {
				throw new InvalidOperation(com.vmesteonline.be.Error.GeneralError, "Failed to save the message ID="+msg.getId()+": Exception:" +e.getMessage());
			}
			if(this != storedMsg) { //update elements of this object according to results of update
				this.setTopic(storedMsg.topic);
				this.setId(storedMsg.id);
				this.setTags(storedMsg.tags);
				this.setLinks(storedMsg.links);
			}
		} finally {
			pm.close();
		} 
	}

	public Message getMessage() {
		Key parentKey = id.getParent();
		return new Message(id.getId(), null==parentKey ? 0L : parentKey.getId(), type,getTopic().getId().getId(), 0L, authorId.getId(), createdAt, editedAt, 
				new String( content ), likesNum, unlikesNum, links, tags, 
				new UserMessage(getUserMessage().isRead(), getUserMessage().isLikes(), getUserMessage().isUnlikes()));
	}
	/**
	 * Method returns child messages of one level below the current message. Set is limited by size parameter and shifted by order 
	 * @param offset how many of childs should be skipped
	 * @param size how big list should be returned
	 * @return set of childs
	 */
	public Set<Message> getDirectChildMessages( int offset, int size ){
		Set<Message> childs = new HashSet<Message>();
		int count = 0;
		for( VoMessage child: this.getChildMessages()){
			if( count >= offset ) {
				childs.add( child.getMessage());
				if( --size == 0 ) break;
			}
		}
		return childs;
	}
	/**
	 * Method returns a list representation of child tree f messages. The size of the tree limited by size parameter and offset messages are skipped.
	 * @param setToFill a set to fill list to
	 * @param offset - skip first of messages
	 * @param size - maximum size of list to return 
	 * @return filled size
	 */
	public int getChildMessagesTree( Set<Message> setToFill, int offset, int size ){
		int count = 0;
		for( VoMessage child: this.getChildMessages()){
			if( --size == 0 ) break;
			count += child.getChildMessagesTree( setToFill, offset-count, size );
			if( count > offset ) 
				setToFill.add( child.getMessage());
			count++;
		}
		return count - offset;
	}
	
	public int getEditedAt() {
		return editedAt;
	}

	public Set<VoMessage> getChildMessages() {return childMessages;}
	public void addChildMessage( VoMessage childMsg ) {
		childMessages.add(childMsg);
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
		return likesNum;
	}

	public void setLikes(int likes) {
		this.likesNum = likes;
	}

	public int decrementLikes(){
		return --likesNum;
	}
	public int incrementLikes(){
		return ++likesNum;
	}
	public int decrementUnlikes(){
		return --unlikesNum;
	}
	public int incrementUnlikes(){
		return ++unlikesNum;
	}
	public int getUnlikes() {
		return unlikesNum;
	}

	public void setUnlikes(int unlikes) {
		this.unlikesNum = unlikes;
	}

	public long getRecipient() {
		return recipient;
	}

	public void setRecipient(long recipient) {
		this.recipient = recipient;
	}

	public Map<Long, String> getTags() {
		return tags;
	}

	public void setTags(Map<Long, String> tags) {
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
	public void setId( Key id) {
		this.id = id;
	}
	public MessageType getType() {
		return type;
	}
	public void setType(MessageType type) {
		this.type = type;
	}

	public int getCreatedAt() {
		return createdAt;
	}

	public void setTopic( VoTopic topic ){
		this.topic = topic; 
	}
	
	public VoTopic getTopic() {
		return topic;
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
	
	

	public VoUserMessage getUserMessage() {
		if(null==userMessage){
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				Query q = pm.newQuery(VoUserMessage.class);
				/*q.setFilter(arg0);
				q.setFilter("tag == tagId");
				q.declareParameters("long tagId");*/
			} finally {
				pm.close();
			}
		}
		return userMessage;
	}

	public void setUserMessage(VoUserMessage userMessage) {
		this.userMessage = userMessage;
	}



	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Unindexed
	private MessageType type;

	@Persistent
	@Extensions( {@Extension(vendorName="datanucleus", key="cascade-update", value="false"), 
		@Extension(vendorName="datanucleus", key="collection", value="dependent-element")})
	@Order(extensions = @Extension(vendorName="datanucleus",key="list-ordering", value="createdAt asc"))
	private Set<VoMessage> childMessages;

	@Persistent
	@Unindexed
	private int createdAt;

	@Persistent
	@Unindexed
	private int editedAt;

	@Persistent
	@Unindexed
	private long approvedId;

	@Persistent/*(mappedBy = "message") // if uncomment - topic become parent of message*/
	private VoTopic topic;

	@Persistent
	private Key authorId;

	@Persistent
	@Unindexed
	private byte[] content;

	@Persistent
	@Unindexed
	private int likesNum;

	@Persistent
	@Unindexed
	private int unlikesNum;

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
	private Map<Long, String> tags;

	@Persistent
	@Unindexed
	private Map<MessageType, Long> links;
	
	@Persistent
	@Unindexed
	@Unowned
	private VoUserMessage userMessage;
	
}
