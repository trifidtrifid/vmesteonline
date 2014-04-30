package com.vmesteonline.be.jdo2.postaladdress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.Street;
import com.vmesteonline.be.VoError;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoStreet implements Comparable<VoStreet> {

	public VoStreet(VoCity city, String name, PersistenceManager pm) throws InvalidOperation {
		List<VoStreet> vcl = (List<VoStreet>)pm.newQuery(VoStreet.class, "city == :key && name=='"+name+"'").execute(city.getId());
		this.setCity(city);
		this.setName(name);
		
		if( vcl.size() > 0 ){
			id = vcl.get(0).getId();
			this.setBuildings(vcl.get(0).getBuildings());
			
		} else {
			
			if( city.getStreets().contains( this ))
				throw new InvalidOperation(VoError.GeneralError, "The same Street '"+name+"' already exists in City "+city.getName());
			city.addStreet(this);
			this.setBuildings(new HashSet<VoBuilding>());
			
			pm.makePersistent(this);
		}
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	public void setId(Key id) {
		this.id = id;
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
	
	

	public void setName(String name) {
		this.name = name;
	}

	public void setCity(VoCity city) {
		this.city = city;
	}

	public void setBuildings(Set<VoBuilding> buildings) {
		this.buildings = buildings;
	}

	@Override
	public int compareTo(VoStreet that) {
		return null == that.name ? this.name == null ? 0 : -1 : this.name.compareToIgnoreCase( that.name ) ;
	}

	public Set<VoBuilding> getBuildings() {
		return buildings;
	}
}
