package tagcloud;

import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.persistence.ManyToMany;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Message {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	private long parent;
	
	
	@Persistent
	String content;
	
	@Persistent
	@ManyToMany
	Set<CloudTag> tags;

	public Message(long parent, String content, Set<CloudTag> tags) {
		super();
		this.parent = parent;
		this.content = content;
		this.tags = tags;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Set<CloudTag> getTags() {
		return tags;
	}

	public void setTags(Set<CloudTag> tags) {
		this.tags = tags;
	}
}
