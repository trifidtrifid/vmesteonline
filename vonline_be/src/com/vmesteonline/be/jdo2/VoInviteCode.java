package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VoInviteCode {

	public VoInviteCode(String code, long postalAddressId) {
		this.code = code;
		this.postalAddressId = postalAddressId;
	}

	public long getId() {
		return id.getId();
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private String code;

	@Persistent
	private long postalAddressId;

	@Persistent
	private int registeredByCode;

	@Override
	public String toString() {
		return "VoInviteCode [id=" + id + ", code=" + code + ", postalAddressId=" + postalAddressId + ", registeredByCode=" + registeredByCode + "]";
	}

}
