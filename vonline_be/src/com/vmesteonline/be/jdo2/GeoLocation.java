package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class GeoLocation {

	public GeoLocation() {
	}

	/*
	 * GeoLocation(float longitude, float latitude) { this.longitude = longitude;
	 * this.latitude = latitude; }
	 */
	public BigDecimal getLongitude() {
		return new BigDecimal(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude.toPlainString();
	}

	public BigDecimal getLatitude() {
		return new BigDecimal(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude.toPlainString();
	}

	public long getId() {
		return id.getId();
	}

	public void setId(Key id) {
		this.id = id;
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key id;

	@Persistent
	@Unindexed
	private String longitude;

	@Persistent
	@Unindexed
	private String latitude;

	public static int R = 6378137;

}
