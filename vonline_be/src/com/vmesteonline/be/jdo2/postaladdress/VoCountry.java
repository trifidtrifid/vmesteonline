package com.vmesteonline.be.jdo2.postaladdress;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.vmesteonline.be.Country;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoCountry {
	
	public VoCountry(String name, PersistenceManager pm){
		List<VoCountry> vcl = (List<VoCountry>)pm.newQuery(VoCountry.class, "name=='"+name+"'").execute();
		if( vcl.size() > 0 ){
			this.id = vcl.get(0).getId();
			this.name = vcl.get(0).getName();
		} else {
			this.setName(name);
			pm.makePersistent(this);
		}
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;
	
	@Persistent
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public Country getCountry() {
		return new Country(id, name);
	}

	@Override
	public String toString() {
		return "VoCountry [id=" + id + ", name=" + name + "]";
	}

	public String getName() {
		return name;
	}
}
