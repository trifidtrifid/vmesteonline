package com.vmesteonline.be.jdo2.postaladdress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.City;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoCity implements Comparable<VoCity> {
	
	public VoCity(VoCountry country,String name,PersistenceManager pm) throws InvalidOperation {
		List<VoCity> vcl = (List<VoCity>)pm.newQuery(VoCity.class, "country == :key && name=='"+name+"'").execute(country.getId());
		if( vcl.size() > 0 ){
			id = vcl.get(0).getId();
			
		} else {
			
			this.setCountry(country);
			this.setName(name);
			if( country.getCities().contains( this ))
				throw new InvalidOperation(VoError.GeneralError, "The same City '"+name+"' already exists in contry "+country.getName());
			country.addCity(this);
			this.name = name;
			this.setStreets(new HashSet<VoStreet>());
			pm.makePersistent(this);
		}
		
	}
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private String name;

	@Persistent
	@Unowned
	private VoCountry country;
	public VoCountry getCountry(){
		return country;
	}

	@Persistent//(mappedBy="city")
	@Unowned
	private Set<VoStreet> streets;
	
	public void addStreet(VoStreet street){
		streets.add(street);
	}

	public Key getId() {
		return id;
	}

	public City getCity() {
		return new City(id.getId(), country.getId().getId(), name);
	}

	@Override
	public String toString() {
		return "VoCity [id=" + id + ", name=" + name + ", country=" + country + "]";
	}
	
	@Override
	public int compareTo(VoCity that) {
		return null == that.name ? this.name == null ? 0 : -1 : this.name.compareToIgnoreCase( that.name ) ;
	}

	public String getName() {
		return name;
	}

	public Set<VoStreet> getStreets() {
		return streets;
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof VoStreet && ((VoCity)that).country.getId() == this.country.getId();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCountry(VoCountry country) {
		this.country = country;
	}

	public void setStreets(Set<VoStreet> streets) {
		this.streets = streets;
	}
	
	
	
}
