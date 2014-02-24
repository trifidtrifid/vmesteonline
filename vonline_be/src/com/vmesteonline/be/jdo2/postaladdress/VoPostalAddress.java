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

@PersistenceCapable
public class VoPostalAddress implements Comparable<VoPostalAddress> {

	public VoPostalAddress(VoBuilding building, byte staircase, byte floor, byte flatNo, String comment) {
		this(building, staircase, floor, flatNo, comment, null);
	}

	public VoPostalAddress(VoBuilding building, byte staircase, byte floor, byte flatNo, String comment, PersistenceManager _pm) {
		// TODO lets check that the address is not created yet
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			/*
			 * Query q = pm.newQuery(VoPostalAddress.class);
			 * q.setFilter("building == :key");
			 * q.setFilter("staircase == "+staircase); q.setFilter("floor == "+floor);
			 * q.setFilter("flatNo == "+flatNo); List<VoPostalAddress> pal =
			 * q.execute(building.getId());
			 */
			this.building = building;
			this.staircase = staircase;
			this.floor = floor;
			this.flatNo = flatNo;
			this.comment = comment;
			// pm.makePersistent(this);
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	@SuppressWarnings("unchecked")
	public VoPostalAddress(PostalAddress postalAddress, PersistenceManager _pm) throws InvalidOperation {
		if (null == postalAddress)
			return;

		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
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
			this.building = vob;
			this.staircase = postalAddress.getStaircase();
			this.floor = postalAddress.getFloor();
			this.flatNo = postalAddress.getFlatNo();
			this.comment = postalAddress.getComment();
			pm.makePersistent(this);
		} finally {
			if (null == _pm)
				pm.close();
		}
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

	public PostalAddress getPostalAddress() {
		return getPostalAddress(null);
	}
	public PostalAddress getPostalAddress(PersistenceManager _pm) {
		Key streetKey = building.getStreet();
		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try {
			VoStreet voStreet = pm.getObjectById(VoStreet.class, streetKey);
			return new PostalAddress(voStreet.getCity().getCountry().getCountry(), voStreet.getCity().getCity(), voStreet.getStreet(),
					building.getBuilding(), staircase, floor, flatNo, comment);
		} finally {
			if(_pm ==null) pm.close();
		}
	}
	
	public String getAddressText( PersistenceManager _pm ){
		PersistenceManager pm = null==_pm ? PMF.getPm() : _pm;
		try {
			PostalAddress pa = getPostalAddress(pm);
			return pa.getCity().getName() + " " + pa.getStreet().getName() + " д." + building.getFullNo() +" кв."+
					flatNo + " [э." + floor + " п. "+staircase+"]";
		} finally {
			if( null==_pm )pm.close();
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

}
