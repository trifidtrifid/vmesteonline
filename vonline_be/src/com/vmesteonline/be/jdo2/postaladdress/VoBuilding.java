package com.vmesteonline.be.jdo2.postaladdress;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.Building;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoBuilding implements Comparable<VoBuilding> {

	public VoBuilding(VoStreet vs, String fullNo, float longitude, float lattutude) throws InvalidOperation {
		this.streetId = vs.getId();
		this.fullNo = fullNo;
		if( vs.getBuildings().contains( this ))
			throw new InvalidOperation(VoError.GeneralError, "The same Building '"+fullNo+"' already exists in Street "+vs.getName());
		users = new ArrayList<VoUser>();
		userGroup = new VoUserGroup(new VoGroup(vs.getName() + " " + fullNo, 0), longitude, lattutude);
		vs.addBuilding(this);
	}
	
	public VoBuilding(Key streetId, String fullNo, float longitude, float lattutude) throws InvalidOperation {
		this.streetId = streetId;
		PersistenceManager pm = PMF.getPm();
		try {
			VoStreet street = pm.getObjectById(VoStreet.class, streetId);
			if (null == street) {
				throw new InvalidOperation(VoError.GeneralError, "Incorrect street Id user in constructor of VoBuilding. streetId=" + streetId);
			}
			this.fullNo = fullNo;
			userGroup = new VoUserGroup(new VoGroup(street.getName() + " " + fullNo, 0), longitude, lattutude);
			street.addBuilding(this);
		} finally {
			pm.close();
		}
		users = new ArrayList<VoUser>();
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

	public VoBuilding(Building building) throws InvalidOperation {
		this( KeyFactory.createKey( VoStreet.class.getSimpleName(), building.getStreetId()), building.getFullNo(), 0F, 0F);
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	@Persistent
	@Unindexed
	private String fullNo; // no with letter or other extension if any

	@Persistent
	private Key streetId;

	@Persistent//(mappedBy="building")
	private VoPostalAddress address;
	
	@Persistent
	@Unowned
	private VoUserGroup userGroup;

	public List<VoUser> getUsers(){
		return users;
	}
	
	public void addUser(VoUser user){
		users.add(user);
	}

	public Key getStreet() {
		return streetId;
	}
	
	public VoUserGroup getUserGroup() {
		return userGroup;
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
		return "VoBuilding [id=" + id + ", fullNo=" + fullNo + ", streetId=" + streetId + ", address=" + address + ", userGroup=" + userGroup + "]";
	}

	@Override
	public int compareTo(VoBuilding that) {
		return that.streetId == null ? this.streetId == null ? 0 : -1 :
			Long.compare(this.streetId.getId(), that.streetId.getId()) != 0 ? Long.compare(this.streetId.getId(), that.streetId.getId()) :
				that.fullNo == null ? this.fullNo == null ? 0 : -1 :
					null == this.fullNo ? 1 : fullNo.compareTo(that.fullNo); 
	}
	
}
