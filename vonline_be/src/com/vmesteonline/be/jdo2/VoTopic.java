package com.vmesteonline.be.jdo2;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Topic;
import com.vmesteonline.be.data.PMF;

@PersistenceCapable
public class VoTopic {
	// id, message, messageNum, viewers, usersNum, lastUpdate, likes, unlikes,
	// rubricId
	public VoTopic(Topic topic, boolean checkConsistacy, boolean updateLInkedObjects, boolean makePersistant) throws InvalidOperation {

		//childTreeList = new ArrayList<Pair<Long,Long>>();
		
		PersistenceManagerFactory pmf = PMF.get();
		PersistenceManager pm = pmf.getPersistenceManager();

		try {
			VoRubric rubric = pm.getObjectById(VoRubric.class, KeyFactory.createKey(VoRubric.class.getSimpleName(), topic.getRubricId()));
			if (null == rubric) {
				throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "No Rubric found by id=" + topic.getRubricId());
			}

			messageNum = 0;
			usersNum = 1;
			viewers = 1;
			lastUpdate = (int) (System.currentTimeMillis() / 1000);
			likesNum = 0;
			unlikesNum = 0; 

			lastUpdate = (int) (System.currentTimeMillis() / 1000);
			message = new VoMessage(topic.getMessage(), this);
			//the topic is made persistent in message constructor
			topic.setId(id.getId());
			topic.message.setId(message.getId().getId());

		} finally {
			pm.close();
		}
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public VoMessage getMessage() {
		return message;
	}

	public void setMessage(VoMessage message) {
		this.message = message;
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

	public void setRubricId(long rubricId) {
		this.rubricId = rubricId;
	}

	public void updateLikes(int likesDelta) {
		likesNum += likesDelta;
	}

	public void updateUnlikes(int unlikesDelta) {
		unlikesNum += unlikesDelta;
	}

	@Persistent(dependent = "true")
	private VoMessage message;

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
	private long rubricId;

	@Persistent
	@Unowned
	private VoUserTopic userTopic;

	/*@Persistent
	@Unindexed
	private long[] listRepresentationOfTree = new long[0];

	@Persistent
	@Unindexed
	@Embedded
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
				throw new InvalidOperation(com.vmesteonline.be.Error.IncorrectParametrs, "FAiled to rewrite the root message of topic "
						+ this.getId().getId() + " by message with ID=" + newMessageId);
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


	/**
	 * Method returns a list representation of tree that should be opened under
	 * the
	 * 
	 * @param msg
	 * @return list representation of tree
	 /
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
		if (parentId == parentChildPairsList.get(offset).getSecond()) {
			// stop recursion on the last leaf
			return offset + 1;
		} else
			return -1;
	}*/
}
