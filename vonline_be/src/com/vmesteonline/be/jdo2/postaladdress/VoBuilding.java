package com.vmesteonline.be.jdo2.postaladdress;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.Building;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.VoUser;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoBuilding implements Comparable<VoBuilding> {

	public VoBuilding(VoStreet vs, String fullNo, BigDecimal longitude, BigDecimal latitude) throws InvalidOperation {
		this.streetId = vs.getId();
		this.fullNo = fullNo;
		if (vs.getBuildings().contains(this))
			throw new InvalidOperation(VoError.GeneralError, "The same Building '" + fullNo + "' already exists in Street " + vs.getName());
		users = new ArrayList<VoUser>();
		users = new ArrayList<VoUser>();
		this.longitude = longitude.toPlainString();
		this.latitude = latitude.toPlainString();
		vs.addBuilding(this);
	}

	public String getFullNo() {
		return fullNo;
	}

	public Key getStreetId() {
		return streetId;
	}

	public VoPostalAddress getAddress() {
		return address;
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	@Persistent
	@Unindexed
	private String fullNo; // no with letter or other extension if any

	@Persistent
	private Key streetId;

	@Persistent
	// (mappedBy="building")
	private VoPostalAddress address;

	public List<VoUser> getUsers() {
		return users;
	}

	public void addUser(VoUser user) {
		users.add(user);
	}

	public Key getStreet() {
		return streetId;
	}

	public Key getId() {
		return id;
	}

	@Persistent
	@Unowned
	@Unindexed
	List<VoUser> users;

	public void removeUser(VoUser voUser) {
		users.remove(voUser);
	}

	public Building getBuilding() {
		return new Building(id.getId(), streetId.getId(), fullNo);
	}

	@Override
	public String toString() {
		return "VoBuilding [id=" + id + ", fullNo=" + fullNo + ", streetId=" + streetId + ", address=" + address + ", long=" + longitude + ", lat="
				+ latitude + "]";
	}

	@Override
	public int compareTo(VoBuilding that) {
		return that.streetId == null ? this.streetId == null ? 0 : -1 : Long.compare(this.streetId.getId(), that.streetId.getId()) != 0 ? Long.compare(
				this.streetId.getId(), that.streetId.getId()) : that.fullNo == null ? this.fullNo == null ? 0 : -1 : null == this.fullNo ? 1 : fullNo
				.compareTo(that.fullNo);
	}

	public void setLocation(BigDecimal longitude, BigDecimal latitude) {
		this.longitude = longitude.toPlainString();
		this.latitude = latitude.toPlainString();
	}

	@Persistent
	@Unindexed
	String longitude;

	@Persistent
	@Unindexed
	String latitude;

	public BigDecimal getLongitude() {
		return new BigDecimal(longitude);
	}

	public BigDecimal getLatitude() {
		return new BigDecimal(longitude);
	}

}
