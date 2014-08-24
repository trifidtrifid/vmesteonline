package com.vmesteonline.be.jdo2.postaladdress;

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

	private VoPostalAddress(VoBuilding voBuilding, byte staircase, byte floor, int flatNo, String comment) {

		this.buildingId = voBuilding.getId();
		this.staircase = staircase;
		this.floor = floor;
		this.flatNo = flatNo;
		this.comment = comment;

	}

	public VoUserGroup getUserHomeGroup() {
		return userGroup;
	}

	@Override
	public boolean equals(Object that) {
		return that instanceof VoPostalAddress ? ((VoPostalAddress) that).buildingId == buildingId && ((VoPostalAddress) that).flatNo == flatNo : super
				.equals(that);
	}

	@Override
	public String toString() {
		return "VoPostalAddress [id=" + id + ", building=" + buildingId + ", staircase=" + staircase + ", floor=" + floor + ", flatNo=" + flatNo + "]";
	}

	@Override
	public int compareTo(VoPostalAddress that) {
		return 0 == that.buildingId ? this.buildingId == 0 ? 0 : -1 : 0 == this.buildingId ? 1
				: Long.compare(this.buildingId, that.buildingId) != 0 ? Long.compare(this.buildingId, that.buildingId) : Integer.compare(flatNo, that.flatNo);
	}

	public long getAddressCode() {
		return id % 100000L;
	}

	public static Key getKeyValue(long code) {
		return KeyFactory.createKey(VoPostalAddress.class.getSimpleName(), code ^ valueMask);
	}

	public static VoPostalAddress createVoPostalAddress(PostalAddress postalAddress, PersistenceManager _pm) throws InvalidOperation {
		
		if (null == postalAddress)
			throw new InvalidOperation(VoError.IncorrectParametrs, "can't init VoPostalAddress object. Input parametr is null");

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			VoBuilding vob;
			try {
				vob = pm.getObjectById(VoBuilding.class, postalAddress.getBuilding().getId());
			} catch (JDOObjectNotFoundException jonfe) {
				jonfe.printStackTrace();
				throw new InvalidOperation(VoError.IncorrectParametrs, "No building found by ID=" + postalAddress.getBuilding().getId());
			}
				 
			return createVoPostalAddress(vob, postalAddress.getStaircase(), postalAddress.getFloor(), postalAddress.getFlatNo(), 
					postalAddress.getComment(), pm);
			
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	public static VoPostalAddress createVoPostalAddress(VoBuilding voBuilding, byte staircase, byte floor, int flatNo, String comment, PersistenceManager pm) throws InvalidOperation {
		
		Query q = pm.newQuery(VoPostalAddress.class);
		q.setFilter("buildingId=="+voBuilding.getId()+" && staircase==" + staircase + " && floor==" + floor + " && flatNo=="+ flatNo);
		List<VoPostalAddress> pal = (List<VoPostalAddress>) q.execute();
		if (pal.size() == 1) {
			return pal.get(0);
		} else if (pal.size() > 1) 
			throw new InvalidOperation(VoError.GeneralError, "There is two or more the same addresses registered. "+pal.get(0));
			 
		VoPostalAddress voPostalAddress = new VoPostalAddress(voBuilding, staircase, floor, flatNo, comment);
		pm.makePersistent(voPostalAddress);
		pm.flush();
		return voPostalAddress;
	}

	public long getBuilding() {
		return buildingId;
	}

	public PostalAddress getPostalAddress() {
		return getPostalAddress(null);
	}

	public PostalAddress getPostalAddress(PersistenceManager _pm) {

		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try {
			VoBuilding building = pm.getObjectById(VoBuilding.class, buildingId);
			VoStreet street = pm.getObjectById(VoStreet.class, building.getStreetId());
			VoCity city = pm.getObjectById(VoCity.class, street.getCity());
			VoCountry country = pm.getObjectById(VoCountry.class, city.getCountry());

			return new PostalAddress(country.getCountry(), city.getCity(), street.getStreet(), building.getBuilding(), staircase, floor, flatNo, comment);
		} finally {
			if (_pm == null)
				pm.close();
		}
	}

	public String getAddressText(PersistenceManager _pm) {

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			VoBuilding building = pm.getObjectById(VoBuilding.class, buildingId);
			if (null == building.getAddressString() || building.getAddressString().trim().length() == 0) {

				PostalAddress pa = getPostalAddress(pm);
				building.setAddressString(pa.getCity().getName() + " " + pa.getStreet().getName() + " д." + building.getFullNo() + " кв. " + flatNo);
				return building.getAddressString();
			} else {
				return building.getAddressString() + " кв. " + flatNo/* + " [этаж " + floor + " подъезд "+ staircase + "]" */;
			}
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	public long getId() {
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

	public Double getDistance(VoPostalAddress that) {
		PersistenceManager pm = PMF.getPm();
		try {
			try {
				VoBuilding building = pm.getObjectById(VoBuilding.class, buildingId);
				VoBuilding thatBuilding = pm.getObjectById(VoBuilding.class, that.getBuilding());
				return building.getDistance(thatBuilding);
			} catch (Exception e) {
				return null;
			}
		} finally {
			pm.close();
		}
	}

	private static long valueMask = 26051976L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent
	private long buildingId;

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
