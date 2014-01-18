package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VoUserGroup {

	public VoUserGroup(String visibleName, int radius) {
		this.visibleName = visibleName;
		this.radius = radius;
	}

	public VoUserGroup(VoGroup g) {
		this.visibleName = g.getVisibleName();
		this.latitude = g.getLatitude();
		this.longitude = g.getLongitude();
	}

	public VoUserGroup clone() {
		VoUserGroup g = new VoUserGroup(visibleName, radius);
		return g;
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

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String visibleName;

	@Persistent
	private int radius;
	@Persistent
	private float longitude;

	@Persistent
	private float latitude;

}
