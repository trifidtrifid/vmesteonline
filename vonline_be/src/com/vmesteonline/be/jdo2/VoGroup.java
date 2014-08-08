package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.GroupType;

@PersistenceCapable
public class VoGroup implements Comparable<VoGroup> {

	public static final int RADIUS_FOR_UNKNOWNS = 2000000; // whole world

	public VoGroup(String visibleName, int radius, GroupType gType) {
		this(visibleName, radius, gType, false);
	}

	public VoGroup(String visibleName, int radius, GroupType gType, boolean subscribedByDefault) {
		this.visibleName = visibleName;
		this.radius = radius;
		this.subscribedByDefault = subscribedByDefault;
		this.groupType = gType.getValue();
	}

	public VoGroup clone() {
		VoGroup gr = new VoGroup(visibleName, radius, GroupType.findByValue(groupType));
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

	public boolean isHome() {
		return radius == 0;
	}

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
	
	@Persistent
	@Unindexed
	private int importantScore;

	
	public int getImportantScore() {
		return importantScore;
	}

	public void setImportantScore(int importantScore) {
		this.importantScore = importantScore;
	}
	
	@Persistent
	@Unindexed
	private int groupType;
	
	public int getGroupType() {
		return groupType;
	}

	public void setGroupType(int groupType) {
		this.groupType = groupType;
	}

	@Override
	public String toString() {
		return "VoGroup [id=" + id + ", visibleName=" + visibleName + ", radius=" + radius + ", subscribedByDefault=" + subscribedByDefault + "]";
	}

	@Override
	public int compareTo(VoGroup o) {
		return Integer.compare(o.radius, radius);
	}

}
