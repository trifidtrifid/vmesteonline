package com.vmesteonline.be;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.jdo2.VoMessage;

public class MessagesTree {

	public MessagesTree(List<VoMessage> vomsgs) {
		msgs = vomsgs;
		items = new ArrayList<ItemPosition>();
		List<VoMessage> firstLevel = getLevel(0);
		parseLevel(firstLevel, 0);
	}

//	public get
	
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
