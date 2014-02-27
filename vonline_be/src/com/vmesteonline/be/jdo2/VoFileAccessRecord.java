package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.tools.cloudstorage.GcsFilename;

@PersistenceCapable
public class VoFileAccessRecord {

	public VoFileAccessRecord( long userId, boolean isPublic, String fileName) {
		this.fileName = fileName;
		this.bucket = ""+(this.userId=userId) + "/" + Math.random()+"/" + (this.isPublic=isPublic);
	}
	
	public long getId() {
		return id;
	}

	public long getUserId() {
		return userId;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public GcsFilename getFileName() {
		return new GcsFilename(bucket,  fileName);
	}

	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private long id;
	
	@Persistent
	private long userId;
	
	@Persistent
	private boolean isPublic;
	
	@Persistent
	@Unindexed
	private String bucket;
	
	@Persistent
	@Unindexed
	private String fileName;
	
}
