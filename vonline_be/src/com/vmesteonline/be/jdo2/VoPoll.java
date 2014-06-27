package com.vmesteonline.be.jdo2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	public Poll getPoll() {
		Poll poll = new Poll(getId(), names, values, subject, false);
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
