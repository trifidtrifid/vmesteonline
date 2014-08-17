package com.vmesteonline.be.jdo2;

import java.math.BigDecimal;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
public abstract class GeoLocation {

	public GeoLocation() {
		longitude = "0";
		latitude = "0";
	}

	/*
	 * GeoLocation(float longitude, float latitude) { this.longitude = longitude; this.latitude = latitude; }
	 */
	public BigDecimal getLongitude() {
		return null == longitude ? null : new BigDecimal(longitude);
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = null == longitude ? null : longitude.toPlainString();
	}

	public BigDecimal getLatitude() {
		return null == latitude ? null : new BigDecimal(latitude);
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = null == latitude ? null : latitude.toPlainString();
	}

	public long getId() {
		return id.getId();
	}

	public void setId(long id) {
		this.id = 0==id ? null : KeyFactory.createKey(this.getClass().getSimpleName(), id);
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key id;

	@Persistent
	private String longitude;

	@Persistent
	private String latitude;
}
