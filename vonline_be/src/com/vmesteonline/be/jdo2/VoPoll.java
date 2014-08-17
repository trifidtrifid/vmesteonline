package com.vmesteonline.be.jdo2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.messageservice.Poll;

@PersistenceCapable
public class VoPoll {

	VoPoll() {
		alreadyPoll = new TreeSet<Long>();
	}

	public static VoPoll create(Poll poll) throws InvalidOperation {

		VoPoll voPoll = new VoPoll();
		if (poll.names == null || poll.names.size() == 0)
			throw new InvalidOperation(VoError.IncorrectParametrs, "poll item is zero length");

		voPoll.names = poll.names;

		if (poll.values != null && poll.values.size() == poll.names.size())
			voPoll.values = poll.values;
		else {
			voPoll.values = new ArrayList<Integer>(poll.names.size());
			for (int i = 0; i < voPoll.names.size(); i++) {
				voPoll.values.add(0);
			}
		}
		voPoll.subject = poll.subject;
		return voPoll;
	}

	public Poll getPoll(long userId) {
		Poll poll = new Poll(getId(), names, values, subject, isAlreadyPoll(userId));
		return poll;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public List<Integer> getValues() {
		return values;
	}

	public void setValues(List<Integer> values) {
		this.values = values;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void doPoll(long userId) {
		alreadyPoll.add(new Long(userId));
	}

	public boolean isAlreadyPoll(long userId) {
		return alreadyPoll.contains(new Long(userId));
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	@Unindexed
	private String subject;

	@Persistent
	@Unindexed
	private List<String> names;

	@Persistent
	@Unindexed
	private List<Integer> values;

	@Persistent
	@Unindexed
	private Set<Long> alreadyPoll;

}
