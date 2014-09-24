package com.vmesteonline.be.jdo2.postaladdress;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.PostalAddress;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUserGroup;

@PersistenceCapable
public class VoPostalAddress implements Comparable<VoPostalAddress> {

	public VoPostalAddress(VoBuilding voBuilding, byte staircase, byte floor, int flatNo, String comment) {

		this.building = voBuilding;
		this.staircase = staircase;
		this.floor = floor;
		this.flatNo = flatNo;
		this.comment = comment;
		BigDecimal staircaseOffset = new BigDecimal("0.0000001");
		staircaseOffset = staircaseOffset.multiply(new BigDecimal(staircase));
		
		//System.out.print("offset: " + staircaseOffset.add(voBuilding.getLongitude()));
		userGroup = new VoUserGroup("Парадная " + staircase, 0, staircaseOffset.add(voBuilding.getLongitude()), voBuilding.getLatitude());

	}

	public VoUserGroup getUserHomeGroup() {
		return userGroup;
	}

	@SuppressWarnings("unchecked")
	public VoPostalAddress(PostalAddress postalAddress, PersistenceManager pm) throws InvalidOperation {
		
		if (null == postalAddress)
			throw new InvalidOperation(VoError.IncorrectParametrs, "can't init VoPostalAddress object. Input parametr is null");

		VoBuilding vob;
		try {
			vob = pm.getObjectById(VoBuilding.class, postalAddress.getBuilding().getId());
		} catch (JDOObjectNotFoundException jonfe) {
			jonfe.printStackTrace();
			throw new InvalidOperation(VoError.IncorrectParametrs, "No building found by ID=" + postalAddress.getBuilding().getId());
		} 
		// check that the address exists
		Query q = pm.newQuery(VoPostalAddress.class);
		q.setFilter("building == :key && staircase == " + postalAddress.getStaircase() + " && floor == " + postalAddress.getFloor() + " && flatNo == "
				+ postalAddress.getFlatNo());
		List<VoPostalAddress> pal = (List<VoPostalAddress>) q.execute(postalAddress.getBuilding().getId());
		if (pal.size() > 0) {
			this.id = pal.get(0).id;
		}
		pm.retrieve(vob);
		
		this.building = vob;
		this.building.longitude = vob.getLongitude().toString();
		this.building.latitude = vob.getLatitude().toString();
		this.staircase = postalAddress.getStaircase();
		this.floor = postalAddress.getFloor();
		this.flatNo = postalAddress.getFlatNo();
		this.comment = postalAddress.getComment();
		pm.makePersistent(this);
		
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof VoPostalAddress ? ((VoPostalAddress) that).building.getId() == building.getId()
				&& ((VoPostalAddress) that).flatNo == flatNo : super.equals(that);
	}

	@Override
	public String toString() {
		return "VoPostalAddress [id=" + id + ", building=" + building + ", staircase=" + staircase + ", floor=" + floor + ", flatNo=" + flatNo + "]";
	}

	@Override
	public int compareTo(VoPostalAddress that) {
		return null == that.building ? this.building == null ? 0 : -1 : null == this.building ? 1 : Long.compare(this.building.getId().getId(),
				that.building.getId().getId()) != 0 ? Long.compare(this.building.getId().getId(), that.building.getId().getId()) : Integer.compare(flatNo,
				that.flatNo);
	}

	public long getAddressCode() {
		return id.getId() ^ valueMask;
	}

	public static Key getKeyValue(long code) {
		return KeyFactory.createKey(VoPostalAddress.class.getSimpleName(), code ^ valueMask);
	}

	public VoBuilding getBuilding() {
		return building;
	}

	public PostalAddress getPostalAddress(PersistenceManager pm) {
		Key streetKey = building.getStreet();
		VoStreet voStreet = pm.getObjectById(VoStreet.class, streetKey);
		return new PostalAddress(voStreet.getCity().getCountry().getCountry(), voStreet.getCity().getCity(), voStreet.getStreet(),
				building.getBuilding(), staircase, floor, flatNo, comment);
	
	}

	public String getAddressText(PersistenceManager pm) {
		if( null == building.getAddressString() || building.getAddressString().trim().length() == 0 ) {
			PostalAddress pa = getPostalAddress(pm);
			building.setAddressString( pa.getCity().getName() + " " + pa.getStreet().getName() + " д." + building.getFullNo() + " кв. " + flatNo + " [э." + floor + " п. "
					+ staircase + "]");
			return building.getAddressString();
		
		} else {
			return building.getAddressString() + " кв. " + flatNo;
		}
	}

	public Key getId() {
		return id;
	}

	public byte getStaircase() {
		return staircase;
	}

	public byte getFloor() {
		return floor;
	}

	public int getFlatNo() {
		return flatNo;
	}

	public String getComment() {
		return comment;
	}

	public Double getDistance( VoPostalAddress that){
		if( null == this.getBuilding() || null == that.getBuilding() )
			return null;
		return this.getBuilding().getDistance(that.getBuilding());
	}
	
	private static long valueMask = 26051976L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	@Unowned
	private VoBuilding building;

	@Persistent
	private byte staircase;

	@Persistent
	private byte floor;

	@Persistent
	private int flatNo;

	@Persistent
	private String comment;

	@Persistent
	@Unowned
	private VoUserGroup userGroup;

}
