package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vmesteonline.be.jdo2.GeoLocation;
import com.vmesteonline.be.jdo2.VoMessage;
import com.vmesteonline.be.jdo2.VoUserGroup;
import com.vmesteonline.be.utils.VoHelper;

public class MessagesTree {

	static public class Filters extends GeoLocation {
		public Filters(long uid, VoUserGroup ug) {
			userId = uid;
			if (ug != null) {
				this.radius = ug.getRadius();
				setLatitude(ug.getLatitude());
				setLongitude(ug.getLongitude());
			}
		}

		public long userId;
		public int radius;

	}

	public MessagesTree(List<VoMessage> vomsgs) {
		msgs = vomsgs;
		items = new ArrayList<ItemPosition>();
	}

	public List<VoMessage> getTreeMessagesFirstLevel(Filters filters) throws InvalidOperation {
		this.filters = filters;
		parseLevel(getLevel(0), 0);

		List<VoMessage> retList = new ArrayList<VoMessage>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).level == 0) {
				VoMessage voMsg = getMessage(items.get(i).id);
				voMsg.setChildMessageNum(items.get(i).childMsgsNum);
				retList.add(voMsg);
			}
		}

		return retList;
	}

	// эта функция возращает все сообщения от parentId до ближайшего сообщения
	// 1-го уровня
	public List<VoMessage> getTreeMessagesAfter(long parentId, Filters filters) throws InvalidOperation {
		this.filters = filters;
		items.clear();
		parseLevel(getLevel(0), 0);

		List<VoMessage> lst = new ArrayList<>();
		boolean add = false;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).id == parentId) {
				add = true;
			} else if (add) {

				VoMessage voMsg = getMessage(items.get(i).id);
				if (voMsg.getParentId() == 0)
					break;

				voMsg.setVisibleOffset(items.get(i).level);
				voMsg.setChildMessageNum(items.get(i).childMsgsNum);
				lst.add(voMsg);

			}
		}

		return lst;
	}

	int getChildsNum(long msgId) {
		for (ItemPosition ip : items) {
			if (ip.id == msgId)
				return ip.childMsgsNum;
		}
		return 0;
	}

	boolean isFirstLevel(long id) {

		for (VoMessage voMsg : firstLevel) {
			if (voMsg.getId() == id)
				return true;
		}
		return false;
	}

	// эта функция возращает все сообщения от parentId до ближайшего сообщения
	// 1-го уровня

	boolean isVisibleMessage(VoMessage voMsg, long userId) {
		return voMsg.getRecipient() == 0 || voMsg.getRecipient() == userId || voMsg.getAuthorId().getId() == userId;
	}

	boolean isInGroup(VoMessage voMsg) {

		if (VoHelper.isInclude(voMsg, voMsg.getRadius(), filters))
			if (voMsg.getRadius() >= filters.radius)
				if (voMsg.getMinimunVisibleRadius() <= filters.radius)
					return true;
		return false;
	}

	private int parseLevel(List<VoMessage> levelMsgs, int level) {
		Collections.sort(levelMsgs, new ByCreateTimeComparator());
		int childsInSublevels = 0;
		for (VoMessage voMsg : levelMsgs) {
			if (isVisibleMessage(voMsg, filters.userId) && isInGroup(voMsg)) {
				ItemPosition ip = new ItemPosition(voMsg.getId(), voMsg.getParentId(), level);
				items.add(ip);
				List<VoMessage> nextLevel = getLevel(voMsg.getId());
				ip.childMsgsNum = parseLevel(nextLevel, level + 1);
				childsInSublevels += ip.childMsgsNum + 1;
			}
		}

		return childsInSublevels;
	}

	private List<VoMessage> getLevel(long parentId) {
		List<VoMessage> l = new ArrayList<VoMessage>();
		for (VoMessage m : msgs) {
			if (m.getParentId() == parentId)
				l.add(m);
		}
		return l;
	}

	private VoMessage getMessage(long id) throws InvalidOperation {
		for (VoMessage m : msgs) {
			if (m.getId() == id)
				return m;
		}
		throw new InvalidOperation(VoError.GeneralError, "can't find message by tree representation");
	}

	public static class ByCreateTimeComparator implements Comparator<VoMessage> {

		@Override
		public int compare(VoMessage a, VoMessage b) {
			return a.getCreatedAt() < b.getCreatedAt() ? 0 : 1;
		}
	}

	class ItemPosition {
		public ItemPosition(long id, long parentId, int level) {
			this.id = id;
			this.parentId = parentId;
			this.level = level;
		}

		public int childMsgsNum;
		public long id;
		public long parentId;
		public int level;

	}

	private List<VoMessage> firstLevel;

	protected MessagesTree() {
	}

	protected List<ItemPosition> items;
	protected List<VoMessage> msgs;

	Filters filters;
}
