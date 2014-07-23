package com.vmesteonline.be.jdo2;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.log4j.Logger;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;

@PersistenceCapable
public class VoInviteCode {

	public VoInviteCode(String code, long postalAddressId) {
		this.code = code;
		this.postalAddressId = postalAddressId;
	}

	public long getPostalAddressId() {
		return postalAddressId;
	}

	public void registered() {
		registeredByCode++;
	}

	public long getId() {
		return id.getId();
	}

	@SuppressWarnings("unchecked")
	public static VoInviteCode getInviteCode(String inviteCode, PersistenceManager pm) throws InvalidOperation {
		Query q = pm.newQuery(VoInviteCode.class);
		q.setFilter("code == '" + inviteCode + "'");
		List<VoInviteCode> voInviteCodes = (List<VoInviteCode>) q.execute();
		if (voInviteCodes.isEmpty())
			throw new InvalidOperation(VoError.IncorrectParametrs, "unknown invite code " + inviteCode);
		if (voInviteCodes.size() != 1) {
			Logger.getLogger(VoInviteCode.class).error("has more than one invite code " + inviteCode);
		}
		return voInviteCodes.get(0);
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
