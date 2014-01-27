package com.vmesteonline.be.jdo2.postaladdress;

import java.util.Set;
import java.util.TreeSet;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.data.PMF;

@PersistenceCapable
public class VoStreet {
	
	public VoStreet(VoCity city, String name){
		PersistenceManager pm = PMF.getPm();
		try {
			pm.makePersistent(city);
			this.city = city;
			city.addStreet(this);
			this.name = name;
			this.buildings = new TreeSet<VoBuilding>();
		} finally {
			pm.close();
		}
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	public void setId(Key id){
		
	}
	
	@Persistent
	@Unindexed
	private String name;
	
	@Persistent
	private VoCity city;
	
	public VoCity getCity(){
		return city;
	}

	@Persistent
	@Unowned
	Set<VoBuilding> buildings;
	
	public void addBuilding(VoBuilding building){
		buildings.add(building);
	}
	
	public Key getId(){
		return id;
		}

	public String getName() {
		return name;
	}
}
