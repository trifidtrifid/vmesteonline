package tagcloud;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.DiscriminatorStrategy;



import javax.persistence.ManyToMany;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;


@PersistenceCapable(identityType = IdentityType.APPLICATION, table="TAGS")
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME) 
@Inheritance(customStrategy = "new-table")
public abstract class CloudTag {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    protected Key key;

	@Persistent
	@Unindexed
	private long tag;
	
	@Persistent
	@ManyToMany
	private Set<Message> msgs = new HashSet<Message>();
	
	public Set<Message> getMsgs() {
		return msgs;
	}

	public void setMsgs(Set<Message> msgs) {
		this.msgs = msgs;
	}
	
	public long getTag() {
		return tag;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public Key getKey() {
		return key;
	}
	
	public void addMessage(Message msg){
		msgs.add(msg);
	}
}
