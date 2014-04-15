package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.Rubric;

@PersistenceCapable
public class VoRubric {

	public VoRubric(String visibleName, String name, String description, boolean defSubscribed) {
		this.name = name;
		this.visibleName = visibleName;
		this.description = description;
		this.subscribedByDefault = defSubscribed;
	}
	public Rubric createRubric(){
			Rubric rubric = new Rubric();
			rubric.setId(id.getId());
			rubric.setVisibleName(visibleName);
			rubric.setDescription(description);
			return rubric;
	}
	
	public VoRubric(String visibleName, String name, String description) {
		this(visibleName, name, description,false); 
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

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private boolean subscribedByDefault;
	
	@Persistent
	@Unindexed
	String visibleName;

	@Persistent
	@Unindexed
	String description;

	@Persistent
	@Unindexed
	String name;

	@Override
	public String toString() {
		return "VoRubric [id=" + id + ", subscribedByDefault=" + subscribedByDefault + ", visibleName=" + visibleName + ", name=" + name + "]";
	}
}
