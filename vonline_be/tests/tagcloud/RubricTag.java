package tagcloud;

import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
//@Inheritance(strategy = InheritanceStrategy SUPERCLASS_TABLE)
public class RubricTag extends CloudTag {

	public RubricTag(long tag, Key parent, String content) {
		super();
		this.parent = parent;
		this.content = content;
		this.setTag(tag);
	} 

	@Persistent
	private Key parent;
	
	@Persistent
	private String content;

	public Key getParent() {
		return parent;
	}

	public void setParent(Key parent) {
		this.parent = parent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
