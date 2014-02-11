package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.Group;
import com.vmesteonline.be.jdo2.postaladdress.VoBuilding;

@PersistenceCapable
public class VoUserGroup implements Comparable<VoUserGroup> {

	public VoUserGroup(VoUser user, VoGroup grp) {
		longitude = user.getHomeGroup().longitude;
		latitude = user.getHomeGroup().latitude;
		radius = grp.getRadius();
		name = grp.getVisibleName();
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public float getLongitudeDelta() {
		return (float) ((radius / (R * Math.cos(Math.PI * latitude / 180))) * (180.0 / Math.PI));
	}

	public float getLatitudeDelta() {
		return (float) ((radius / R) * (180.0 / Math.PI));
	}

	public Group createGroup() {
		return new Group(getId().getId(), name, name, description, radius);
	}

	public VoUserGroup(VoGroup grp, float longitude, float lattitude) {
		this.radius = grp.getRadius();
		this.longitude = longitude;
		this.latitude = lattitude;
		this.name = grp.getVisibleName();
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
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

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Unindexed
	private String description;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private Float longitude;

	@Persistent
	@Unindexed
	private Float latitude;

	@Persistent
	@Unindexed
	private int radius;

	@Override
	public String toString() {
		return "VoUserGroup [id=" + id + ", name=" + name + ", longitude=" + longitude + ", latitude=" + latitude + ", radius=" + radius + "]";
	}

	@Override
	public int compareTo(VoUserGroup that) {
		return Float.compare(that.latitude, this.latitude) != 0 ? Float.compare(that.latitude, this.latitude) : Float.compare(that.longitude,
				this.longitude) != 0 ? Float.compare(that.longitude, this.longitude) : Integer.compare(that.radius, this.radius);
	}

	private static int R = 6378137;
}
