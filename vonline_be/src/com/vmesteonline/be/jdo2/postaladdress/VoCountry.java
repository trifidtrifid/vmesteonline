package com.vmesteonline.be.jdo2.postaladdress;

import java.util.Set;
import java.util.TreeSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.Country;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoCountry implements Comparable<VoCountry> {
	
	public VoCountry(String name){
		this.name = name;
		this.cities = new TreeSet<VoCity>(); 
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private String name;
	
	@Persistent//(mappedBy = "country")
	@Unowned
	private Set<VoCity> cities;
	
	public void addCity(VoCity city){
		cities.add(city);
	}

	public Key getId() {
		return id;
	}

	public Country getCountry() {
		return new Country(id.getId(), name);
	}

	@Override
	public String toString() {
		return "VoCountry [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int compareTo(VoCountry that) {
		return null == that.name ? this.name == null ? 0 : -1 : this.name.compareToIgnoreCase( that.name ) ;
	}

	public String getName() {
		return name;
	}

	public Set<VoCity> getCities() {
		return cities;
	}
	
	
}
