package com.vmesteonline.be.jdo2.postaladdress;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.City;
import com.vmesteonline.be.data.PMF;

@PersistenceCapable
public class VoCity {
	
	public VoCity(VoCountry country,String name){
		PersistenceManager pm = PMF.getPm();
		try {
			pm.makePersistent(country);
			this.country = country;
			country.addCity(this);
			this.name = name;
			this.streets = new ArrayList<VoStreet>();
		} finally {
			pm.close();
		}
	}
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private String name;

	@Persistent
	private VoCountry country;
	public VoCountry getCountry(){
		return country;
	}

	@Persistent(mappedBy="city")
	private List<VoStreet> streets;
	
	public void addStreet(VoStreet street){
		streets.add(street);
	}

	public Key getId() {
		return KeyFactory.createKey(VoCity.class.getSimpleName(), name);
	}

	public City getCity() {
		return new City(id.getId(), country.getId().getId(), name);
	}

	@Override
	public String toString() {
		return "VoCity [id=" + id + ", name=" + name + ", country=" + country + "]";
	}
	
	
}
