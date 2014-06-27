package com.vmesteonline.be.jdo2;

import java.util.List;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.messageservice.Poll;

@PersistenceCapable
public class VoPoll {

	public VoPoll(Poll poll) {
		subject = poll.subject;
		names = poll.names;
		values = poll.values;
	}

	public Poll getPoll() {
		Poll poll = new Poll(getId(), names, values, subject);
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
		return id.getId();
	}

	public void setId(Key id) {
		this.id = id;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

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
