package com.vmesteonline.be.jdo2.postaladdress;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.jdo2.VoUser;

@PersistenceCapable
public class VoPostalAddress {
	
	public VoPostalAddress( VoBuilding building, int staircase ){
		this.building = building;
		this.staircase = staircase;
		users = new ArrayList<VoUser>();
	}
	
	private static long valueMask = 978654319985431429L;
	
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key id;
  
	@Persistent
	@Unowned
	private VoBuilding building;
	
	@Persistent
	private int staircase;
	
	@Persistent 
	List<VoUser> users;
	
	public long getAddressCode(){
		return id.getId() ^ valueMask;
	}
	
	public static Key getKeyValue( long code ){
		return KeyFactory.createKey(VoPostalAddress.class.getSimpleName(), code ^ valueMask);
	}

	public VoBuilding getBuilding() {
		return building;
	}
}
