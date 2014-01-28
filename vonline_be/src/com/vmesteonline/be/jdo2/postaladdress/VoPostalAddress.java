package com.vmesteonline.be.jdo2.postaladdress;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class VoPostalAddress {

	public VoPostalAddress(VoBuilding building, int staircase, int floor, int flatNo, String comment) {
		this.building = building;
		this.staircase = staircase;
		this.floor = floor;
		this.flatNo = flatNo;
		this.comment = comment;
	}

	private static long valueMask = 26051976L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private VoBuilding building;

	@Persistent
	private int staircase;

	@Persistent
	private int floor;

	@Persistent
	private int flatNo;

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
}
