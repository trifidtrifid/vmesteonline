package com.vmesteonline.be.jdo2.shop;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VoDataSnapshot {

	public VoDataSnapshot() {
		// TODO Auto-generated constructor stub
	}
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;

	@Persistent
	private long shopId;
	
	@Persistent
	private int timestamp;
	
	@Persistent
	@Unindexed
	private String  fileName;
	
}
