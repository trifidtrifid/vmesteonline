package com.vmesteonline.be.jdo2.postaladdress;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.Error;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoGroup;
import com.vmesteonline.be.jdo2.VoUser;
import com.vmesteonline.be.jdo2.VoUserGroup;

@PersistenceCapable
public class VoBuilding {

	public VoBuilding(Key streetId, String fullNo, float longitude, float lattutude) throws InvalidOperation {
		this.streetId = streetId;
		PersistenceManager pm = PMF.getPm();
		try {
			VoStreet street = pm.getObjectById(VoStreet.class, streetId);
			if (null == street) {
				throw new InvalidOperation(Error.GeneralError, "Incorrect street Id user in constructor of VoBuilding. streetId=" + streetId);
			}
			street.addBuilding(this);
			
			this.fullNo = fullNo;
			userGroup = new VoUserGroup(new VoGroup(street.getName() + " " + fullNo, 0), longitude, lattutude);
		} finally {
			pm.close();
		}
		users = new ArrayList<VoUser>();
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	@Persistent
	@Unindexed
	private String fullNo; // no with letter or other extension if any

	@Persistent
	private Key streetId;

	@Persistent(mappedBy="building")
	private VoPostalAddress address;
	
	@Persistent
	@Unowned
	private VoUserGroup userGroup;

	public List<VoUser> getUsers(){
		return users;
	}
	
	public void addUser(VoUser user){
		users.add(user);
	}

	public Key getStreet() {
		return streetId;
	}
	
	public VoUserGroup getUserGroup() {
		return userGroup;
	}

	public Key getId() {
		return id;
	}
	
	@Persistent
	@Unowned
	@Unindexed
	List<VoUser> users;

	public void removeUser(VoUser voUser) {
		users.remove(voUser);
	}
}
