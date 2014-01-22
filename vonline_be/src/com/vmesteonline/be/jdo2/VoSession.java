package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.datanucleus.annotations.Unindexed;

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
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private String lastName;

	@Persistent
	private Long userId;

	@Persistent
	private float longitude;
	
	@Persistent
	private float latitude;

	@Persistent
	@Unindexed
	private int lastActivityTs;

	@Persistent
	@Unindexed
	private int lastUpdateTs;

	public int getLastUpdateTs() {
		return lastUpdateTs;
	}

	public void setLastUpdateTs(int lastUpdateTs) {
		this.lastUpdateTs = lastUpdateTs;
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
