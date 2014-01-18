package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VoRubric {

	public VoRubric(String visibleName, String name, String description) {
		this.name = name;
		this.visibleName = visibleName;
		this.description = description;
	}

	public VoRubric clone() {
		VoRubric r = new VoRubric(visibleName, name, description);
		return r;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public void setVisibleName(String visibleName) {
		this.visibleName = visibleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	String visibleName;

	@Persistent
	String description;

	@Persistent
	String name;

}
