package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VoGroup {

	public VoGroup(String visibleName, String name, String descriprion, float longitude, float latitude, int radius) {
		this.visibleName = visibleName;
		this.name = name;
		this.description = descriprion;
		this.longitude = longitude;
		this.latitude = latitude;
		this.radius = radius;
	}

	public VoGroup clone() {
		VoGroup gr = new VoGroup(visibleName, name, description, longitude, latitude, radius);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String visibleName;

	@Persistent
	private String name;

	@Persistent
	private String description;

	@Persistent
	private float longitude;

	@Persistent
	private float latitude;

	@Persistent
	private int radius;

}
