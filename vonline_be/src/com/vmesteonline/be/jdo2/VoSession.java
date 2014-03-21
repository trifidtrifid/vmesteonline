package com.vmesteonline.be.jdo2;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.CurrentAttributeType;

@PersistenceCapable(detachable = "true")
// extends GeoLocation
public class VoSession {

	public VoSession(String sessId, VoUser user) {

		// this.id = KeyFactory.createKey(VoSession.class.getSimpleName(), sessId);
		id = sessId;
		setUser(user);
		curAttrMap = new HashMap<Integer, Long>();
	}

	public void setUser(VoUser user) {
		if (null != user) {
			setName(user.getName());
			setLastName(user.getLastName());
			setUserId(user.getId());
		} else {
			setName("");
			setLastName("Гость");
			setName("");
		}
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
	protected String id;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private String lastName;

	@Persistent
	private Long userId;

	@Persistent
	@Unindexed
	private int lastActivityTs;

	@Persistent
	@Unindexed
	private int lastUpdateTs;

	@Persistent
	@Unindexed
	private Map<Integer, Long> curAttrMap;

	public int getLastUpdateTs() {
		return lastUpdateTs;
	}

	public void setLastUpdateTs(int lastUpdateTs) {
		this.lastUpdateTs = lastUpdateTs;
	}

	public int getLastActivityTs() {
		return lastActivityTs;
	}

	public void setLastActivityTs(int lastActivityTs) {
		this.lastActivityTs = lastActivityTs;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public long getSessionAttribute(CurrentAttributeType type) {
		Long val = curAttrMap.get(type.getValue());
		return val == null ? 0 : val;
	}

	public void setSessionAttribute(int key, long value) {
		if(null==curAttrMap) curAttrMap = new HashMap<Integer, Long>();
		curAttrMap.put(key, value);
	}

	public void setSessionAttributes(Map<Integer, Long> newAttrMap) {
		curAttrMap.putAll(newAttrMap);
	}

	public Map<Integer, Long> getSessionAttributes() {
		return curAttrMap;

	}

	@Override
	public String toString() {
		return "VoSession [id=" + id + ", name=" + name + ", lastName=" + lastName + ", userId=" + userId + ", lastActivityTs=" + lastActivityTs
				+ ", lastUpdateTs=" + lastUpdateTs + "]";
	}
}

// ", longitude=" + getLongitude()+ ", latitude=" + getLatitude() + 