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
import com.vmesteonline.be.Country;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoCountry implements Comparable<VoCountry> {
	
	public VoCountry(String name, PersistenceManager pm){
		List<VoCountry> vcl = (List<VoCountry>)pm.newQuery(VoCountry.class, "name=='"+name+"'").execute();
		if( vcl.size() > 0 ){
			this.id = vcl.get(0).getId();
			this.name = vcl.get(0).getName();
			this.cities = vcl.get(0).getCities();
		} else {
			this.setName(name);
			this.setCities(new HashSet<VoCity>());
			pm.makePersistent(this);
		}
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private String name;
	
	@Persistent//(mappedBy = "country")
	@Unowned
	private Set<VoCity> cities;
	
	
	public void setName(String name) {
		this.name = name;
	}

	public void setCities(Set<VoCity> cities) {
		this.cities = cities;
	}

	public void addCity(VoCity city){
		if(null==cities) cities = new HashSet<VoCity>();
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
		if(null==cities) cities = new HashSet<VoCity>();
		return cities;
	}
	
	
}
