package com.vmesteonline.be.jdo2.postaladdress;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.data.PMF;

@PersistenceCapable
public class VoPostalAddress {

	public VoPostalAddress( VoBuilding building, byte staircase, byte floor, byte flatNo, String comment) {
		//TODO lets check that the address is not created yet
		PersistenceManager pm = PMF.getPm();
		try{
			/*Query q = pm.newQuery(VoPostalAddress.class);
			q.setFilter("building == :key");
			q.setFilter("staircase == "+staircase);
			q.setFilter("floor == "+floor);
			q.setFilter("flatNo == "+flatNo);
			List<VoPostalAddress> pal = q.execute(building.getId());*/
			this.building = building;
			this.staircase = staircase;
			this.floor = floor;
			this.flatNo = flatNo;
			this.comment = comment;
			pm.makePersistent(this);
		} finally {
			pm.close();
		}
	}
	
	public VoPostalAddress( PostalAddress postalAddress ) throws InvalidOperation{
		this( new VoBuilding(postalAddress.getBuilding()), postalAddress.getStaircase(), postalAddress.getFloor(),
			postalAddress.getFlatNo(), postalAddress.getComment() );
	}

	private static long valueMask = 26051976L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private VoBuilding building;

	@Persistent
	private byte staircase;

	@Persistent
	private byte floor;

	@Persistent
	private byte flatNo;

	@Persistent
	private String comment;

	public long getAddressCode() {
		return id.getId() ^ valueMask;
	}

	public static Key getKeyValue(long code) {
		return KeyFactory.createKey(VoPostalAddress.class.getSimpleName(), code ^ valueMask);
	}

	public VoBuilding getBuilding() {
		return building;
	}

	public PostalAddress getPostalAddress() {
		Key streetKey = building.getStreet();
		PersistenceManager pm = PMF.getPm();
		VoStreet voStreet = pm.getObjectById(VoStreet.class, streetKey);
		
		return new PostalAddress(voStreet.getCity().getCountry().getCountry(), voStreet.getCity().getCity(), voStreet.getStreet(), building.getBuilding(), staircase, floor, flatNo, comment);
	}

	@Override
	public String toString() {
		return "VoPostalAddress [id=" + id + ", building=" + building + ", staircase=" + staircase + ", floor=" + floor + ", flatNo=" + flatNo + "]";
	}
	
}
