package com.vmesteonline.be.jdo2.postaladdress;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class VoDistance {

	public VoDistance(Long a, Long b, int d) {
		buildingA = a;
		buildingB = b;
		distance = d;
	}

	public Key getId() {
		return id;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public Long getBuildingA() {
		return buildingA;
	}

	public void setBuildingA(Long buildingA) {
		this.buildingA = buildingA;
	}

	public Long getBuildingB() {
		return buildingB;
	}

	public void setBuildingB(Long buildingB) {
		this.buildingB = buildingB;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;

	private Long buildingA;
	private Long buildingB;
	private Integer distance;

}
