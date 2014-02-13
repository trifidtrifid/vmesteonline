package com.vmesteonline.be.jdo2;

import java.util.List;
import java.util.Vector;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Message;
import com.vmesteonline.be.Topic;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.utils.Pair;

@PersistenceCapable
public class VoTopic extends VoBaseMessage {
	// id, message, messageNum, viewers, usersNum, lastUpdate, likes, unlikes,
	// rubricId
	public VoTopic(Topic topic, boolean checkConsistacy, boolean updateLInkedObjects, boolean makePersistant) throws InvalidOperation {

		super(topic.getMessage());
		subject = topic.getSubject().getBytes();
		messageNum = 0;
		usersNum = 1;
		viewers = 1;
		likesNum = 0;
		unlikesNum = 0;
		rubricId = topic.getRubricId();
		userGroupId = topic.getMessage().getGroupId();
		lastUpdate = (int) (System.currentTimeMillis() / 1000);

	}

	public int getLikesNum() {
		return likesNum;
	}

	public void setLikesNum(int likesNum) {
		this.likesNum = likesNum;
	}

	public int getUnlikesNum() {
		return unlikesNum;
	}

	public void setUnlikesNum(int unlikesNum) {
		this.unlikesNum = unlikesNum;
	}

	public Topic getTopic() {

		Message msg = new Message(id.getId(), 0L, type, getId().getId(), 0L, authorId.getId(), createdAt, editedAt, new String(content), likesNum,
				unlikesNum, links, tags, null, 0, null);

		return new Topic(getId().getId(), new String(subject), msg, getMessageNum(), getViewers(), getUsersNum(), getLastUpdate(), getLikes(), getUnlikes(), null, null);
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

	public int getLikes() {
		return likesNum;
	}

	public void setLikes(int likes) {
		this.likesNum = likes;
	}

	public int getUnlikes() {
		return unlikesNum;
	}

	public void setUnlikes(int unlikes) {
		this.unlikesNum = unlikes;
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

	public void updateLikes(int likesDelta) {
		likesNum += likesDelta;
	}

	public void updateUnlikes(int unlikesDelta) {
		unlikesNum += unlikesDelta;
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
	private int lastUpdate;

	@Persistent
	@Unindexed
	private int likesNum;

	@Persistent
	@Unindexed
	private int unlikesNum;

	@Persistent
	private Long rubricId;

	@Persistent
	private Long userGroupId;

	@Persistent
	@Unindexed
	private long[] listRepresentationOfTree = new long[0];

	@Persistent
	@Unindexed
	protected byte[] subject;

	@NotPersistent
	private List<Pair<Long, Long>> childTreeList; // parent:child

	private void packListRepresentation() {
		listRepresentationOfTree = new long[childTreeList.size() * 2];
		for (int ptr = 0; ptr < childTreeList.size(); ptr++) {
			listRepresentationOfTree[ptr * 2] = childTreeList.get(ptr).left;
			listRepresentationOfTree[ptr * 2 + 1] = childTreeList.get(ptr).right;
		}
	}
	
	private void unpackListRepresentation() {
		for (int ptr = 0; ptr < listRepresentationOfTree.length / 2; ptr++)
			childTreeList.add(new Pair<Long, Long>(listRepresentationOfTree[ptr * 2], listRepresentationOfTree[ptr * 2 + 1]));
	}

	void addChildMessage(long parentId, long newMessageId) throws InvalidOperation {
		if (0 == parentId) { // root message of the topic
			if (!childTreeList.isEmpty() && childTreeList.get(0).right != newMessageId) {
				throw new InvalidOperation(VoError.IncorrectParametrs, "FAiled to rewrite the root message of topic " + this.getId().getId()
						+ " by message with ID=" + newMessageId);
			}
			childTreeList.add(new Pair<Long, Long>(0L, newMessageId));
			packListRepresentation();
		} else {
			synchronized (childTreeList) {
				if (0 == childTreeList.size()) {
					unpackListRepresentation();
				}

				int pos = getPosOfTheLastChildOf(parentId, 0, childTreeList);
				childTreeList.add(pos, new Pair<Long, Long>(parentId, newMessageId));
				packListRepresentation();
				childTreeList.notify();
			}
		}

	}

	/*
	 * Method returns a list representation of tree that should be opened under
	 * the
	 * 
	 * @param msg
	 * 
	 * @return list representation of tree /
	 */
	
	public List<Pair<Long, Long>> getListRepresentationOfTreeUnder(Message msg) {
		List<Pair<Long, Long>> outList = new Vector<Pair<Long, Long>>();
		synchronized (childTreeList) {
			if (0 == childTreeList.size())
				unpackListRepresentation();
			int pos = 0;
			for (; pos < childTreeList.size(); pos++) {
				if (childTreeList.get(pos).getSecond() == msg.id) {
					pos++;
					break;
				}
			}
			if (pos != childTreeList.size()) {
				for (; pos < childTreeList.size() && msg.parentId != childTreeList.get(pos).getFirst(); pos++) {
					outList.add(childTreeList.get(pos));
				}
			}
			childTreeList.notify();
		}
		return outList;
	}

	public static int getPosOfTheLastChildOf(long parentId, int offset, List<Pair<Long, Long>> parentChildPairsList) {

		for (int pairNo = parentChildPairsList.size() - 1; offset <= pairNo; pairNo--) {
			if (parentId == parentChildPairsList.get(pairNo).getFirst()) {
				return getPosOfTheLastChildOf(parentChildPairsList.get(pairNo).getSecond(), pairNo, parentChildPairsList);
			} else if (parentId == parentChildPairsList.get(pairNo).getSecond()) {
				return pairNo + 1;
			}
		}
		if (parentId == parentChildPairsList.get(offset).getSecond()) { // stop
																																		// recursion
																																		// on the
																																		// last leaf
			return offset + 1;
		} else
			return -1;
	}
}
