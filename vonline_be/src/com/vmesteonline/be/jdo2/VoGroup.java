package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VoGroup {

	public VoGroup(String visibleName, int radius) {
		this(visibleName,radius,false);
	}
	
	public VoGroup(String visibleName, int radius, boolean subscribedByDefault) {
		this.visibleName = visibleName;
		this.radius = radius;
		this.subscribedByDefault = subscribedByDefault;
	}

	public VoGroup clone() {
		VoGroup gr = new VoGroup(visibleName, radius);
		return gr;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public String getVisibleName() {
		return visibleName;
	}

	public void setVisibleName(String visibleName) {
		this.visibleName = visibleName;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isHome(){
		return radius==0;}
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Unindexed
	private String visibleName;

	@Persistent
	private int radius;
	
	@Persistent 
	private boolean subscribedByDefault;

}
