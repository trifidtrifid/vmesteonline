package com.vmesteonline.be.jdo2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.messageservice.Attach;
import com.vmesteonline.be.messageservice.Message;
import com.vmesteonline.be.messageservice.Topic;

@PersistenceCapable
public class VoTopic extends VoBaseMessage {
	// id, message, messageNum, viewers, usersNum, lastUpdate, likes, unlikes,
	// rubricId
	public VoTopic(Topic topic) throws InvalidOperation, IOException {

		super(topic.getMessage());
		subject = topic.getSubject().getBytes();
		messageNum = 0;
		usersNum = 1;
		viewers = 1;
		rubricId = topic.getRubricId();
		userGroupId = topic.getMessage().getGroupId();
		lastUpdate = (int) (System.currentTimeMillis() / 1000);
	}

	public Topic getTopic(long userId, PersistenceManager pm) {

		List<Attach> imgs = new ArrayList<Attach>();
		if( null!=images ) for( Long farId : images ){
			VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, farId);
			imgs.add( att.getAttach() );
		}
		List<Attach> docs = new ArrayList<Attach>();
		if(null!=documents) for( Long farId : documents ){
			VoFileAccessRecord att = pm.getObjectById(VoFileAccessRecord.class, farId);
			docs.add( att.getAttach() );
		}
		
		Message msg = new Message(id.getId(), 0L, type, getId(), userGroupId, authorId.getId(), createdAt, editedAt, new String(content), getLikes(),
				0, links, null, null, 0, null, imgs, docs, isImportant(userId), isLiked(userId));

		Topic tpc = new Topic(getId(), new String(subject), msg, getMessageNum(), getViewers(), getUsersNum(), getLastUpdate(), getLikes(),
				0, null, null, null);

		if (pollId != 0) {
			VoPoll voPoll = pm.getObjectById(VoPoll.class, pollId);
			tpc.poll = voPoll.getPoll(userId);
		}
		return tpc;

	}

	public int getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(int messageNum) {
		this.messageNum = messageNum;
	}

	public int getViewers() {
		return viewers;
	}

	public void setViewers(int viewers) {
		this.viewers = viewers;
	}

	public int getUsersNum() {
		return usersNum;
	}

	public void setUsersNum(int usersNum) {
		this.usersNum = usersNum;
	}

	public int getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(int lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public long getRubricId() {
		return rubricId;
	}

	public Long getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}

	public void setRubricId(long rubricId) {
		this.rubricId = rubricId;
	}

	public Long getPollId() {
		return pollId;
	}

	public void setPollId(Long pollId) {
		this.pollId = pollId;
	}

	@Override
	public String toString() {
		return "VoTopic [id=" + id + ", message=" + content.toString() + ", messageNum=" + messageNum + "]";
	}

	@Persistent
	@Unindexed
	private int messageNum;

	@Persistent
	@Unindexed
	private int viewers;

	@Persistent
	@Unindexed
	private int usersNum;

	@Persistent
	@Unindexed
	private int lastUpdate;

	@Persistent
	@Unindexed
	private Long rubricId;

	@Persistent
	@Unindexed
	private Long userGroupId;

	@Persistent
	@Unindexed
	private long pollId;

	@Persistent
	@Unindexed
	protected byte[] subject;
	
	

}
