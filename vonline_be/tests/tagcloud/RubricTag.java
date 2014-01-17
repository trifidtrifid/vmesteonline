package tagcloud;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table="TAGS")
public class RubricTag extends CloudTag {

	public RubricTag(long tag, Long parent, String content) {
		super();
		this.parent = parent;
		this.content = content;
		this.setTag(tag);
	} 
//	@PrimaryKey
//	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//  private Key key;
	
	@Persistent
	private Long parent; 
	
	@Persistent
	private String content;
	
	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
