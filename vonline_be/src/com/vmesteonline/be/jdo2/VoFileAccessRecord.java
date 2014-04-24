package com.vmesteonline.be.jdo2;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.tools.cloudstorage.GcsFilename;

@PersistenceCapable
public class VoFileAccessRecord {

	public VoFileAccessRecord( long userId, boolean isPublic, String fileName, String contentType) {
		this.fileName = ""+(System.currentTimeMillis() % 10000) +"_"+fileName.replaceAll("[^A-Za-z0-9._]", "");
		if(this.fileName.length() > 32 ) this.fileName = this.fileName.substring(1,32);
		this.bucket = ""+(this.userId=userId) + "_" + ((this.isPublic=isPublic) ? "public" : "private");
		this.contentType = contentType;
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
		return new GcsFilename(bucket, fileName);
	}

	public String getContentType() {
		return contentType;
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
	
	@Persistent
	@Unindexed
	private String contentType;
	
	
	
}
