package com.vmesteonline.be.jdo2;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.vmesteonline.be.GroupType;
@PersistenceCapable

public class VoUsersRelation {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key id;

	@Persistent
	List<Long> users;
	
	@Persistent
	GroupType groupType;
}
