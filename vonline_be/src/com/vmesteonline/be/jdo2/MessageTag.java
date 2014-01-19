package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, table = "TAGS")
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@Inheritance(customStrategy = "new-table")
public abstract class MessageTag {

	public MessageTag(long tag) {
		this.tag = tag;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key key;

	@Persistent
	private long tag;

	public long getTag() {
		return tag;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public Key getKey() {
		return key;
	}
}