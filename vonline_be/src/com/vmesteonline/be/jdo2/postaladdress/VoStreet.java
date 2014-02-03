package com.vmesteonline.be.jdo2.postaladdress;

import java.util.Set;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Street;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoStreet implements Comparable<VoStreet> {

	public VoStreet(VoCity city, String name) throws InvalidOperation {
		this.city = city;
		this.name = name;
		if( city.getStreets().contains( this ))
			throw new InvalidOperation(VoError.GeneralError, "The same Street '"+name+"' already exists in City "+city.getName());
		city.addStreet(this);
		this.name = name;
		this.buildings = new TreeSet<VoBuilding>();
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	public void setId(Key id) {

	}

	@Persistent
	private String name;

	@Persistent
	@Unowned
	private VoCity city;

	public VoCity getCity() {
		return city;
	}

	@Persistent
	@Unowned
	Set<VoBuilding> buildings;

	public void addBuilding(VoBuilding building) {
		buildings.add(building);
	}

	public Key getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Street getStreet() {
		return new Street(id.getId(), city.getId().getId(), name);
	}

	@Override
	public String toString() {
		return "VoStreet [id=" + id + ", name=" + name + ", city=" + city + "]";
	}

	@Override
	public int compareTo(VoStreet that) {
		return null == that.name ? this.name == null ? 0 : -1 : this.name.compareToIgnoreCase( that.name ) ;
	}

	public Set<VoBuilding> getBuildings() {
		return buildings;
	}
}
