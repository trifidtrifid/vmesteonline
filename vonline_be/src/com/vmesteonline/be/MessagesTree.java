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

	List<VoMessage> firstLevel;

	public List<VoMessage> getTreeMessagesAfter(long parentId, int length) throws InvalidOperation {

		List<VoMessage> lst = new ArrayList<>();
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).id == parentId) {
				int fromPos = i + 1;
				int toPos = i + 1 + length;
				if (fromPos >= items.size())
					break;
				if (toPos >= items.size()) {
					toPos = items.size();
				}
				List<ItemPosition> iList = items.subList(fromPos, toPos);
				for (ItemPosition iPos : iList) {

					if (isFromFirstlevel(iPos.id))
						break;

					VoMessage voMsg = getMessage(iPos.id);
					voMsg.setVisibleOffset(iPos.level);
					lst.add(voMsg);
				}
			}
		}

		return lst;
	}

	private boolean isFromFirstlevel(long id) {
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
