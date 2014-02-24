package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.vmesteonline.be.jdo2.VoMessage;

public class MessagesTree {

	public MessagesTree(List<VoMessage> vomsgs) {
		msgs = vomsgs;
		items = new ArrayList<ItemPosition>();
		firstLevel = getLevel(0);
		parseLevel(firstLevel, 0);
	}

	private List<VoMessage> firstLevel;

	// эта функция возращает все сообщения от parentId до ближайшего сообщения
	// 1-го уровня

	public List<VoMessage> getTreeMessagesAfter(long parentId, long userId) throws InvalidOperation {

		List<VoMessage> lst = new ArrayList<>();
		boolean add = false;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).id == parentId) {
				add = true;
			} else if (add) {

				VoMessage voMsg = getMessage(items.get(i).id);
				if (voMsg.getParentId() == 0)
					break;

				if (voMsg.getRecipient() == 0 || voMsg.getRecipient() == userId || voMsg.getAuthorId().getId() == userId) {
					voMsg.setVisibleOffset(items.get(i).level);
					lst.add(voMsg);
				}

			}
		}

		for (int i = 0; i < lst.size(); i++) {
			lst.get(i).setChildMessageNum(getChildsNum(lst, i, lst.get(i).getVisibleOffset()));
		}

		return lst;
	}

	int getChildsNum(List<VoMessage> lst, int currentIndex, int currentLevel) {
		int childMsgsNum = 0;
		for (int i = currentIndex + 1; i < lst.size(); i++) {
			if (lst.get(i).getVisibleOffset() > currentLevel)
				childMsgsNum++;
			else
				break;
		}

		return childMsgsNum;
	}

	boolean isFirstLevel(long id) {

		for (VoMessage voMsg : firstLevel) {
			if (voMsg.getId().getId() == id)
				return true;
		}
		return false;
	}

	private void parseLevel(List<VoMessage> levelMsgs, int level) {
		Collections.sort(levelMsgs, new ByCreateTimeComparator());
		for (VoMessage voMsg : levelMsgs) {
			items.add(new ItemPosition(voMsg.getId().getId(), 0, level));
			List<VoMessage> nextLevel = getLevel(voMsg.getId().getId());
			parseLevel(nextLevel, level + 1);
		}
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
			if (m.getId().getId() == id)
				return m;
		}
		throw new InvalidOperation(VoError.GeneralError, "can't find message by tree representation");
	}

	class ByCreateTimeComparator implements Comparator<VoMessage> {

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

		public long id;
		public long parentId;
		public int level;

	}

	protected MessagesTree() {
	}

	protected List<ItemPosition> items;
	protected List<VoMessage> msgs;
}
