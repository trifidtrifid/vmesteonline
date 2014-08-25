package com.vmesteonline.be.jdo2;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VoInitKey {
	
	public static VoInitKey getVoInitKey( PersistenceManager pm) {
		List<VoInitKey> kl = (List<VoInitKey>) pm.newQuery(VoInitKey.class, "").execute();
		if( 0==kl.size() ){
			VoInitKey vik = new VoInitKey();
			pm.makePersistent(vik);
			return vik;
		}
		return kl.get(0);
	}
	private VoInitKey(){
		resetCode();
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private long code;
	
	public String getCode(){
		return ""+code;
	}
	public String resetCode(){
		return ""+(code = System.currentTimeMillis() % 123456L);
	}
}
