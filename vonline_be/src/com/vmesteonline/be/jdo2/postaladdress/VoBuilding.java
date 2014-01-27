package com.vmesteonline.be.jdo2.postaladdress;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.Error;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoUserGroup;

@PersistenceCapable
public class VoBuilding {
	
	public VoBuilding(Key streetId, String fullNo, float longitude, float lattutude) throws InvalidOperation{
		this.streetId = streetId;
		PersistenceManager pm = PMF.getPm();
		try {
			VoStreet street = pm.getObjectById(VoStreet.class, streetId);
			if(null==street){
				throw new InvalidOperation(Error.GeneralError, "Incorrect street Id user in constructor of VoBuilding. streetId=" + streetId);
			}
			street.addBuilding(this);
			this.fullNo = fullNo;
			userGroup = new VoUserGroup(new VoGroup(street.getName() + " "+fullNo, 0), longitude, lattutude);
		} finally {
			pm.close();
		}
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private String fullNo; //no with letter or other extension if any

	@Persistent
	private Key streetId;
	
	public Key getStreet(){
		return streetId;
	}
	
	@Persistent
	@Embedded
	private VoUserGroup userGroup;
	
	public VoUserGroup getUserGroup() {
		return userGroup;
	}
}
