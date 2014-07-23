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
import com.vmesteonline.be.City;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoCity {
	
	public VoCity(VoCountry country,String name,PersistenceManager pm) throws InvalidOperation {
		List<VoCity> vcl = (List<VoCity>)pm.newQuery(VoCity.class, "countryId=="+country.getId()+" && name=='"+name+"'").execute();
		this.setCountry(country.getId());
		this.setName(name);
		
		if( vcl.size() > 0 ){
			id = vcl.get(0).getId();
			
		} else {
			
			pm.makePersistent(this);
		}
	}
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;
	
	@Persistent
	private String name;

	@Persistent
	private long countryId;
	public long getCountry(){
		return countryId;
	}

	public long getId() {
		return id;
	}

	public City getCity() {
		return new City(id, countryId, name);
	}

	@Override
	public String toString() {
		return "VoCity [id=" + id + ", name=" + name + ", country=" + countryId + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCountry(long country) {
		this.countryId = country;
	}
}
