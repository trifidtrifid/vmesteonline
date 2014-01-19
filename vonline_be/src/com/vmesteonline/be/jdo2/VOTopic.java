package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VOTopic {
	// id, messageId, messageNum, viewers, usersNum, lastUpdate, likes, unlikes, rubricId
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	
	@Persistent
	@Unindexed
	private long messageId;
	
	@Persistent
	@Unindexed
	private int messageNum;
	
	@Persistent
	@Unindexed
	private int viewers;
	
	@Persistent
	@Unindexed
	private int usersNum;
	
	@Persistent
	private int lastUpdate;
	
	@Persistent
	@Unindexed
	private int likes;
	
	@Persistent
	@Unindexed
	private int unlikes;
	
	@Persistent
	@Unindexed
	private long rubricId;
}
