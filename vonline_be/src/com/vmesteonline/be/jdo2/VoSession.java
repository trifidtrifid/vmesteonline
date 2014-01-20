package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VoSession {
 
	public VoSession(String sessId, VoUser user) {
		this.id = sessId;
		this.name = user.getName();
		this.lastName = user.getLastName();
		this.userId = user.getId();
	}
	
	public void setId(String s) {
		id = s;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private String id;

	
	@Persistent
	private String name;

	@Persistent
	private String lastName;

	@Persistent
	private Long userId;

	@Persistent
	private float longitude;
	
	@Persistent
	private float latitude;

	@Persistent
	private int lastActivityTs;

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

	public int getLastActivityTs() {
		return lastActivityTs;
	}

	public void setLastActivityTs(int lastActivityTs) {
		this.lastActivityTs = lastActivityTs;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
