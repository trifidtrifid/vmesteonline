package com.vmesteonline.be.jdo2;

import java.util.List;
import java.util.Vector;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.Message;
import com.vmesteonline.be.utils.Pair;

@PersistenceCapable
public class VoTopic {
	// id, messageId, messageNum, viewers, usersNum, lastUpdate, likes, unlikes,
	// rubricId

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
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
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public int getUnlikes() {
		return unlikes;
	}

	public void setUnlikes(int unlikes) {
		this.unlikes = unlikes;
	}

	public long getRubricId() {
		return rubricId;
	}

	public void setRubricId(long rubricId) {
		this.rubricId = rubricId;
	}

	public void updateLikes(int likesDelta) {
		likes += likesDelta;
	}

	public void updateUnlikes(int unlikesDelta) {
		unlikes += unlikesDelta;
	}

	@Persistent
	@Unindexed
	private long messageId;

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
	private int likes;

	@Persistent
	@Unindexed
	private int unlikes;

	@Persistent
	@Unindexed
	private long rubricId;

	@Persistent
	@Unindexed
	private long[] listRepresentationOfTree;

	private final List<Pair<Long, Long>> childTreeList = new Vector<Pair<Long, Long>>(); // parent:child

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

	void addChildMessage(long parentId, long newMessageId) {
		synchronized (childTreeList) {
			while (true)
				try {
					childTreeList.wait();
					break;
				} catch (InterruptedException e) {
					continue;
				}
			if (0 == childTreeList.size()) {
				unpackListRepresentation();
			}

			int pos = getPosOfTheLastChildOf(parentId, 0, childTreeList);
			childTreeList.add(pos, new Pair<Long, Long>(parentId, newMessageId));
			packListRepresentation();
			childTreeList.notify();
		}
	}

	/**
	 * Method returns a list representation of tree that should be opened under the 
	 * @param msg
	 * @return list representation of tree
	 */
	public List<Pair<Long,Long>> getListRepresentationOfTreeUnder( Message msg ){
		List<Pair<Long,Long>> outList = new Vector<Pair<Long,Long>>();
		synchronized (childTreeList) {
			while (true)
				try {
					childTreeList.wait();
					break;
				} catch (InterruptedException e) {
					continue;
				}
			if( 0==childTreeList.size() ) unpackListRepresentation();
			int pos = 0;
			for( ; pos < childTreeList.size(); pos++){
				if( childTreeList.get(pos).getSecond() == msg.id ){
					pos++;
					break;
				} 
			}
			if( pos != childTreeList.size() ){
				for( ; pos < childTreeList.size() && msg.parentId !=childTreeList.get(pos).getFirst(); pos++){
					outList.add(childTreeList.get(pos));
				}
			}
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
	}
}
