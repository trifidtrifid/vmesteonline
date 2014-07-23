package com.vmesteonline.be.jdo2.postaladdress;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.datanucleus.annotations.Unowned;
import com.vmesteonline.be.Building;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.VoUser;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoBuilding {

	public VoBuilding(String zip, VoStreet vs, String fullNo, BigDecimal longitude, BigDecimal latitude, PersistenceManager pm) throws InvalidOperation {
		Query q = pm.newQuery(VoBuilding.class, "streetId=="+vs.getId()+" && fullNo == '"+fullNo+"'");
		List<VoBuilding> bgsl = (List<VoBuilding>)q.execute();
		this.streetId = vs.getId();
		this.fullNo = fullNo;
		this.zipCode = zip;
		
		if( 0!=bgsl.size() ){
			
			VoBuilding oldbg = bgsl.get(0);
			this.setId(oldbg.getId());
			
			longitude = oldbg.getLongitude();
			latitude = oldbg.getLatitude();
			this.addressString = oldbg.getAddressString();
		}
		
		if( null == longitude || longitude.toPlainString().trim().length() == 0 || 
				null == latitude || latitude.toPlainString().trim().length() == 0) {
			VoGeocoder.getPosition(this, false);
			
		} else {
			this.longitude = longitude.toPlainString();
			this.latitude = latitude.toPlainString();
		}
		if( this.addressString == null ) {
			VoStreet street = pm.getObjectById(VoStreet.class, this.streetId);
			VoCity city = pm.getObjectById(VoCity.class, street.getCity());
			VoCountry country = pm.getObjectById(VoCountry.class, city.getCountry());
			
			this.addressString = country.getName() + "," + city.getName() + "," + street.getName() + ","
					+ this.getFullNo();
		}
		pm.makePersistent(this);
	}

	private void setId(long id) {
		this.id = id;
	}

	public String getAddressString() {
		return addressString;
	}

	public String getFullNo() {
		return fullNo;
	}

	public long getStreetId() {
		return streetId;
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Long id;

	@Persistent
	@Unindexed
	private String addressString; 

	@Persistent
	private String fullNo; // no with letter or other extension if any

	@Persistent
	private String zipCode; 
	
	@Persistent
	private long streetId;

	public long getStreet() {
		return streetId;
	}

	public long getId() {
		return id;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public Building getBuilding() {
		return new Building(id, zipCode, streetId, fullNo);
	}

	@Override
	public String toString() {
		return "VoBuilding [id=" + id + ", zipCode=" + zipCode +", fullNo=" + fullNo + ", streetId=" + streetId + ", long=" + longitude + ", lat="
				+ latitude + "]";
	}

	public void setLocation(BigDecimal longitude, BigDecimal latitude) {
		this.longitude = longitude.toPlainString();
		this.latitude = latitude.toPlainString();
	}
	
	
	private static double coordToRad(String coord){
		return Double.parseDouble(coord) * Math.PI / 180.0D;
	}

	//Calculate distance in kilometers between two buildings if all off coordinates are defined
	
	public Double getDistance( VoBuilding that ){
		
		if( null == longitude || null == latitude || 
				null == that || null == that.longitude || null == that.latitude)
			return null;
		
		double lat1 = coordToRad(this.latitude);
		double lat2 = coordToRad(that.latitude);
		double dLat = lat2-lat1;
		double dLon = coordToRad(that.longitude)-coordToRad(this.longitude);
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		return 6371.0D * c;
	}

	@Persistent
	@Unindexed
	String longitude;

	@Persistent
	@Unindexed
	String latitude;

	public BigDecimal getLongitude() {
		return null == longitude ? null : new BigDecimal(longitude);
	}

	public BigDecimal getLatitude() {
		return null == latitude ? null : new BigDecimal(latitude);
	}

	public void setAddressString(String addressString) {
		this.addressString = addressString;
	}

	public void setStreetId(long streetId) {
		this.streetId = streetId;
	}
}
