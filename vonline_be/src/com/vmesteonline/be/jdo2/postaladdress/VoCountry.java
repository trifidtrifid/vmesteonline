package com.vmesteonline.be.jdo2.postaladdress;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.Country;

@PersistenceCapable
public class VoCountry {
	
	public VoCountry(String name){
		this.name = name;
		this.cities = new ArrayList<VoCity>(); 
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private String name;
	
	@Persistent(mappedBy = "country")
	private List<VoCity> cities;
	
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
}
