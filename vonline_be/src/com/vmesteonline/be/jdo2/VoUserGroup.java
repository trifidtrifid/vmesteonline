package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.vmesteonline.be.Group;

@PersistenceCapable
public class VoUserGroup extends GeoLocation implements Comparable<VoUserGroup> {

	public VoUserGroup(VoUser user, VoGroup grp) {
		setLongitude(user.getLongitude());
		setLatitude(user.getLatitude());
		radius = grp.getRadius();
		name = grp.getVisibleName();
	}

	public VoUserGroup(String visibleName, int radius, BigDecimal longitude, BigDecimal lattitude) {
		this.radius = radius;
		this.longitude = longitude.toPlainString();
		this.latitude = longitude.toPlainString();
		this.name = visibleName;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Group createGroup() {
		return new Group(getId(), name, name, description, radius);
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

	public BigDecimal getLongitude() {
		return new BigDecimal(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude.toPlainString();
	}

	@Persistent
	@Unindexed
	private String description;

	@Persistent
	@Unindexed
	private String name;

	@Persistent
	@Unindexed
	private int radius;

	@Override
	public String toString() {
		return "VoUserGroup [id=" + getId() + ", name=" + name + ", longitude=" + longitude + ", latitude=" + latitude + ", radius=" + radius + "]";
	}

	@Override
	public int compareTo(VoUserGroup that) {
		return that.latitude.compareTo(this.latitude) != 0 ? that.latitude.compareTo(this.latitude)
				: that.longitude.compareTo(this.longitude) != 0 ? that.longitude.compareTo(this.longitude) : Integer.compare(that.radius, this.radius);
	}

}
