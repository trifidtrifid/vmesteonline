package com.vmesteonline.be.access;

import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unindexed;

@PersistenceCapable
public class VoUserAccessBase {
	
	public static final long SYS_ADMIN = -1L;
	public static final long ACCOUNT_MANAGER = 1L;
	
	public void setAccessPermission( long flag, boolean value ){
		permissionBits = value ? permissionBits | flag : permissionBits & -flag;
	}
	public boolean getAccessPermission( long flag ) {
		return (permissionBits & flag) == permissionBits;
	}
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@PrimaryKey
	private Key id;
	
	@Persistent
	protected long userId;
	
	//category of access to, like SHOP, FORUM etc
	@Persistent
	protected long categoryId; 
	
	@Persistent
	protected Set<String> methodNames;
	
	//the first 16 bits should be used as a general access permission flags
	//second 16 bits - as a functionality specific
	@Persistent
	@Unindexed
	private long permissionBits;
}
