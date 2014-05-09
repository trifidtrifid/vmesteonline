package com.vmesteonline.be.jdo2;

import java.util.HashMap;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.mail.internet.ContentType;
import com.google.appengine.api.datastore.Key;

import com.google.appengine.datanucleus.annotations.Unindexed;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.vmesteonline.be.utils.StorageHelper;

@PersistenceCapable
public class VoFileAccessRecord {

	private String gcsFileName;


	public VoFileAccessRecord( long userId, boolean isPublic, String fileName, String contentType, String versionKey, VoFileAccessRecord parent) {
		this.gcsFileName = (this.userId=userId) + "_" + ((this.isPublic=isPublic) ? "public" : "private")+(System.currentTimeMillis() % 10000) +"_"+fileName.replaceAll("[^A-Za-z0-9._]", "");
		if(this.gcsFileName.length() > 128) this.gcsFileName = this.gcsFileName.substring(1,128);
		this.fileName=fileName;
		this.bucket = "vmesteonline.appspot.com";
		this.contentType = contentType;
		this.createdAt = (int)(System.currentTimeMillis() / 1000L); 
		if( null != parent ) parent.setVersion(versionKey, this);
	}
	
	public VoFileAccessRecord( long userId, boolean isPublic, String fileName, String contentType) {
		this( userId, isPublic, fileName, contentType, null, null ); 
	}
	
	public long getId() {
		return id.getId();
	}

	public long getUserId() {
		return userId;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public String getFileName(){
		return fileName == null ? gcsFileName : fileName;
	}
	public GcsFilename getGSFileName() {
		return new GcsFilename(bucket, gcsFileName);
	}

	
	public String getContentType() {
		return contentType;
	}


	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	private int createdAt;
	
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
	
	@Unindexed
	@Persistent(dependentElement = "true")
	Map<String,VoFileAccessRecord> versions;
	

	public VoFileAccessRecord getVersion(Map<String, String[]> params, PersistenceManager p) {
		return getVersion(params,p,true);
	}
	
	private VoFileAccessRecord getVersion(Map<String, String[]> params, PersistenceManager pm, boolean createIfNotExists) {
		if( null!=contentType){
			try {
				ContentType ct = new ContentType(contentType);
				if( null !=  ct){
					VersionCreator vc  = StorageHelper.getVersionCreator( this, ct, pm );
					if( null != vc ){
						return vc.createParametrizedVersion( params, createIfNotExists);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}
	
	public static interface VersionCreator {
		VoFileAccessRecord createParametrizedVersion(Map<String, String[]> params, boolean createIfNotExists);
	}

	public VoFileAccessRecord getVersion(String string) {
		return null==versions ? null : versions.get(string);
	}

	public VoFileAccessRecord setVersion(String versionKey, VoFileAccessRecord ver) {
		if(null==versions)
			versions = new HashMap<String,VoFileAccessRecord>(); 
		versions.put( versionKey, ver);
		return ver;
	}
}
